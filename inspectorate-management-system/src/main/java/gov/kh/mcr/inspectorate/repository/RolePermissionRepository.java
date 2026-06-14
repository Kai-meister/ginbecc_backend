package gov.kh.mcr.inspectorate.repository;


import gov.kh.mcr.inspectorate.entity.RolePermission;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {

    @Query("""
        SELECT rp.permission.permissionName
        FROM RolePermission rp
        WHERE rp.role.roleId = :roleId
        """)
    List<String> findPermissionNamesByRoleId(
            @Param("roleId") Integer roleId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Integer roleId);
}
