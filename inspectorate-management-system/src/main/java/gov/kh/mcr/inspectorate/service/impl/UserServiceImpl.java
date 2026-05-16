package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.request.UserRequest;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.dto.response.UserResponse;
import gov.kh.mcr.inspectorate.entity.LookupUserStatus;
import gov.kh.mcr.inspectorate.entity.Role;
import gov.kh.mcr.inspectorate.entity.User;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.UserMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OfficerRepository officerRepository;
    private final LookupUserStatusRepository lookupStatusRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAll(
            int page, int size,
            Integer roleId,
            String status,
            String keyword) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<User> result;

        if (roleId != null && status != null) {
            result = userRepository
                    .findByRole_RoleIdAndStatusCode_StatusCode(
                            roleId, status, pageable);
        } else if (roleId != null) {
            result = userRepository
                    .findByRole_RoleId(
                            roleId, pageable);
        } else if (status != null) {
            result = userRepository
                    .findByStatusCode_StatusCode(
                            status, pageable);
        } else if (org.springframework.util
                .StringUtils.hasText(keyword)) {
            result = userRepository
                    .findByUserNameKhContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            keyword, keyword, pageable);
        } else {
            result = userRepository.findAll(pageable);
        }

        return PageResponse.of(
                result.map(userMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Integer id) {
        return userMapper.toResponse(findById(id));
    }

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email [" + request.getEmail() + "] មានស្ទួន");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "តួនាទី", request.getRoleId()));

        LookupUserStatus status =
                lookupStatusRepository.findById(request.getStatusCode())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "ស្ថានភាព", request.getStatusCode()));

        User user = User.builder()
                .uuid(UUID.randomUUID().toString())
                .userNameKh(request.getUserNameKh())
                .userNameEn(request.getUserNameEn())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(
                        passwordEncoder.encode(request.getPassword()))
                .role(role)
                .statusCode(status)
                .build();

        if (request.getOfficerId() != null) {
            officerRepository.findById(request.getOfficerId())
                    .ifPresent(user::setOfficer);
        }

        securityUtils.getCurrentUser()
                .ifPresent(user::setCreatedBy);

        User saved = userRepository.save(user);
        activityLogService.log("CREATE", "User",
                saved.getUserId(),
                "បង្កើត User: " + saved.getEmail());

        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse update(Integer id, UserRequest request) {
        User user = findById(id);

        userMapper.updateEntity(request, user);

        if (request.getRoleId() != null) {
            roleRepository.findById(request.getRoleId())
                    .ifPresent(user::setRole);
        }

        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {
            user.setPasswordHash(
                    passwordEncoder.encode(request.getPassword()));
        }

        securityUtils.getCurrentUser()
                .ifPresent(user::setUpdatedBy);

        activityLogService.log("UPDATE", "User",
                id, "កែប្រែ: " + user.getEmail());

        return userMapper.toResponse(userRepository.save(user));
    }
    @Override
    public UserResponse updateStatus(
            Integer id, StatusRequest request) {
        User user = findById(id);
        lookupStatusRepository.findById(request.getStatusCode())
                .ifPresent(user::setStatusCode);
        activityLogService.log("UPDATE", "User",
                id, "ស្ថានភាព → " + request.getStatusCode());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getPermissions(Integer id) {
        User user = findById(id);
        return rolePermissionRepository
                .findPermissionNamesByRoleId(
                        user.getRole().getRoleId());
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        userRepository.deleteById(id);
        activityLogService.log("DELETE", "User",
                id, "លុប User");
    }

    private User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("អ្នកប្រើ", id));
    }
}
