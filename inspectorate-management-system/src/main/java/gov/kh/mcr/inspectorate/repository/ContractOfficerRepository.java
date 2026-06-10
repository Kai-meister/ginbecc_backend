package gov.kh.mcr.inspectorate.repository;
import gov.kh.mcr.inspectorate.entity.ContractOfficer;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractOfficerRepository
        extends JpaRepository<ContractOfficer,
        Integer> {

    boolean existsByContractOfficerCode(String code);

    Page<ContractOfficer>
    findByStatusCode_StatusCode(
            String status, Pageable pageable);
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
}