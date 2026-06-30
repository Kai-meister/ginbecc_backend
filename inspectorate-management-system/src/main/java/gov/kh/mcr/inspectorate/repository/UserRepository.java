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


    boolean existsByOfficer_OfficerId(
            Integer officerId);
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
    boolean
    existsByContractOfficer_ContractOfficerId(
            Integer contractOfficerId);

    boolean
    existsByContractOfficer_ContractOfficerIdAndUserIdNot(
            Integer contractOfficerId,
            Integer userId);

    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.officer o
    LEFT JOIN FETCH u.contractOfficer co
    WHERE u.statusCode.statusCode = 'ACTIVE'
    AND (
        (o IS NOT NULL
         AND o.department.departmentId
             = :deptId)
        OR
        (co IS NOT NULL
         AND co.department.departmentId
             = :deptId)
    )
    ORDER BY u.userNameKh ASC
    """)
    List<User> findActiveByDepartmentId(
            @Param("deptId") Integer deptId);

    // All active users (SUPER_ADMIN use case)
    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.officer o
    LEFT JOIN FETCH u.contractOfficer co
    LEFT JOIN FETCH u.role r
    WHERE u.statusCode.statusCode = 'ACTIVE'
    ORDER BY r.roleName   ASC,
             u.userNameKh ASC
    """)
    List<User> findAllActive();
    // Existing — keep
    Optional<User> findByOfficer_OfficerId(
            Integer officerId);

    // Add for contract officer lookup
    Optional<User>
    findByContractOfficer_ContractOfficerId(
            Integer contractOfficerId);

    @Query("""
    SELECT u FROM User u
    WHERE u.statusCode.statusCode = 'ACTIVE'
    AND (
        u.officer IS NOT NULL
        AND u.officer.department.departmentId
            IN :deptIds
        OR
        u.contractOfficer IS NOT NULL
        AND u.contractOfficer.department
            .departmentId IN :deptIds
    )
    ORDER BY u.userNameKh ASC
    """)
    List<User> findActiveByDepartments(
            @Param("deptIds")
            List<Integer> deptIds);

    // Active users by IDs
    @Query("""
    SELECT u FROM User u
    WHERE u.statusCode.statusCode = 'ACTIVE'
    AND   u.userId IN :userIds
    """)
    List<User> findActiveByIds(
            @Param("userIds") List<Integer> userIds);
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