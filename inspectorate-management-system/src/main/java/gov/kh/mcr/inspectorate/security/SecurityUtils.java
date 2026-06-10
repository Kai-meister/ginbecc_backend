package gov.kh.mcr.inspectorate.security;

import gov.kh.mcr.inspectorate.dto.request
        .ActivityLogContext;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.repository
        .UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core
        .Authentication;
import org.springframework.security.core.context
        .SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    // ── Get current User ──────────────────────────
    public Optional<User> getCurrentUser() {
        Authentication auth = getAuth();
        if (!isAuthenticated(auth)) {
            return Optional.empty();
        }
        return userRepository
                .findByEmail(auth.getName());
    }

    // ── Get current User ID ───────────────────────
    public Integer getCurrentUserId() {
        return getCurrentUser()
                .map(User::getUserId)
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "ត្រូវ Login"));
    }

    public String getCurrentEmail() {
        Authentication auth = getAuth();
        return isAuthenticated(auth)
                ? auth.getName()
                : null;
    }

    // ── Get permanent Officer ─────────────────────
    // Throw if no officer
    public Officer getCurrentOfficer() {
        User user = getCurrentUser()
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "ត្រូវ Login"));

        if (user.getOfficer() == null) {
            throw new BusinessException(
                    "Account មិនភ្ជាប់ Officer");
        }

        return user.getOfficer();
    }

    // ── Get permanent Officer or null ─────────────
    // null = Admin / ContractOfficer
    public Officer getCurrentOfficerOrNull() {
        return getCurrentUser()
                .map(User::getOfficer)
                .orElse(null);
    }

    // Fix ── Get Contract Officer ──────────────────
    // Throw if no contract officer
    public ContractOfficer
    getCurrentContractOfficer() {

        User user = getCurrentUser()
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "ត្រូវ Login"));

        if (user.getContractOfficer() == null) {
            throw new BusinessException(
                    "Account មិនភ្ជាប់"
                            + " Contract Officer");
        }

        return user.getContractOfficer();
    }

    // Fix ── Get Contract Officer or null ──────────
    // null = Admin / permanent Officer
    public ContractOfficer
    getCurrentContractOfficerOrNull() {
        return getCurrentUser()
                .map(User::getContractOfficer)
                .orElse(null);
    }

    // Fix ── Check is any Officer type ────────────
    // true = Officer OR ContractOfficer
    public boolean isOfficerType() {
        return getCurrentUser()
                .map(u ->
                        u.getOfficer() != null
                                || u.getContractOfficer() != null)
                .orElse(false);
    }

    // Fix ── Check is Contract Officer ────────────
    public boolean isContractOfficer() {
        return getCurrentUser()
                .map(u ->
                        u.getContractOfficer() != null)
                .orElse(false);
    }

    // ── Check has permission ──────────────────────
    public boolean hasPermission(
            String permission) {
        Authentication auth = getAuth();
        if (auth == null) return false;
        return auth.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority()
                                .equals(permission));
    }

    // ── Check is Admin ────────────────────────────
    public boolean isAdmin() {
        return getCurrentUser()
                .map(u -> {
                    String role =
                            u.getRole() != null
                                    ? u.getRole().getRoleName()
                                    : "";
                    return "ADMIN".equals(role)
                            || "SUPER_ADMIN".equals(role);
                })
                .orElse(false);
    }

    // ── Check is SuperAdmin ───────────────────────
    public boolean isSuperAdmin() {
        return getCurrentUser()
                .map(u ->
                        u.getRole() != null
                                && "SUPER_ADMIN".equals(
                                u.getRole().getRoleName()))
                .orElse(false);
    }

    // ── Build ActivityLogContext ───────────────────
    public ActivityLogContext buildLogContext(
            HttpServletRequest request) {

        ActivityLogContext
                .ActivityLogContextBuilder builder =
                ActivityLogContext.builder();

        getCurrentUser().ifPresent(user -> {
            builder.userId(user.getUserId());
            builder.userEmail(user.getEmail());
        });

        if (request != null) {
            builder.ipAddress(
                    extractIp(request));
            builder.userAgent(
                    request.getHeader(
                            "User-Agent"));
        }

        return builder.build();
    }

    // ── Extract IP ────────────────────────────────
    public String extractIp(
            HttpServletRequest request) {

        String ip = request.getHeader(
                "X-Forwarded-For");
        if (hasText(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (hasText(ip)) return ip;
        return request.getRemoteAddr();
    }

    // ── Private ───────────────────────────────────
    private Authentication getAuth() {
        try {
            return SecurityContextHolder
                    .getContext()
                    .getAuthentication();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isAuthenticated(
            Authentication auth) {
        return auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(
                auth.getPrincipal());
    }

    private boolean hasText(String s) {
        return s != null
                && !s.isBlank()
                && !"unknown"
                .equalsIgnoreCase(s);
    }
}