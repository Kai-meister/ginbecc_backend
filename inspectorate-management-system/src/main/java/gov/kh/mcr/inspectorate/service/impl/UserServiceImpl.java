package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper.UserMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password
        .PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request
        .RequestContextHolder;
import org.springframework.web.context.request
        .ServletRequestAttributes;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl
        implements UserService {

    private final UserRepository             userRepo;
    private final OfficerRepository          officerRepo;
    private final RoleRepository             roleRepo;
    private final LookupUserStatusRepository statusRepo;
    private final RolePermissionRepository   rolePermRepo;
    private final UserMapper                 userMapper;
    private final PasswordEncoder            passwordEncoder;
    private final SecurityUtils              securityUtils;
    private final ActivityLogService         activityLogService;
    private final ContractOfficerRepository  contractOfficerRepository;

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

        if (StringUtils.hasText(keyword)) {
            result = userRepo
                    .findByUserNameKhContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            keyword, keyword, pageable);
        } else if (roleId != null
                && status != null) {
            result = userRepo
                    .findByRole_RoleIdAndStatusCode_StatusCode(
                            roleId, status, pageable);
        } else if (roleId != null) {
            result = userRepo
                    .findByRole_RoleId(
                            roleId, pageable);
        } else if (status != null) {
            result = userRepo
                    .findByStatusCode_StatusCode(
                            status, pageable);
        } else {
            result = userRepo.findAll(pageable);
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
    @Transactional(readOnly = true)
    public List<String> getPermissions(
            Integer id) {
        User user = findById(id);
        return rolePermRepo
                .findPermissionNamesByRoleId(
                        user.getRole().getRoleId());
    }


    @Override
    public UserResponse create(UserRequest request) {

        validateOfficerByType(request);

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new BusinessException(
                    "ពាក្យសម្ងាត់ចាំបាច់ត្រូវបញ្ចូល");
        }

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "មិនអាចបង្កើតបានទេ ដោយសារអ៊ីមែល [" + request.getEmail()
                            + "] មានក្នុងប្រព័ន្ធរួចហើយ។");
        }

        checkOfficerDuplicate(request, null);

        User user = userMapper.toEntity(request);
        user.setRole(findRole(request.getRoleId()));
        user.setStatusCode(
                findStatus(request.getStatusCode()));
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));user.setFailedLoginCount(0);

        setOfficerByType(user, request);

        if (request.getOfficerId() != null) {
            Officer officer =
                    officerRepo.findById(
                                    request.getOfficerId())
                            .orElseThrow(() ->
                                    new
                                            ResourceNotFoundException(
                                            "Officer",
                                            request
                                                    .getOfficerId()));

            user.setOfficer(officer);

            // Fix — Profile auto-set
            // from Officer (no upload needed)
            log.info(
                    "User linked to Officer {}"
                            + " — Profile auto-inherit"
                            + " from Officer",
                    officer.getOfficerId());
        }
        if (request.getContractOfficerId()
                != null) {
            ContractOfficer co =
                    contractOfficerRepository.findById(
                                    request
                                            .getContractOfficerId())
                            .orElseThrow(() ->
                                    new
                                            ResourceNotFoundException(
                                            "ContractOfficer",
                                            request
                                                    .getContractOfficerId()));

            user.setContractOfficer(co);

            log.info(
                    "User linked to"
                            + " ContractOfficer {}"
                            + " — Profile auto-inherit"
                            + " from ContractOfficer",
                    co.getContractOfficerId());
        }

        User saved = userRepo.save(user);

        activityLogService.log(
                "CREATE", "User",
                saved.getUserId(),
                "បានបង្កើតគណនីអ្នកប្រើប្រាស់ថ្មីសម្រាប់៖ " + saved.getEmail(),
                buildContext());

        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse update(
            Integer id, UserRequest request) {

        User user = findById(id);

        validateOfficerByType(request);

        if (!user.getEmail().equals(request.getEmail())
                && userRepo.existsByEmail(
                request.getEmail())) {
            throw new DuplicateResourceException(
                    "មិនអាចកែប្រែបានទេ ដោយសារអ៊ីមែល [" + request.getEmail()
                            + "] មានក្នុងប្រព័ន្ធរួចហើយ។");
        }

        checkOfficerDuplicate(request, id);

        userMapper.updateEntity(request, user);
        user.setRole(findRole(request.getRoleId()));
        user.setStatusCode(
                findStatus(request.getStatusCode()));

        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {
            user.setPasswordHash(
                    passwordEncoder.encode(
                            request.getPassword()));
        }

        setOfficerByType(user, request);

        User saved = userRepo.save(user);

        activityLogService.log(
                "UPDATE", "User",
                id,
                "បានកែប្រែទិន្នន័យគណនីអ្នកប្រើប្រាស់ " + saved.getEmail(),
                buildContext());

        return userMapper.toResponse(saved);
    }

    private ContractOfficer resolveContractOfficer(
            Integer contractOfficerId) {

        if (contractOfficerId == null) return null;

        return contractOfficerRepository
                .findById(contractOfficerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រីជាប់កិច្ចសន្យា",
                                contractOfficerId));
    }

    @Override
    public UserResponse updateStatus(
            Integer id,
            StatusRequest request) {

        User user = findById(id);

        user.setStatusCode(
                findStatus(request.getStatusCode()));

        activityLogService.log(
                "UPDATE", "User",
                id,
                "បានកែប្រែស្ថានភាពគណនីទៅជា "
                        + request.getStatusCode(),
                buildContext());

        return userMapper.toResponse(
                userRepo.save(user));
    }

    @Override
    public void delete(Integer id) {
        User user = findById(id);
        Integer currentId =
                securityUtils.getCurrentUserId();
        if (id.equals(currentId)) {
            throw new BusinessException(
                    "មិនអនុញ្ញាតឱ្យលុបគណនីអ្នកប្រើប្រាស់របស់ខ្លួនឯង");
        }

        userRepo.deleteById(id);
        activityLogService.log(
                "DELETE", "User",
                id, "បានលុបគណនីអ្នកប្រើប្រាស់ " + user.getEmail(),
                buildContext());
    }

    private User findById(Integer id) {
        return userRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "អ្នកប្រើប្រាស់", id));
    }

    private Officer resolveOfficer(
            Integer officerId) {
        if (officerId == null) return null;
        return officerRepo.findById(officerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "រកមិនឃើញព័ត៌មានមន្ត្រីដែលមានលេខសម្គាល់ ", officerId));
    }

    private Role findRole(Integer roleId) {
        return roleRepo.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "រកមិនឃើញទិន្នន័យតួនាទី ដែលមានលេខសម្គាល់", roleId));
    }

    private LookupUserStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យស្ថានភាពអ្នកប្រើប្រាស់ដែលមានលេខកូដ ", code));
    }

    private ActivityLogContext buildContext() {
        try {
            var req =
                    ((ServletRequestAttributes)
                            RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();
            return securityUtils
                    .buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext.builder()
                    .build();
        }
    }
    private void setOfficerByType(
            User user, UserRequest request) {

        user.setOfficer(null);
        user.setContractOfficer(null);
        user.setUserType(request.getUserType());

        switch (request.getUserType()) {
            case OFFICER -> user.setOfficer(
                    resolveOfficer(request.getOfficerId()));
            case CONTRACT_OFFICER ->
                    user.setContractOfficer(
                            resolveContractOfficer(
                                    request.getContractOfficerId()));
        }
    }

    private void validateOfficerByType(
            UserRequest request) {

        switch (request.getUserType()) {
            case OFFICER -> {
                if (request.getOfficerId() == null)
                    throw new BusinessException(
                            "សូមបញ្ចូលលេខសម្គាល់មន្ត្រី (Officer ID) ឱ្យបានត្រឹមត្រូវ។");
                if (request.getContractOfficerId() != null)
                    throw new BusinessException(
                            "ប្រភេទមន្ត្រីក្របខ័ណ្ឌ (OFFICER) មិនអាចមានលេខសម្គាល់មន្ត្រីជាប់កិច្ចសន្យាឡើយ។");
            }
            case CONTRACT_OFFICER -> {
                if (request.getContractOfficerId() == null)
                    throw new BusinessException(
                            "សូមបញ្ចូលលេខសម្គាល់មន្ត្រីជាប់កិច្ចសន្យា (Contract Officer ID) ឱ្យបានត្រឹមត្រូវ។");
                if (request.getOfficerId() != null)
                    throw new BusinessException(
                            "ប្រភេទមន្ត្រីជាប់កិច្ចសន្យា (CONTRACT_OFFICER) មិនអាចមានលេខសម្គាល់មន្ត្រីក្របខ័ណ្ឌឡើយ។");
            }
        }
    }

    private void checkOfficerDuplicate(
            UserRequest request, Integer excludeId) {

        if (request.getOfficerId() != null) {
            boolean exists = excludeId == null
                    ? userRepo.existsByOfficer_OfficerId(
                    request.getOfficerId())
                    : userRepo
                      .existsByOfficer_OfficerIdAndUserIdNot(
                              request.getOfficerId(), excludeId);
            if (exists) throw new BusinessException(
                    "មន្ត្រីនេះមានគណនីអ្នកប្រើប្រាស់រួចហើយ");
        }

        if (request.getContractOfficerId() != null) {
            boolean exists = excludeId == null
                    ? userRepo
                      .existsByContractOfficer_ContractOfficerId(
                              request.getContractOfficerId())
                    : userRepo
                      .existsByContractOfficer_ContractOfficerIdAndUserIdNot(
                              request.getContractOfficerId(),
                              excludeId);
            if (exists) throw new BusinessException(
                    "មន្ត្រីជាប់កិច្ចសន្យានេះមានគណនីអ្នកប្រើប្រាស់រួចហើយ");
        }
    }

}