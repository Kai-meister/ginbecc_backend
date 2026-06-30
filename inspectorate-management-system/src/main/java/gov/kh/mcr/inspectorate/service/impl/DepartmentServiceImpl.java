package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request
        .ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request
        .DepartmentRequest;
import gov.kh.mcr.inspectorate.dto.response
        .DepartmentManagerResponse;
import gov.kh.mcr.inspectorate.dto.response
        .DepartmentResponse;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .ActiveStatus;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper.*;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security
        .SecurityUtils;
import gov.kh.mcr.inspectorate.service
        .ActivityLogService;
import gov.kh.mcr.inspectorate.service
        .DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request
        .RequestContextHolder;
import org.springframework.web.context.request
        .ServletRequestAttributes;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl
        implements DepartmentService {

    private final DepartmentRepository
            departmentRepo;
    private final UserRepository
            userRepo;
    private final OfficerRepository
            officerRepo;
    private final ContractOfficerRepository
            contractRepo;
    private final DepartmentManagerRepository
            deptManagerRepo;
    private final DepartmentMapper
            departmentMapper;
    private final DepartmentManagerMapper
            departmentManagerMapper;
    private final SecurityUtils
            securityUtils;
    private final ActivityLogService
            activityLogService;


    @Override
    public List<DepartmentResponse> getAll(
            ActiveStatus status,
            String keyword) {

        List<Department> list;

        if (status != null
                && StringUtils.hasText(
                keyword)) {
            list = departmentRepo
                    .findByStatusAndDepartmentNameContainingIgnoreCase(
                            status, keyword);
        } else if (status != null) {
            list = departmentRepo
                    .findByStatus(status);
        } else if (StringUtils.hasText(
                keyword)) {
            list = departmentRepo
                    .findByDepartmentNameContainingIgnoreCase(
                            keyword);
        } else {
            list = departmentRepo
                    .findAllByOrderByDepartmentNameAsc();
        }

        return list.stream()
                .map(departmentMapper::toResponse)
                .toList();
    }


    @Override
    public DepartmentResponse getById(
            Integer id) {
        return departmentMapper.toResponse(
                findById(id));
    }

    @Override
    public DepartmentResponse create(
            DepartmentRequest request) {

        if (departmentRepo
                .existsByDepartmentCode(
                        request
                                .getDepartmentCode())) {
            throw new
                    DuplicateResourceException(
                    "លេខកូដ ["
                            + request
                            .getDepartmentCode()
                            + "] មានស្ទួន");
        }

        Department dept =
                departmentMapper.toEntity(
                        request);

        Department saved =
                departmentRepo.save(dept);

        activityLogService.log(
                "CREATE", "Department",
                saved.getDepartmentId(),
                "បង្កើត: "
                        + saved.getDepartmentName(),
                buildContext());

        return departmentMapper.toResponse(
                saved);
    }

    @Override
    public DepartmentResponse update(
            Integer id,
            DepartmentRequest request) {

        Department dept = findById(id);

        if (request.getStatus()
                == ActiveStatus.INACTIVE
                && dept.getStatus()
                == ActiveStatus.ACTIVE) {

            long officerCount =
                    officerRepo
                            .countByDepartment_DepartmentId(
                                    id);

            if (officerCount > 0) {
                throw new
                        BusinessException(
                        "មិនអាចប្តូរស្ថានភាពទៅជា «មិនសកម្ម» បានឡើយ ព្រោះនាយកដ្ឋាននេះ"
                                + "កំពុងមានមន្ត្រីក្នុងឱវាទចំនួន " + officerCount + " នាក់ ស្ថិតក្នុងស្ថានភាពសកម្មនៅឡើយ។");
            }
        }

        departmentMapper.updateEntity(
                request, dept);

        activityLogService.log(
                "UPDATE", "Department",
                id,
                "កែប្រែ: "
                        + dept.getDepartmentName(),
                buildContext());

        return departmentMapper.toResponse(
                departmentRepo.save(dept));
    }


    @Override
    public void delete(Integer id) {

        Department dept = findById(id);

        long officerCount =
                officerRepo
                        .countByDepartment_DepartmentId(
                                id);

        if (officerCount > 0) {
            throw new BusinessException(
                    "មិនអាចលុបនាយកដ្ឋាននេះបានឡើយ ព្រោះបច្ចុប្បន្នមានមន្ត្រីក្នុងឱវាទចំនួន "
                            + officerCount + " នាក់។ សូមផ្លាស់ប្តូរស្ថានភាពនាយកដ្ឋានទៅជា «មិនសកម្ម» ជំនួសវិញ។");
        }

        long contractCount =
                contractRepo
                        .countByDepartment_DepartmentId(
                                id);

        if (contractCount > 0) {
            throw new BusinessException(
                    "មិនអាចលុបនាយកដ្ឋាននេះបានឡើយ ព្រោះបច្ចុប្បន្នមានមន្ត្រីជាប់កិច្ចសន្យាក្នុងឱវាទចំនួន "
                            + contractCount + " នាក់។ សូមផ្លាស់ប្តូរស្ថានភាពនាយកដ្ឋានទៅជា «មិនសកម្ម» ជំនួសវិញ។");
        }

        departmentRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "Department",
                id, "លុបនាយកដ្ឋាន",
                buildContext());
    }


    @Override
    public DepartmentManagerResponse
    addManager(
            Integer departmentId,
            Integer managerUserId,
            Boolean isPrimary) {

        Department dept =
                findById(departmentId);

        User manager =
                userRepo.findById(
                                managerUserId)
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "User",
                                        managerUserId));

        String roleName =
                manager.getRole() != null
                        ? manager.getRole()
                        .getRoleName()
                        : "";

        if (!"MANAGER".equals(roleName)
                && !"ADMIN".equals(
                roleName)
                && !"SUPER_ADMIN".equals(
                roleName)) {
            throw new BusinessException(
                    "អ្នកប្រើប្រាស់ «" + manager.getUserNameKh() + "» មិនអាចកំណត់ជាអ្នកគ្រប់គ្រងនាយកដ្ឋានបានឡើយ "
                            + "ព្រោះតួនាទីបច្ចុប្បន្ននៅក្នុងប្រព័ន្ធមិនមែនជា អ្នកគ្រប់គ្រង (MANAGER) ឬអ្នកគ្រប់គ្រងប្រព័ន្ធ (ADMIN) ឡើយ។");
        }

        if (deptManagerRepo
                .existsByDepartment_DepartmentIdAndUser_UserId(
                        departmentId,
                        managerUserId)) {
            throw new
                    DuplicateResourceException(
                    "អ្នកប្រើប្រាស់ «" + manager.getUserNameKh() + "» ត្រូវបានកំណត់ជាអ្នកគ្រប់គ្រងនៅក្នុងនាយកដ្ឋាននេះរួចរាល់ហើយ។");
        }

        boolean primary =
                Boolean.TRUE.equals(
                        isPrimary);

        if (primary) {
            deptManagerRepo
                    .findByDepartment_DepartmentId(
                            departmentId)
                    .forEach(dm -> {
                        dm.setIsPrimary(
                                false);
                        deptManagerRepo.save(
                                dm);
                    });
        }

        DepartmentManager dm =
                DepartmentManager.builder()
                        .department(dept)
                        .user(manager)
                        .isPrimary(primary)
                        .build();

        DepartmentManager saved =
                deptManagerRepo.save(dm);

        activityLogService.log(
                "CREATE",
                "DepartmentManager",
                saved
                        .getDepartmentManagerId(),
                "បន្ថែម Manager: "
                        + manager.getUserNameKh()
                        + " → "
                        + dept.getDepartmentName(),
                buildContext());

         return departmentManagerMapper
                .toResponse(saved);
    }


    @Override
    public void removeManager(
            Integer departmentId,
            Integer managerUserId) {

        if (!deptManagerRepo
                .existsByDepartment_DepartmentIdAndUser_UserId(
                        departmentId,
                        managerUserId)) {
            throw new
                    ResourceNotFoundException(
                    "DepartmentManager",
                    managerUserId);
        }

        deptManagerRepo
                .deleteByDepartment_DepartmentIdAndUser_UserId(
                        departmentId,
                        managerUserId);

        activityLogService.log(
                "DELETE",
                "DepartmentManager",
                departmentId,
                "លុប Manager: "
                        + managerUserId,
                buildContext());
    }

    @Override
    public List<DepartmentManagerResponse>
    getManagers(
            Integer departmentId) {

        findById(departmentId);

        return deptManagerRepo
                .findByDepartment_DepartmentId(
                        departmentId)
                .stream()
                .map(
                        departmentManagerMapper
                                ::toResponse)
                .toList();
    }



    private Department findById(
            Integer id) {
        return departmentRepo.findById(id)
                .orElseThrow(() ->
                        new
                                ResourceNotFoundException(
                                "នាយកដ្ឋាន", id));
    }

    private ActivityLogContext
    buildContext() {
        try {
            var req =
                    ((ServletRequestAttributes)
                            RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();
            return securityUtils
                    .buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext
                    .builder().build();
        }
    }

}