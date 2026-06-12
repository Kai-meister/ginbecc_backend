package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request.ChangePasswordRequest;
import gov.kh.mcr.inspectorate.dto.request.LoginRequest;
import gov.kh.mcr.inspectorate.dto.response.LoginResponse;
import gov.kh.mcr.inspectorate.dto.response.ResetPasswordResponse;
import gov.kh.mcr.inspectorate.entity.User;
import gov.kh.mcr.inspectorate.enums.NotificationType;
import gov.kh.mcr.inspectorate.enums.UserStatusCode;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.exception.UnauthorizedException;
import gov.kh.mcr.inspectorate.repository.RolePermissionRepository;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import gov.kh.mcr.inspectorate.security.JwtTokenProvider;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.AuthService;
import gov.kh.mcr.inspectorate.service.NotificationService;
import gov.kh.mcr.inspectorate.util.PasswordGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager           authManager;
    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermRepo;
    private final JwtTokenProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;
    private final RedisTemplate<String, Object>   redisTemplate;
    private final NotificationService notificationService;

    private static final int  MAX_ATTEMPTS = 5;
    private static final long LOCK_MINUTES = 30L;

    // Fix ក្នុង login() — pass context

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "អាសយដ្ឋានអ៊ីមែល ឬពាក្យសម្ងាត់មិនត្រឹមត្រូវឡើយ"));

        validateAccountStatus(user);
        checkAccountLocked(user);

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (BadCredentialsException ex) {
            handleFailedLogin(user);
            throw new UnauthorizedException(
                    "អាសយដ្ឋានអ៊ីមែល ឬពាក្យសម្ងាត់មិនត្រឹមត្រូវឡើយ");
        }

        user.setFailedLoginCount(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        List<String> permissions =
                rolePermRepo.findPermissionNamesByRoleId(
                        user.getRole().getRoleId());

        String accessToken =
                jwtProvider.generateAccessToken(
                        user.getEmail(), user.getUserId(),
                        user.getRole().getRoleName(),
                        permissions);

        String refreshToken =
                jwtProvider.generateRefreshToken(
                        user.getEmail());

        ActivityLogContext context =
                ActivityLogContext.builder()
                        .userId(user.getUserId())
                        .userEmail(user.getEmail())
                        .ipAddress(extractIpFromRequest())
                        .userAgent(extractUserAgent())
                        .build();

        activityLogService.log(
                "LOGIN", "User",
                user.getUserId(),
                "ចូលប្រព័ន្ធ: " + user.getEmail(),
                context);                     // ← Fix

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .userNameKh(user.getUserNameKh())
                .userNameEn(user.getUserNameEn())
                .email(user.getEmail())
                .roleName(user.getRole().getRoleName())
                .permissions(permissions)
                .mustChangePassword(
                        user.getMustChangePassword())
                .build();
    }

    private String extractIpFromRequest() {
        try {
            HttpServletRequest req =
                    ((ServletRequestAttributes)
                            RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();

            String ip = req.getHeader(
                    "X-Forwarded-For");
            if (ip != null && !ip.isBlank()) {
                return ip.split(",")[0].trim();
            }
            ip = req.getHeader("X-Real-IP");
            if (ip != null && !ip.isBlank()) {
                return ip;
            }
            return req.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String extractUserAgent() {
        try {
            HttpServletRequest req =
                    ((ServletRequestAttributes)
                            RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();
            return req.getHeader("User-Agent");
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public LoginResponse refreshToken(
            String refreshToken) {

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException(
                    "ថូខិន (Refresh Token) មិនត្រឹមត្រូវ "
                            + "ឬអស់សុពលភាពហើយ");
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + refreshToken))) {
            throw new UnauthorizedException(
                    "ថូខិន (Refresh Token) ត្រូវបានលុបចោលហើយ");
        }

        String email =
                jwtProvider.getEmailFromToken(refreshToken);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "ថូខិន (Refresh Token) ត្រូវបានលុបចោលហើយ"));

        validateAccountStatus(user);

        List<String> permissions =
                rolePermRepo.findPermissionNamesByRoleId(
                        user.getRole().getRoleId());

        return LoginResponse.builder()
                .accessToken(
                        jwtProvider.generateAccessToken(
                                user.getEmail(),
                                user.getUserId(),
                                user.getRole().getRoleName(),
                                permissions))
                .refreshToken(
                        jwtProvider.generateRefreshToken(
                                user.getEmail()))
                .userId(user.getUserId())
                .userNameKh(user.getUserNameKh())
                .userNameEn(user.getUserNameEn())
                .email(user.getEmail())
                .roleName(user.getRole().getRoleName())
                .permissions(permissions)
                .mustChangePassword(
                        user.getMustChangePassword())
                .build();
    }

    @Override
    public void changePassword(Integer userId,
                               ChangePasswordRequest req) {

        User user = findUserById(userId);

        if (!passwordEncoder.matches(
                req.getOldPassword(),
                user.getPasswordHash())) {
            throw new BusinessException(
                    "ពាក្យសម្ងាត់ចាស់មិនត្រឹមត្រូវ");
        }

        if (passwordEncoder.matches(
                req.getNewPassword(),
                user.getPasswordHash())) {
            throw new BusinessException(
                    "ពាក្យសម្ងាត់ថ្មីត្រូវខុសពីចាស់");
        }

        if (!req.getNewPassword()
                .equals(req.getConfirmPassword())) {
            throw new BusinessException(
                    "ពាក្យសម្ងាត់ថ្មីមិនដូចគ្នា");
        }

        user.setPasswordHash(
                passwordEncoder.encode(
                        req.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);

        activityLogService.log(
                "CHANGE_PASSWORD", "User",
                userId,
                "ផ្លាស់ប្ដូរ ពាក្យសម្ងាត់: "
                        + user.getEmail());

        log.info("Password changed: {}",
                user.getEmail());
    }
    @Override
    public ResetPasswordResponse resetPassword(Integer userId) {

        User user = findUserById(userId);

        String plainPassword =
                PasswordGenerator.generate(12);

        user.setPasswordHash(
                passwordEncoder.encode(plainPassword));

        user.setMustChangePassword(true);

        user.setFailedLoginCount(0);
        user.setLockedUntil(null);

        userRepository.save(user);

        activityLogService.log(
                "RESET_PASSWORD", "User",
                userId,
                "ការកំណត់ពាក្យសម្ងាត់សារជាថ្មីដោយអ្នកគ្រប់គ្រងប្រព័ន្ធ៖"
                        + user.getEmail());

        log.info("Password reset by admin: {}",
                user.getEmail());

        notificationService.createByUserId(
                user.getUserId(),
                "ការប្រកាសប្រព័ន្ធ",
                "Password ត្រូវបាន Reset",
                NotificationType.SYSTEM,  // ← Enum
                null);

        return ResetPasswordResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userNameKh(user.getUserNameKh())
                .temporaryPassword(plainPassword)
                .mustChangePassword(true)
                .message(
                        "ពាក្យសម្ងាត់នេះអាចប្រើប្រាស់បានតែម្តងគត់ (One-time Password) "
                                + "សូមធ្វើការផ្លាស់ប្តូរពាក្យសម្ងាត់ថ្មីជាបន្ទាន់")
                .build();
    }

    @Override
    public void logout(String token) {
        if (!jwtProvider.validateToken(token)) {
            return;
        }

        long ttl = jwtProvider.getRemainingExpiry(token);
        if (ttl > 0) {
            redisTemplate.opsForValue()
                    .set("blacklist:" + token, "1",
                            ttl, TimeUnit.MILLISECONDS);
            log.debug("Token blacklisted, TTL: {}ms",
                    ttl);
        }

        try {
            String email =
                    jwtProvider.getEmailFromToken(token);
            userRepository.findByEmail(email)
                    .ifPresent(u ->
                            activityLogService.log(
                                    "LOGOUT", "User",
                                    u.getUserId(),
                                    "ចាកចេញ: " + email));
        } catch (Exception ex) {
            log.warn("Logout log error: {}",
                    ex.getMessage());
        }
    }



    private User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User", id));
    }

    private void validateAccountStatus(User user) {
        if (user.getStatusCode() == null) return;

        String code =
                user.getStatusCode().getStatusCode();

        if (UserStatusCode.isLoginBlocked(code)) {
            String reason =
                    switch (code) {
                        case "BLOCKED" ->
                                "គណនីត្រូវបានបិទ — "
                                        + "ទំនាក់ទំនងអ្នកគ្រប់គ្រង";
                        case "SUSPENDED" ->
                                "គណនីត្រូវបានផ្អាក";
                        case "INACTIVE" ->
                                "គណនីមិនសកម្ម";
                        case "LOCKED" ->
                                "គណនីត្រូវបានចាក់សោ — "
                                        + "ព្យាយាមម្ដងទៀតក្រោយ "
                                        + LOCK_MINUTES + " នាទី";
                        default ->
                                "មិនអាចចូលប្រព័ន្ធ";
                    };
            throw new UnauthorizedException(reason);
        }
    }

    private void checkAccountLocked(User user) {
        if (user.getLockedUntil() != null
                && user.getLockedUntil()
                .isAfter(LocalDateTime.now())) {
            throw new UnauthorizedException(
                    "គណនីត្រូវបានចាក់សោ — "
                            + "ព្យាយាមម្ដងទៀត"
                            + " " + LOCK_MINUTES
                            + " នាទីក្រោយ");
        }
    }

    private void handleFailedLogin(User user) {
        int count = user.getFailedLoginCount() + 1;
        user.setFailedLoginCount(count);

        if (count >= MAX_ATTEMPTS) {
            user.setLockedUntil(
                    LocalDateTime.now()
                            .plusMinutes(LOCK_MINUTES));
            log.warn(
                    "Account locked after {} fails: {}",
                    count, user.getEmail());
        }
        userRepository.save(user);
    }
    private final
    jakarta.servlet.http.HttpServletRequest httpRequest;
}