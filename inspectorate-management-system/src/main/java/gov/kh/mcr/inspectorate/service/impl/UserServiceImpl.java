package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper.UserMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password
        .PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request
        .RequestContextHolder;
import org.springframework.web.context.request
        .ServletRequestAttributes;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl
        implements UserService {

    private final UserRepository             userRepo;
    private final OfficerRepository          officerRepo;
    private final RoleRepository             roleRepo;
    private final LookupUserStatusRepository statusRepo;
    private final RolePermissionRepository   rolePermRepo;
    private final UserMapper                 userMapper;
    private final PasswordEncoder            passwordEncoder;
    private final SecurityUtils              securityUtils;
    private final ActivityLogService         activityLogService;


    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAll(
            int page, int size,
            Integer roleId,
            String status,
            String keyword) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<User> result;

        if (StringUtils.hasText(keyword)) {
            result = userRepo
                    .findByUserNameKhContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            keyword, keyword, pageable);
        } else if (roleId != null
                && status != null) {
            result = userRepo
                    .findByRole_RoleIdAndStatusCode_StatusCode(
                            roleId, status, pageable);
        } else if (roleId != null) {
            result = userRepo
                    .findByRole_RoleId(
                            roleId, pageable);
        } else if (status != null) {
            result = userRepo
                    .findByStatusCode_StatusCode(
                            status, pageable);
        } else {
            result = userRepo.findAll(pageable);
        }

        return PageResponse.of(
                result.map(userMapper::toResponse));
    }

    // ─────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Integer id) {
        return userMapper.toResponse(findById(id));
    }


    @Override
    @Transactional(readOnly = true)
    public List<String> getPermissions(
            Integer id) {
        User user = findById(id);
        return rolePermRepo
                .findPermissionNamesByRoleId(
                        user.getRole().getRoleId());
    }


    @Override
    public UserResponse create(
            UserRequest request) {

        // Check email duplicate
        if (userRepo.existsByEmail(
                request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email ["
                            + request.getEmail()
                            + "] មានស្ទួន");
        }

        // Check officer already linked
        if (request.getOfficerId() != null
                && userRepo.existsByOfficer_OfficerId(
                request.getOfficerId())) {
            throw new BusinessException(
                    "មន្ត្រីនេះ"
                            + " មាន Account រួចហើយ");
        }

        User user = userMapper.toEntity(request);

        // Fix — Set officer
        user.setOfficer(
                resolveOfficer(
                        request.getOfficerId()));

        // Set role
        user.setRole(
                findRole(request.getRoleId()));

        // Set status
        user.setStatusCode(
                findStatus(request.getStatusCode()));

        // Set default password
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword()
                ));
        user.setMustChangePassword(true);
        user.setFailedLoginCount(0);

        User saved = userRepo.save(user);

        activityLogService.log(
                "CREATE", "User",
                saved.getUserId(),
                "បង្កើត: " + saved.getEmail(),
                buildContext());

        return userMapper.toResponse(saved);
    }

    // ─────────────────────────────────────────────
    // UPDATE — Fix officer link
    // ─────────────────────────────────────────────
    @Override
    public UserResponse update(
            Integer id,
            UserRequest request) {

        User user = findById(id);

        // Check email duplicate (exclude self)
        if (!user.getEmail()
                .equals(request.getEmail())
                && userRepo.existsByEmail(
                request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email ["
                            + request.getEmail()
                            + "] មានស្ទួន");
        }

        // Fix — Check officer duplicate
        // (exclude current user)
        if (request.getOfficerId() != null
                && userRepo
                .existsByOfficer_OfficerIdAndUserIdNot(
                        request.getOfficerId(), id)) {
            throw new BusinessException(
                    "មន្ត្រីនេះ"
                            + " ភ្ជាប់ Account ផ្សេងរួចហើយ");
        }

        // Update basic fields
        userMapper.updateEntity(request, user);

        // Fix — Update officer manually
        user.setOfficer(
                resolveOfficer(
                        request.getOfficerId()));

        // Update role
        user.setRole(
                findRole(request.getRoleId()));

        // Update status
        user.setStatusCode(
                findStatus(request.getStatusCode()));

        activityLogService.log(
                "UPDATE", "User",
                id,
                "កែប្រែ: " + user.getEmail(),
                buildContext());

        return userMapper.toResponse(
                userRepo.save(user));
    }

    // ─────────────────────────────────────────────
    // UPDATE STATUS
    // ─────────────────────────────────────────────
    @Override
    public UserResponse updateStatus(
            Integer id,
            StatusRequest request) {

        User user = findById(id);

        user.setStatusCode(
                findStatus(request.getStatusCode()));

        activityLogService.log(
                "UPDATE", "User",
                id,
                "ស្ថានភាព → "
                        + request.getStatusCode(),
                buildContext());

        return userMapper.toResponse(
                userRepo.save(user));
    }

    // ─────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────
    @Override
    public void delete(Integer id) {

        User user = findById(id);

        // Block delete self
        Integer currentId =
                securityUtils.getCurrentUserId();
        if (id.equals(currentId)) {
            throw new BusinessException(
                    "មិនអាចលុប Account ខ្លួនឯង");
        }

        userRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "User",
                id, "លុប: " + user.getEmail(),
                buildContext());
    }

    // ── Private Helpers ───────────────────────────

    private User findById(Integer id) {
        return userRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User", id));
    }

    // Fix — resolveOfficer
    // null = Admin (no officer linked)
    private Officer resolveOfficer(
            Integer officerId) {
        if (officerId == null) return null;
        return officerRepo.findById(officerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រី", officerId));
    }

    private Role findRole(Integer roleId) {
        return roleRepo.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role", roleId));
    }

    private LookupUserStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ស្ថានភាព User", code));
    }

    private ActivityLogContext buildContext() {
        try {
            var req =
                    ((ServletRequestAttributes)
                            RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();
            return securityUtils
                    .buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext.builder()
                    .build();
        }
    }
}