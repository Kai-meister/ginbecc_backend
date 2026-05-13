package gov.kh.mcr.inspectorate.security;

import gov.kh.mcr.inspectorate.entity.User;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public Optional<User> getCurrentUser() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return userRepository.findByEmail(auth.getName());
    }

    public Integer getCurrentUserId() {
        return getCurrentUser()
                .map(User::getUserId)
                .orElse(null);
    }

    public String getCurrentEmail() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return auth.getName();
    }

    public boolean hasPermission(String permission) {
        Authentication auth = getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }

    private Authentication getAuthentication() {
        try {
            return SecurityContextHolder
                    .getContext()
                    .getAuthentication();
        } catch (Exception e) {
            return null;
        }
    }
}