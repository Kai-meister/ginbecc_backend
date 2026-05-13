package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.RoleRequest;
import gov.kh.mcr.inspectorate.dto.response.RoleResponse;
import gov.kh.mcr.inspectorate.entity.Role;
import gov.kh.mcr.inspectorate.entity.RolePermission;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.RoleMapper;
import gov.kh.mcr.inspectorate.repository.PermissionRepository;
import gov.kh.mcr.inspectorate.repository.RolePermissionRepository;
import gov.kh.mcr.inspectorate.repository.RoleRepository;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleMapper roleMapper;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(Integer id) {
        return toResponse(findById(id));
    }

    @Override
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new DuplicateResourceException(
                    "តួនាទី [" + request.getRoleName()
                            + "] មានស្ទួន");
        }

        Role saved = roleRepository.save(
                roleMapper.toEntity(request));

        assignPermissions(saved, request.getPermissionIds());

        activityLogService.log("CREATE", "Role",
                saved.getRoleId(),
                "បង្កើតតួនាទី: " + saved.getRoleName());

        return toResponse(saved);
    }

    @Override
    public RoleResponse update(Integer id, RoleRequest request) {
        Role role = findById(id);
        roleMapper.updateEntity(request, role);
        roleRepository.save(role);

        if (request.getPermissionIds() != null) {
            rolePermissionRepository.deleteByRoleId(id);
            assignPermissions(role, request.getPermissionIds());
        }

        activityLogService.log("UPDATE", "Role",
                id, "កែប្រែ: " + role.getRoleName());

        return toResponse(role);
    }

    private void assignPermissions(Role role,
                                   List<Integer> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) return;

        permissionIds.forEach(permId ->
                permissionRepository.findById(permId)
                        .ifPresent(perm ->
                                rolePermissionRepository.save(
                                        RolePermission.builder()
                                                .role(role)
                                                .permission(perm)
                                                .build())));
    }

    private RoleResponse toResponse(Role r) {
        RoleResponse dto = roleMapper.toResponse(r);
        dto.setPermissions(
                rolePermissionRepository
                        .findPermissionNamesByRoleId(r.getRoleId()));
        return dto;
    }

    private Role findById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("តួនាទី", id));
    }
}