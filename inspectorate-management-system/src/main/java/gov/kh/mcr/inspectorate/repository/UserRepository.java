package gov.kh.mcr.inspectorate.repository;
import gov.kh.mcr.inspectorate.entity.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUuid(String uuid);

    Optional<User> findByOfficer_OfficerId(
            Integer officerId);

    boolean existsByEmail(String email);

    Page<User> findByRole_RoleId(
            Integer roleId, Pageable pageable);

    Page<User> findByStatusCode_StatusCode(
            String status, Pageable pageable);

    Page<User>
    findByRole_RoleIdAndStatusCode_StatusCode(
            Integer roleId,
            String status,
            Pageable pageable);

    Page<User>
    findByUserNameKhContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nameKeyword,
            String emailKeyword,
            Pageable pageable);

    List<User> findByRole_RoleName(
            String roleName);
}