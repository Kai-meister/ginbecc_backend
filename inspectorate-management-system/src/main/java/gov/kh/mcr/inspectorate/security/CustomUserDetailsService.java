package gov.kh.mcr.inspectorate.security;


import gov.kh.mcr.inspectorate.repository.RolePermissionRepository;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermRepo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        var user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User រកមិនឃើញ: " + email));

        boolean locked =
                user.getLockedUntil() != null
                        && user.getLockedUntil()
                        .isAfter(LocalDateTime.now());


        boolean enabled =
                user.getStatusCode() == null
                        || "ACTIVE".equals(
                        user.getStatusCode().getStatusCode());


        var permissions =
                rolePermRepo.findPermissionNamesByRoleId(
                        user.getRole().getRoleId());

        var authorities = Stream.concat(
                permissions.stream()
                        .map(SimpleGrantedAuthority::new),
                Stream.of(
                        new SimpleGrantedAuthority(
                                "ROLE_"
                                        + user.getRole().getRoleName()))
        ).collect(Collectors.toList());

        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .accountLocked(locked)
                .disabled(!enabled)
                .build();
    }
}
