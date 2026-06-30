package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository
        extends JpaRepository<Department, Integer> {

    List<Department> findByStatus(
            ActiveStatus status);

    List<Department>
    findByDepartmentNameContainingIgnoreCase(
            String keyword);

    List<Department>
    findByStatusAndDepartmentNameContainingIgnoreCase(
            ActiveStatus status, String keyword);

    List<Department>
    findAllByOrderByDepartmentNameAsc();

    Optional<Department>
    findByDepartmentCode(String code);

    boolean existsByDepartmentCode(String code);


    Optional<Department>
    findByDepartmentIdAndStatus(
            Integer id, ActiveStatus status);

}