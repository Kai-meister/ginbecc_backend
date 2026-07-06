package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.DepartmentManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentManagerRepository extends JpaRepository
        <DepartmentManager, Integer> {

    List<DepartmentManager>
    findByDepartment_DepartmentId(
            Integer departmentId);


    Optional<DepartmentManager>
    findByDepartment_DepartmentIdAndIsPrimaryTrue(
            Integer departmentId);

    boolean
    existsByDepartment_DepartmentIdAndUser_UserId(
            Integer departmentId,
            Integer userId);
    boolean
    existsByDepartment_DepartmentIdAndUser_UserIdAndDepartment_DepartmentId(
            Integer departmentId,
            Integer userId,
            Integer departmentId2);

    List<DepartmentManager>
    findByUser_UserId(
            Integer userId);

    void
    deleteByDepartment_DepartmentIdAndUser_UserId(
            Integer departmentId,
            Integer userId);
}
