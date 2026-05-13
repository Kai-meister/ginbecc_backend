package gov.kh.mcr.inspectorate.repository;
import gov.kh.mcr.inspectorate.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository
        extends JpaRepository<Permission, Integer> {

    List<Permission> findByModule(String module);


    List<Permission>
    findAllByOrderByModuleAscPermissionNameAsc();


    List<Permission>
    findByModuleOrderByPermissionNameAsc(
            String module);

    Optional<Permission> findByPermissionName(
            String name);
}