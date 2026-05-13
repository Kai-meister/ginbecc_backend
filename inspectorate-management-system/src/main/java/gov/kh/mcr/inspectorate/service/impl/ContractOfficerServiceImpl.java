package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.ContractOfficerRequest;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.response.ContractOfficerResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.ContractOfficer;
import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.entity.LookupOfficerStatus;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.ContractOfficerMapper;
import gov.kh.mcr.inspectorate.repository.ContractOfficerRepository;
import gov.kh.mcr.inspectorate.repository.DepartmentRepository;
import gov.kh.mcr.inspectorate.repository.LookupOfficerStatusRepository;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.ContractOfficerService;
import gov.kh.mcr.inspectorate.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContractOfficerResponse> getAll(
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
                            .map(this::toResponseWithDays)
                            .toList();

            return PageResponse
                    .<ContractOfficerResponse>builder()
                    .content(list)
                    .pageNumber(0)
                    .pageSize(list.size())
                    .totalElements(list.size())
                    .totalPages(list.isEmpty() ? 0 : 1)
                    .first(true).last(true)
                    .build();
        }

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<ContractOfficer> result;

        if (status != null && deptId != null) {
            result = contractOfficerRepository
                    .findByDepartment_DepartmentIdAndStatusCode_StatusCode(
                            deptId, status, pageable);
        } else if (status != null) {
            result = contractOfficerRepository
                    .findByStatusCode_StatusCode(
                            status, pageable);
        } else if (deptId != null) {
            result = contractOfficerRepository
                    .findByDepartment_DepartmentId(
                            deptId, pageable);
        } else {
            result = contractOfficerRepository
                    .findAll(pageable);
        }

        return PageResponse.of(
                result.map(this::toResponseWithDays));
    }

    @Override
    @Transactional(readOnly = true)
    public ContractOfficerResponse getById(Integer id) {
        return contractOfficerMapper.toResponse(findById(id));
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<ContractOfficerResponse> getExpiring(int withinDays) {
//        LocalDate expiryDate = LocalDate.now().plusDays(withinDays);
//        return contractOfficerRepository.findExpiring(expiryDate)
//                .stream()
//                .map(contractOfficerMapper::toResponse)
//                .toList();
//    }

    @Override
    public ContractOfficerResponse create(
            ContractOfficerRequest request) {
        if (contractOfficerRepository.existsByContractOfficerCode(
                request.getContractOfficerCode())) {
            throw new DuplicateResourceException(
                    "លេខកូដ ["
                            + request.getContractOfficerCode()
                            + "] មានស្ទួន");
        }

        ContractOfficer entity =
                contractOfficerMapper.toEntity(request);
        entity.setDepartment(
                findDepartment(request.getDepartmentId()));
        entity.setStatusCode(
                findStatus(request.getStatusCode()));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException(
                    "ថ្ងៃចាប់ផ្តើម ត្រូវតែមុន ថ្ងៃបញ្ចប់");
        }

        ContractOfficer saved =
                contractOfficerRepository.save(entity);
        activityLogService.log("CREATE", "ContractOfficer",
                saved.getContractOfficerId(),
                "បង្កើតមន្ត្រីកិច្ចសន្យា: "
                        + saved.getFullNameKh());

        return contractOfficerMapper.toResponse(saved);
    }

    @Override
    public ContractOfficerResponse update(
            Integer id, ContractOfficerRequest request) {
        ContractOfficer entity = findById(id);

        if (!entity.getContractOfficerCode()
                .equals(request.getContractOfficerCode())
                && contractOfficerRepository
                .existsByContractOfficerCode(
                        request.getContractOfficerCode())) {
            throw new DuplicateResourceException(
                    "លេខកូដ ["
                            + request.getContractOfficerCode()
                            + "] មានស្ទួន");
        }

        entity.setFullNameKh(request.getFullNameKh());
        entity.setFullNameEn(request.getFullNameEn());
        entity.setGender(request.getGender());
        entity.setJobLevel(request.getJobLevel());
        entity.setJobDescription(request.getJobDescription());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setDepartment(
                findDepartment(request.getDepartmentId()));
        entity.setStatusCode(
                findStatus(request.getStatusCode()));

        activityLogService.log("UPDATE", "ContractOfficer",
                id, "កែប្រែ: " + entity.getFullNameKh());

        return contractOfficerMapper.toResponse(
                contractOfficerRepository.save(entity));
    }

    @Override
    public ContractOfficerResponse updateStatus(
            Integer id, StatusRequest request) {
        ContractOfficer entity = findById(id);
        entity.setStatusCode(findStatus(request.getStatusCode()));
        activityLogService.log("UPDATE", "ContractOfficer",
                id, "ស្ថានភាព → " + request.getStatusCode());
        return contractOfficerMapper.toResponse(
                contractOfficerRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        contractOfficerRepository.deleteById(id);
        activityLogService.log("DELETE", "ContractOfficer",
                id, "លុបមន្ត្រីកិច្ចសន្យា");
    }

    private ContractOfficer findById(Integer id) {
        return contractOfficerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រីកិច្ចសន្យា", id));
    }

    private Department findDepartment(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("នាយកដ្ឋាន", id));
    }

    private LookupOfficerStatus findStatus(String code) {
        return lookupStatusRepository.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ស្ថានភាព", code));
    }

    private ContractOfficerResponse toResponseWithDays(
            ContractOfficer c) {
        ContractOfficerResponse dto =
                contractOfficerMapper.toResponse(c);
        if (c.getEndDate() != null) {
            dto.setDaysUntilExpiry(
                    DateUtils.daysUntil(c.getEndDate()));
        }
        return dto;
    }
}
