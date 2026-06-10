package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request.ContractOfficerRequest;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.response.ContractOfficerResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.ContractOfficer;
import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.entity.LookupOfficerStatus;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.ContractOfficerMapper;
import gov.kh.mcr.inspectorate.repository.ContractOfficerRepository;
import gov.kh.mcr.inspectorate.repository.DepartmentRepository;
import gov.kh.mcr.inspectorate.repository.LookupOfficerStatusRepository;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.ContractOfficerService;
import gov.kh.mcr.inspectorate.util.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContractOfficerServiceImpl
        implements ContractOfficerService {

    private final ContractOfficerRepository contractOfficerRepository;
    private final DepartmentRepository departmentRepository;
    private final LookupOfficerStatusRepository lookupStatusRepository;
    private final ContractOfficerMapper contractOfficerMapper;
    private final ActivityLogService activityLogService;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContractOfficerResponse>
    getAll(
            int page, int size,
            String status,
            Integer deptId,
            Integer expiringWithinDays) {

        if (expiringWithinDays != null) {
            LocalDate expiryDate = LocalDate.now()
                    .plusDays(expiringWithinDays);
            List<ContractOfficerResponse> list =
                    contractOfficerRepository
                            .findExpiring(expiryDate)
                            .stream()
                            .map(contractOfficerMapper::toResponse)
                            .toList();

        if (expiringWithinDays != null) {
            LocalDate expiry =
                    LocalDate.now()
                            .plusDays(expiringWithinDays);

            List<ContractOfficer> list;

            if (resolvedId != null) {
                list = contractRepo
                        .findByContractOfficerIdAndExpiring(
                                resolvedId, expiry);
            } else {
                list = contractRepo
                        .findExpiring(expiry);
            }

            return toPage(list);
        }

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<ContractOfficer> result;

        if (resolvedId != null) {
            result = contractRepo
                    .findById(resolvedId)
                    .map(c -> {
                        List<ContractOfficer> l =
                                List.of(c);
                        return (Page<ContractOfficer>)
                                new PageImpl<>(l, pageable,
                                        l.size());
                    })
                    .orElse(Page.empty(pageable));

        } else if (status != null
                && deptId != null) {
            result = contractRepo
                    .findByDepartment_DepartmentIdAndStatusCode_StatusCode(
                            deptId, status, pageable);
        } else if (status != null) {
            result = contractRepo
                    .findByStatusCode_StatusCode(
                            status, pageable);
        } else if (deptId != null) {
            result = contractRepo
                    .findByDepartment_DepartmentId(
                            deptId, pageable);
        } else {
            result = contractRepo
                    .findAll(pageable);
        }

        return PageResponse.of(
                result.map(contractOfficerMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ContractOfficerResponse getById(
            Integer id) {

        ContractOfficer contract = findById(id);

        validateViewPermission(contract);

        return contractMapper.toResponse(contract);
    }

    @Override
    public ContractOfficerResponse create(
            ContractOfficerRequest request) {

        if (contractOfficerRepository
                .existsByContractOfficerCode(
                        request
                                .getContractOfficerCode())) {
            throw new DuplicateResourceException(
                    "លេខកូដ ["
                            + request.getContractOfficerCode()
                            + "] មានស្ទួន");
        }
        validateDates(request);
        if (!contract.getDepartment()
                .getDepartmentId()
                .equals(request
                        .getDepartmentId())) {
            contract.setDepartment(
                    findActiveDept(
                            request.getDepartmentId()));
        }

        ContractOfficer contract =
        contractOfficerMapper.toEntity(request);

        // Fix — Check dept ACTIVE
        contract.setDepartment(
                findActiveDept(
                        request.getDepartmentId()));

        contract.setStatusCode(
                findStatus(request.getStatusCode()));

        ContractOfficer saved =
                contractOfficerRepository.save(contract);

        activityLogService.log(
                "CREATE", "ContractOfficer",
                saved.getContractOfficerId(),
                "បង្កើត: " + saved.getFullNameKh(),
                buildContext());

        return contractOfficerMapper.toResponse(
                contractOfficerRepository.save(contract));
    }

    @Override
    public ContractOfficerResponse update(
            Integer id,
            ContractOfficerRequest request) {

        ContractOfficer contract = findById(id);

        // Fix — Check dept ACTIVE on update
        if (!contract.getDepartment()
                .getDepartmentId()
                .equals(request.getDepartmentId())) {
            contract.setDepartment(
                    findActiveDept(
                            request.getDepartmentId()));
        }

        contract.setStatusCode(
                findStatus(request.getStatusCode()));

        contractOfficerMapper.updateEntity(
                request, contract);

        activityLogService.log(
                "UPDATE", "ContractOfficer",
                id,
                "កែប្រែ: "
                        + contract.getFullNameKh(),buildContext());

        return contractOfficerMapper.toResponse(
                contractOfficerRepository.save(contract));
    }

    // ── Fix: findActiveDept ───────────────────────
    private Department findActiveDept(Integer id) {

        Department dept =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "នាយកដ្ឋាន", id));

        if (dept.getStatus() != ActiveStatus.ACTIVE) {
            throw new BusinessException(
                    "នាយកដ្ឋាន \""
                            + dept.getDepartmentName()
                            + "\" មិនអាចប្រើបាន"
                            + " (ស្ថានភាព: "
                            + dept.getStatus().name()
                            + ")");
        }

        return dept;
    }

    @Override
    public ContractOfficerResponse updateStatus(
            Integer id,
            StatusRequest request) {

        if (securityUtils.isContractOfficer()) {
            throw new BusinessException(
                    "មន្ត្រីកិច្ចសន្យា"
                            + " មិនមានសិទ្ធិក្នុងការផ្លាស់ប្តូរស្ថានភាពឡើយ");
        }

        ContractOfficer contract = findById(id);
        contract.setStatusCode(
                findStatus(request.getStatusCode()));

        activityLogService.log(
                "UPDATE", "ContractOfficer",
                id,
                "ធ្វើបច្ចុប្បន្នភាពស្ថានភាពមន្ត្រីកិច្ចសន្យា ទៅជា "
                        + request.getStatusCode(),
                buildContext());

        return contractMapper.toResponse(
                contractRepo.save(contract));
    }

    @Override
    public void delete(Integer id) {

        // Contract Officer cannot delete
        if (securityUtils.isContractOfficer()) {
            throw new BusinessException(
                    "មន្ត្រីកិច្ចសន្យា"
                            + " មិនមានសិទ្ធិក្នុងការលុបទិន្នន័យចេញពីប្រព័ន្ធឡើយ");
        }

        ContractOfficer contract = findById(id);
        contractRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "ContractOfficer",
                id,
                "លុបទិន្នន័យមន្ត្រីកិច្ចសន្យា "
                        + contract.getFullNameKh(),
                buildContext());
    }

    private Integer resolveContractOfficerId() {
        ContractOfficer co =
                securityUtils
                        .getCurrentContractOfficerOrNull();
        return co != null
                ? co.getContractOfficerId() : null;
    }

    private void validateViewPermission(
            ContractOfficer contract) {

        ContractOfficer current =
                securityUtils
                        .getCurrentContractOfficerOrNull();

        if (current == null
                || securityUtils.hasPermission(
                "CONTRACT_OFFICER_VIEW")) {
            return;
        }

        if (!contract
                .getContractOfficerId()
                .equals(current
                        .getContractOfficerId())) {
            throw new ResourceNotFoundException(
                    "រកមិនឃើញទិន្នន័យមន្ត្រីកិច្ចសន្យាដែលមានលេខសម្គាល់ ",
                    contract.getContractOfficerId());
        }
    }

    private ActivityLogContext buildContext() {
        HttpServletRequest request = getCurrentRequest();
        return securityUtils.buildLogContext(request);
    }
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
    private ContractOfficer findById(Integer id) {
        return contractRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យមន្ត្រីកិច្ចសន្យាដែលមានលេខសម្គាល់ ", id));
    }

    private Department findActiveDept(Integer id) {
        Department dept =
                deptRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "មិនមានទិន្នន័យនាយកដ្ឋានដែលមានលេខសម្គាល់", id));

    private LookupOfficerStatus findStatus(String code) {
        return lookupStatusRepository.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ស្ថានភាព", code));
    }

//    private ContractOfficerResponse toResponseWithDays(
//            ContractOfficer c) {
//        ContractOfficerResponse dto =
//                contractOfficerMapper.toResponse(c);
//        if (c.getEndDate() != null) {
//            dto.setDaysUntilExpiry(
//                    DateUtils.daysUntil(c.getEndDate()));
//        }
//        return dto;
    //}
}
