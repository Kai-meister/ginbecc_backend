package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Officer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OfficerRepository
        extends JpaRepository<Officer, Integer>,
        JpaSpecificationExecutor<Officer> {

    boolean existsByOfficerCode(String officerCode);
    boolean existsByEmail(String email);

    Page<Officer> findByDepartment_DepartmentIdAndStatusCode_StatusCode(
            Integer deptId, String status, Pageable pageable);

    Page<Officer> findByStatusCode_StatusCode(
            String status, Pageable pageable);


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
}