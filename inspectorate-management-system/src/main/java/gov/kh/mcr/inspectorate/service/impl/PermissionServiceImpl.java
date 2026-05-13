package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.entity.Permission;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.repository.PermissionRepository;
import gov.kh.mcr.inspectorate.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionServiceImpl
        implements PermissionService {

    private final PermissionRepository
            permissionRepository;

    @Override
    public List<Permission> getAll() {
        return permissionRepository
                .findAllByOrderByModuleAscPermissionNameAsc();
    }

    @Override
    public Permission getById(Integer id) {
        return permissionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "សិទ្ធិ", id));
    }

    @Override
    public List<Permission> getByModule(
            String module) {
        return permissionRepository
                .findByModuleOrderByPermissionNameAsc(
                        module);
    }
}