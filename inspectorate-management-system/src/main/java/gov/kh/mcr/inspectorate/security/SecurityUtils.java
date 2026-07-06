package gov.kh.mcr.inspectorate.security;

import gov.kh.mcr.inspectorate.dto.request
        .ActivityLogContext;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.repository.DepartmentRepository;
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
    private final DepartmentRepository departmentRepository;
    public Optional<User> getCurrentUser() {
        Authentication auth = getAuth();
        if (!isAuthenticated(auth)) {
            return Optional.empty();
        }
        return userRepository
                .findByEmail(auth.getName());
    }

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



    public ContractOfficer
    getCurrentContractOfficerOrNull() {
        return getCurrentUser()
                .map(User::getContractOfficer)
                .orElse(null);
    }

    public boolean isContractOfficer() {
        return getCurrentUser()
                .map(u ->
                        u.getContractOfficer() != null)
                .orElse(false);
    }

    public boolean hasPermission(
            String permission) {
        Authentication auth = getAuth();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority()
                                .equals(permission));
    }

    public boolean isAdmin() {
        return getCurrentUser()
                .map(u -> {
                    String role =
                            u.getRole() != null
                                    ? u.getRole().getRoleName()
                                    : "";
                    return "ADMIN".equals(role)
                            || "SUPER_ADMIN"
                            .equals(role);
                })
                .orElse(false);
    }


    public boolean isManager() {
        return getCurrentUser()
                .map(u ->
                        u.getRole() != null
                                && "MANAGER".equals(
                                u.getRole().getRoleName()))
                .orElse(false);
    }

    public Integer getCurrentDepartmentId() {
        return getCurrentUser()
                .map(u -> {
                    if (u.getOfficer() != null
                            && u.getOfficer()
                            .getDepartment()
                            != null) {
                        return u.getOfficer()
                                .getDepartment()
                                .getDepartmentId();
                    }
                    if (u.getContractOfficer()
                            != null
                            && u.getContractOfficer()
                            .getDepartment()
                            != null) {
                        return u.getContractOfficer()
                                .getDepartment()
                                .getDepartmentId();
                    }
                    return null;
                })
                .orElse(null);
    }

    public boolean canBypassDepartmentScope() {
        return isAdmin();
    }


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

    public ActivityLogContext buildLogContext(
            HttpServletRequest request) {
        ActivityLogContext
                .ActivityLogContextBuilder
                builder = ActivityLogContext
                .builder();

        getCurrentUser().ifPresent(user -> {
            builder.userId(user.getUserId());
            builder.userEmail(
                    user.getEmail());
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

    private boolean hasText(String s) {
        return s != null && !s.isBlank()
                && !"unknown"
                .equalsIgnoreCase(s);
    }
    public void validateDepartmentScope(
            Integer targetDepartmentId) {

        if (canBypassDepartmentScope()) {
            return;

        }

        Integer currentDeptId =
                getCurrentDepartmentId();


        if (currentDeptId == null) {
            return;
        }

        if (targetDepartmentId == null
                || !currentDeptId.equals(
                targetDepartmentId)) {

            String ownDeptName =
                    resolveDepartmentName(
                            currentDeptId);
            String targetDeptName =
                    targetDepartmentId != null
                            ? resolveDepartmentName(
                            targetDepartmentId)
                            : "មិនស្គាល់";

            throw new
                    DepartmentScopeException(
                    ownDeptName,
                    targetDeptName);
        }
    }
    private String resolveDepartmentName(
            Integer departmentId) {
        return departmentRepository
                .findById(departmentId)
                .map(Department
                        ::getDepartmentName)
                .orElse("ID:" + departmentId);
    }
}