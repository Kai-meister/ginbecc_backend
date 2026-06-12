package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request.DepartmentRequest;
import gov.kh.mcr.inspectorate.dto.response.DepartmentResponse;
import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.DepartmentMapper;
import gov.kh.mcr.inspectorate.repository.ContractOfficerRepository;
import gov.kh.mcr.inspectorate.repository.DepartmentRepository;
import gov.kh.mcr.inspectorate.repository.OfficerRepository;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.DepartmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl
        implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final ActivityLogService activityLogService;
    private final OfficerRepository    officerRepository;
    private final ContractOfficerRepository contractOfficerRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll(
            ActiveStatus status,
            String keyword) {

        List<Department> list;

        if (status != null
                && StringUtils.hasText(keyword)) {
            list = departmentRepository
                    .findByStatusAndDepartmentNameContainingIgnoreCase(
                            status, keyword);
        } else if (status != null) {
            list = departmentRepository
                    .findByStatus(status);
        } else if (StringUtils.hasText(keyword)) {
            list = departmentRepository
                    .findByDepartmentNameContainingIgnoreCase(
                            keyword);
        } else {
            list = departmentRepository
                    .findAllByOrderByDepartmentNameAsc();
        }

        return list.stream()
                .map(departmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getById(Integer id) {
        return departmentMapper.toResponse(
                findById(id));
    }

    @Override
    public DepartmentResponse create(
            DepartmentRequest request) {

        if (departmentRepository
                .existsByDepartmentCode(
                        request.getDepartmentCode())) {
            throw new DuplicateResourceException(
                    "លេខកូដនាយកដ្ឋាន ["
                            + request.getDepartmentCode()
                            + "] មានក្នុងប្រព័ន្ធរួចហើយ");
        }

        Department saved = departmentRepository.save(
                departmentMapper.toEntity(request));

        activityLogService.log(
                "CREATE", "Department",
                saved.getDepartmentId(),
                "បង្កើតនាយកដ្ឋានថ្មី "
                        + saved.getDepartmentName());

        return departmentMapper.toResponse(saved);
    }

    @Override
    public void delete(Integer id) {

        Department dept = findById(id);

        long officerCount =
                officerRepository
                        .countByDepartment_DepartmentId(id);

        if (officerCount > 0) {
            throw new BusinessException(
                    "មិនអាចលុបទិន្នន័យនាយកដ្ឋាននេះបានឡើយ ដោយសារមានមន្ត្រីកំពុងបំពេញការងារចំនួន"
                            + " មានមន្ត្រី "
                            + officerCount + " នាក់"
                            + "។ សូមធ្វើការផ្លាស់ប្តូរស្ថានភាពទៅជា «មិនមានសកម្មភាព» ជំនួសវិញ។");
        }

        // Check has ContractOfficers
        long contractCount =
                contractOfficerRepository
                        .countByDepartment_DepartmentId(id);

        if (contractCount > 0) {
            throw new BusinessException(
                    "មិនអាចលុបទិន្នន័យនាយកដ្ឋាននេះបានឡើយ ដោយសារមានមន្ត្រីកិច្ចសន្យាកំពុងបំពេញការងារចំនួន"
                            + contractCount + " នាក់"
                            + "។ សូមធ្វើការផ្លាស់ប្តូរស្ថានភាពទៅជា «មិនមានសកម្មភាព» ជំនួសវិញ។");
        }
        departmentRepository.deleteById(id);

        activityLogService.log(
                "DELETE", "Department",
                id, "លុបទិន្នន័យនាយកដ្ឋាន",
                buildContext());
    }

    @Override
    public DepartmentResponse update(
            Integer id,
            DepartmentRequest request) {

        Department dept = findById(id);

        // Fix — Set INACTIVE → warn active officers
        if (request.getStatus() == ActiveStatus.INACTIVE
                && dept.getStatus() == ActiveStatus.ACTIVE) {

            long count =
                    officerRepository
                            .countByDepartment_DepartmentId(id);

            if (count > 0) {
                throw new BusinessException(
                        "មិនអាចផ្លាស់ប្តូរស្ថានភាពនាយកដ្ឋាននេះទៅជា «មិនមានសកម្មភាព» បានឡើយ "
                                + "ដោយសារមានមន្ត្រីកំពុងស្ថិតក្នុងស្ថានភាពសកម្មចំនួន "
                                + count + " នាក់។");
            }
        }

        departmentMapper.updateEntity(request, dept);

        activityLogService.log(
                "UPDATE", "Department",
                id, "កែប្រែព័ត៌មាននាយកដ្ឋាន "
                        + dept.getDepartmentName(),
                buildContext());

        return departmentMapper.toResponse(
                departmentRepository.save(dept));
    }



    private Department findById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យនាយកដ្ឋានដែលមានលេខសម្គាល់ ", id));
    }
    private ActivityLogContext buildContext() {
        HttpServletRequest request =
                getCurrentRequest();
        return securityUtils.buildLogContext(request);
    }

    // ── Get current HTTP Request ──────────────────
    private HttpServletRequest getCurrentRequest() {
        try {
            return ((ServletRequestAttributes)
                    RequestContextHolder
                            .currentRequestAttributes())
                    .getRequest();
        } catch (Exception e) {
            return null;
        }
    }
}