package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Officer;
import gov.kh.mcr.inspectorate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfficerRepository extends JpaRepository<Officer, Integer>,
        JpaSpecificationExecutor<Officer> {

    boolean existsByOfficerCode(String officerCode);
//    boolean existsByEmail(String email);

    Page<Officer> findByDepartment_DepartmentIdAndStatusCode_StatusCode(
            Integer deptId, String status, Pageable pageable);

    Page<Officer> findByStatusCode_StatusCode(
            String status, Pageable pageable);

    long countByDepartment_DepartmentId(
            Integer departmentId);

    Page<Officer> findByDepartment_DepartmentId(
            Integer deptId, Pageable pageable);

    @Query("""

            SELECT o FROM Officer o
        WHERE o.dob <= :retirementDate
        AND o.statusCode.statusCode = 'ACTIVE'
        ORDER BY o.dob ASC
        """)
    List<Officer> findNearRetirement(
            @Param("retirementDate") LocalDate retirementDate);

    @Query("""
        SELECT o.gender, COUNT(o)
        FROM Officer o
        WHERE o.statusCode.statusCode = 'ACTIVE'
        GROUP BY o.gender
        """)
    List<Object[]> countByGender();

    @Query("""
        SELECT d.departmentName, COUNT(o)
        FROM Officer o
        JOIN o.department d
        GROUP BY d.departmentName
        ORDER BY COUNT(o) DESC
        """)
    List<Object[]> countByDepartment();
// for auto recipient

    // All ACTIVE officers
    @Query("""
    SELECT o FROM Officer o
    LEFT JOIN FETCH o.department d
    WHERE o.statusCode.statusCode = 'ACTIVE'
    ORDER BY d.departmentName ASC,
             o.fullNameKh     ASC
    """)
    List<Officer> findAllActive();

    // Active officers by departments
    @Query("""
    SELECT o FROM Officer o
    LEFT JOIN FETCH o.department d
    WHERE o.statusCode.statusCode = 'ACTIVE'
    AND   d.departmentId IN :deptIds
    ORDER BY d.departmentName ASC,
             o.fullNameKh     ASC
    """)
    List<Officer> findActiveByDepartments(
            @Param("deptIds")
            List<Integer> deptIds);

    // Active officers by IDs
    @Query("""
    SELECT o FROM Officer o
    WHERE o.statusCode.statusCode = 'ACTIVE'
    AND   o.officerId IN :ids
    """)
    List<Officer> findActiveByIds(
            @Param("ids") List<Integer> ids);

    // ═══ OfficerRepository.java — Report query ═══
    @Query("""
    SELECT o FROM Officer o
    LEFT JOIN FETCH o.department d
    LEFT JOIN FETCH o.position   p
    LEFT JOIN FETCH o.statusCode s
    WHERE (:deptId IS NULL
           OR d.departmentId = :deptId)
    AND   (:status IS NULL
           OR s.statusCode = :status)
    AND   (:from IS NULL
           OR o.joinDate >= :from)
    AND   (:to IS NULL
           OR o.joinDate <= :to)
    ORDER BY d.departmentName ASC,
             o.fullNameKh     ASC
    """)
    List<Officer> findForReport(
            @Param("deptId") Integer deptId,
            @Param("status") String status,
            @Param("from")   LocalDate from,
            @Param("to")     LocalDate to);

    // Single officer with all relations eagerly fetched
    @Query("""
    SELECT o FROM Officer o
    LEFT JOIN FETCH o.department d
    LEFT JOIN FETCH o.position   p
    LEFT JOIN FETCH o.statusCode s
    LEFT JOIN FETCH o.profileAttachment a
    WHERE o.officerId = :id
    """)
    Optional<Officer> findByIdWithAll(
            @Param("id") Integer id);

    // Paged list with optional department/status filters
    @Query(value = """
    SELECT o FROM Officer o
    WHERE (:deptId IS NULL
           OR o.department.departmentId = :deptId)
    AND   (:status IS NULL
           OR o.statusCode.statusCode = :status)
    """, countQuery = """
    SELECT COUNT(o) FROM Officer o
    WHERE (:deptId IS NULL
           OR o.department.departmentId = :deptId)
    AND   (:status IS NULL
           OR o.statusCode.statusCode = :status)
    """)
    Page<Officer> findAllWithFilters(
            @Param("deptId") Integer deptId,
            @Param("status") String status,
            Pageable pageable);
    }