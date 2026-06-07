package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query
        .Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    //  find by officer
    Optional<User> findByOfficer_OfficerId(
            Integer officerId);

    //  check officer already linked
    boolean existsByOfficer_OfficerId(
            Integer officerId);

    // check officer already linked
    // exclude current user (for update)
    boolean
    existsByOfficer_OfficerIdAndUserIdNot(
            Integer officerId, Integer userId);

    Page<User> findByRole_RoleId(
            Integer roleId, Pageable pageable);

    Page<User>
    findByStatusCode_StatusCode(
            String status, Pageable pageable);

    Page<User>
    findByRole_RoleIdAndStatusCode_StatusCode(
            Integer roleId,
            String status,
            Pageable pageable);

    Page<User>
    findByUserNameKhContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email,
            Pageable pageable);

    // Report
    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.role       r
        LEFT JOIN FETCH u.statusCode s
        LEFT JOIN FETCH u.officer    o
        WHERE (:roleId IS NULL
               OR r.roleId = :roleId)
        AND   (:status IS NULL
               OR s.statusCode = :status)
        ORDER BY r.roleName   ASC,
                 u.userNameKh ASC
        """)
    List<User> findForReport(
            @Param("roleId") Integer roleId,
            @Param("status") String status);
}