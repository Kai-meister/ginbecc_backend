package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper
        .ContractOfficerMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security
        .SecurityUtils;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;
import org.springframework.web.context.request
        .RequestContextHolder;
import org.springframework.web.context.request
        .ServletRequestAttributes;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContractOfficerServiceImpl
        implements ContractOfficerService {

    private final ContractOfficerRepository  contractRepo;
    private final DepartmentRepository       deptRepo;
    private final LookupOfficerStatusRepository statusRepo;
    private final ContractOfficerMapper      contractMapper;
    private final SecurityUtils              securityUtils;
    private final ActivityLogService         activityLogService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContractOfficerResponse> getAll(
            int page, int size,
            String status,
            Integer deptId,
            Integer expiringWithinDays) {

        Integer resolvedId = resolveContractOfficerId();
        Integer resolvedDeptId = resolveDepartmentScope(deptId); // Fix

        if (expiringWithinDays != null) {
            LocalDate expiry = LocalDate.now()
                    .plusDays(expiringWithinDays);
            List<ContractOfficer> list;

            if (resolvedId != null) {
                list = contractRepo
                        .findByContractOfficerIdAndExpiring(
                                resolvedId, expiry);
            } else {
                list = contractRepo.findExpiring(expiry);
            }
            return toPage(list);
        }

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<ContractOfficer> result;

        if (resolvedId != null) {
            result = contractRepo.findById(resolvedId)
                    .map(c -> {
                        List<ContractOfficer> l = List.of(c);
                        return (Page<ContractOfficer>)
                                new PageImpl<>(l, pageable, l.size());
                    })
                    .orElse(Page.empty(pageable));

        } else if (status != null && resolvedDeptId != null) { // Fix
            result = contractRepo
                    .findByDepartment_DepartmentIdAndStatusCode_StatusCode(
                            resolvedDeptId, status, pageable); // Fix

        } else if (status != null) {
            result = contractRepo
                    .findByStatusCode_StatusCode(status, pageable);

        } else if (resolvedDeptId != null) {
            result = contractRepo
                    .findByDepartment_DepartmentId(
                            resolvedDeptId, pageable);
        } else {
            result = contractRepo.findAll(pageable);
        }

        return PageResponse.of(
                result.map(contractMapper::toResponse));
    }
    @Override
    @Transactional(readOnly = true)
    public ContractOfficerResponse getById(
            Integer id) {

        ContractOfficer contract = findById(id);

        validateViewPermission(contract);


        securityUtils.validateDepartmentScope(
                contract.getDepartment() != null
                        ? contract.getDepartment()
                        .getDepartmentId()
                        : null);

        return contractMapper.toResponse(contract);
    }

    @Override
    public ContractOfficerResponse create(
            ContractOfficerRequest request) {

        if (securityUtils.isContractOfficer()) {
            throw new BusinessException(
                    "Contract Officer"
                            + " មិនអាចបង្កើត Record ថ្មី");
        }


        securityUtils.validateDepartmentScope(
                request.getDepartmentId());

        if (contractRepo
                .existsByContractOfficerCode(
                        request
                                .getContractOfficerCode())) {
            throw new DuplicateResourceException(
                    "លេខកូដ ["
                            + request.getContractOfficerCode()
                            + "] មានស្ទួន");
        }

        ContractOfficer contract =
                contractMapper.toEntity(request);
        contract.setDepartment(
                findActiveDept(
                        request.getDepartmentId()));
        contract.setStatusCode(
                findStatus(request.getStatusCode()));

        ContractOfficer saved =
                contractRepo.save(contract);

        activityLogService.log(
                "CREATE", "ContractOfficer",
                saved.getContractOfficerId(),
                "បង្កើត: " + saved.getFullNameKh(),
                buildContext());

        return contractMapper.toResponse(saved);
    }


    @Override
    public ContractOfficerResponse update(
            Integer id,
            ContractOfficerRequest request) {

        if (securityUtils.isContractOfficer()) {
            throw new BusinessException(
                    "មន្ត្រីកិច្ចសន្យា"
                            + " មិនមានសិទ្ធិក្នុងការកែប្រែទិន្នន័យឡើយ");
        }


        ContractOfficer contract = findById(id);

        securityUtils.validateDepartmentScope(
                contract.getDepartment() != null
                        ? contract.getDepartment()
                        .getDepartmentId()
                        : null);

        securityUtils.validateDepartmentScope(
                request.getDepartmentId());

        if (!contract
                .getContractOfficerCode()
                .equals(request.getContractOfficerCode())
                && contractRepo
                .existsByContractOfficerCode(
                        request.getContractOfficerCode())) {
            throw new DuplicateResourceException(
                    "លេខកូដ ["
                            + request.getContractOfficerCode()
                            + "] មានស្ទួន");
        }

        validateDates(request);

        if (!contract.getDepartment()
                .getDepartmentId()
                .equals(request.getDepartmentId())) {
            contract.setDepartment(
                    findActiveDept(
                            request.getDepartmentId()));
        }

        contract.setStatusCode(
                findStatus(request.getStatusCode()));

        contractMapper.updateEntity(request, contract);

        activityLogService.log(
                "UPDATE", "ContractOfficer",
                id,
                "កែប្រែ: " + contract.getFullNameKh(),
                buildContext());

        return contractMapper.toResponse(
                contractRepo.save(contract));
    }
    private void validateDates(
            ContractOfficerRequest request) {

        if (request.getDob() != null
                && !request.getDob()
                .isBefore(LocalDate.now())) {
            throw new BusinessException(
                    "ថ្ងៃខែឆ្នាំកំណើត"
                            + " ត្រូវតែជាកាលបរិច្ឆេទក្នុងអតីតកាល (មុនថ្ងៃបច្ចុប្បន្ន)");
        }

        if (request.getStartDate() != null
                && request.getEndDate() != null
                && !request.getEndDate()
                .isAfter(
                        request.getStartDate())) {
            throw new BusinessException(
                    "ថ្ងៃផុតកំណត់"
                            + " ត្រូវធំជាង"
                            + " ថ្ងៃចាប់ផ្ដើម");
        }
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

        if (securityUtils.isContractOfficer()) {
            throw new BusinessException(
                    "មន្ត្រីជាប់កិច្ចសន្យា មិនមានសិទ្ធិក្នុងការលុបព័ត៌មាន ឬទិន្នន័យនេះឡើយ");
        }

        ContractOfficer contract = findById(id);

        securityUtils.validateDepartmentScope(
                contract.getDepartment() != null
                        ? contract.getDepartment()
                        .getDepartmentId()
                        : null);

        contractRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "ContractOfficer", id,
                "លុប: " + contract.getFullNameKh(),
                buildContext());
    }

    private Integer resolveDepartmentScope(
            Integer requestedDeptId) {

        if (securityUtils
                .canBypassDepartmentScope()) {
            return requestedDeptId;
        }

        Integer ownDeptId =
                securityUtils.getCurrentDepartmentId();

        if (ownDeptId == null) {
            return requestedDeptId;
        }

        return ownDeptId;
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

        if (dept.getStatus()
                != ActiveStatus.ACTIVE) {
            throw new BusinessException(
                    "នាយកដ្ឋាន \""
                            + dept.getDepartmentName()
                            + "\" មិនមានសកម្មភាពក្នុងប្រព័ន្ធឡើយ (Inactive)");
        }

        return dept;
    }

    private LookupOfficerStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យស្ថានភាពដែលមានកូដ", code));
    }

    private PageResponse<ContractOfficerResponse>
    toPage(List<ContractOfficer> list) {
        return PageResponse
                .<ContractOfficerResponse>builder()
                .content(list.stream()
                        .map(contractMapper::toResponse)
                        .toList())
                .pageNumber(0)
                .pageSize(list.size())
                .totalElements(list.size())
                .totalPages(
                        list.isEmpty() ? 0 : 1)
                .first(true).last(true)
                .build();
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
}