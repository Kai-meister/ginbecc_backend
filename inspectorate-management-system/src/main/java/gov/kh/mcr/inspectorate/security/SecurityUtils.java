package gov.kh.mcr.inspectorate.security;
import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.entity.Officer;
import gov.kh.mcr.inspectorate.entity.User;
import gov.kh.mcr.inspectorate.exception.UnauthorizedException;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public Optional<User> getCurrentUser() {
        Authentication auth = getAuth();
        if (!isAuthenticated(auth)) {
            return Optional.empty();
        }
        return userRepository
                .findByEmail(auth.getName());
    }

//    public Integer getCurrentUserId() {
//        return getCurrentUser()
//                .map(User::getUserId)
//                .orElse(null);
//    }
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

    public Officer getCurrentOfficerOrNull() {
        return getCurrentUser()
                .map(User::getOfficer)
                .orElse(null);
    }
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


    public ActivityLogContext buildLogContext(
            HttpServletRequest request) {

        ActivityLogContext.ActivityLogContextBuilder
                builder = ActivityLogContext.builder();

        getCurrentUser().ifPresent(user -> {
            builder.userId(user.getUserId());
            builder.userEmail(user.getEmail());
        });

        // IP + UserAgent — from HTTP Request
        if (request != null) {
            builder.ipAddress(
                    extractIp(request));
            builder.userAgent(
                    request.getHeader("User-Agent"));
        }

        return builder.build();
    }

    public String extractIp(
            HttpServletRequest request) {

        // Check X-Forwarded-For (Nginx proxy)
        String ip = request.getHeader(
                "X-Forwarded-For");
        if (hasText(ip)) {
            return ip.split(",")[0].trim();
        }

        // Check X-Real-IP
        ip = request.getHeader("X-Real-IP");
        if (hasText(ip)) return ip;

        // Fallback to remote address
        return request.getRemoteAddr();
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

    private boolean hasText(String s) {
        return s != null
                && !s.isBlank()
                && !"unknown".equalsIgnoreCase(s);
    }
}