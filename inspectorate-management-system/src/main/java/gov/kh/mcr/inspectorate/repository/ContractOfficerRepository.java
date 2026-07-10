package gov.kh.mcr.inspectorate.repository;
import gov.kh.mcr.inspectorate.entity.ContractOfficer;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractOfficerRepository
        extends JpaRepository<ContractOfficer,
        Integer> {

    boolean existsByContractOfficerCode(String code);

    @Query("""
    SELECT c FROM ContractOfficer c
    WHERE c.contractOfficerId = :id
    AND   c.endDate <= :expiryDate
    AND   c.statusCode.statusCode = 'ACTIVE'
    ORDER BY c.endDate ASC
    """)
    List<ContractOfficer>
    findByContractOfficerIdAndExpiring(@Param("id") Integer id, @Param("expiryDate") LocalDate expiryDate);

    Page<ContractOfficer> findByStatusCode_StatusCode(String status, Pageable pageable);
    long countByDepartment_DepartmentId(Integer departmentId);

    Page<ContractOfficer>
    findByDepartment_DepartmentId(
            Integer deptId, Pageable pageable);



    Page<ContractOfficer>
    findByDepartment_DepartmentIdAndStatusCode_StatusCode(
            Integer deptId,
            String status,
            Pageable pageable);


    @Query("""
        SELECT c FROM ContractOfficer c
        WHERE c.endDate <= :expiryDate
        AND   c.statusCode.statusCode = 'ACTIVE'
        ORDER BY c.endDate ASC
        """)
    List<ContractOfficer> findExpiring(
            @Param("expiryDate")
            LocalDate expiryDate);

    // Single contract officer with all relations eagerly fetched
    @Query("""
    SELECT c FROM ContractOfficer c
    LEFT JOIN FETCH c.department d
    LEFT JOIN FETCH c.statusCode s
    LEFT JOIN FETCH c.profileAttachment a
    WHERE c.contractOfficerId = :id
    """)
    Optional<ContractOfficer> findByIdWithAll(
            @Param("id") Integer id);

    // Paged list with optional department/status filters
    @Query(value = """
    SELECT c FROM ContractOfficer c
    WHERE (:deptId IS NULL
           OR c.department.departmentId = :deptId)
    AND   (:status IS NULL
           OR c.statusCode.statusCode = :status)
    """, countQuery = """
    SELECT COUNT(c) FROM ContractOfficer c
    WHERE (:deptId IS NULL
           OR c.department.departmentId = :deptId)
    AND   (:status IS NULL
           OR c.statusCode.statusCode = :status)
    """)
    Page<ContractOfficer> findAllWithFilters(
            @Param("deptId") Integer deptId,
            @Param("status") String status,
            Pageable pageable);
}