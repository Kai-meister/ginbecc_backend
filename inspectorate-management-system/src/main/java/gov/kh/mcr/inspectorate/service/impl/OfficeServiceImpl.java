package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request.OfficeRequest;
import gov.kh.mcr.inspectorate.dto.response.OfficeResponse;
import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.entity.Office;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.OfficeMapper;
import gov.kh.mcr.inspectorate.repository.ContractOfficerRepository;
import gov.kh.mcr.inspectorate.repository.DepartmentRepository;
import gov.kh.mcr.inspectorate.repository.OfficeRepository;
import gov.kh.mcr.inspectorate.repository.OfficerRepository;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepo;
    private final OfficerRepository officerRepo;
    private final ContractOfficerRepository contractRepo;
    private final OfficeMapper officeMapper;
    private final DepartmentRepository departmentRepo;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional(readOnly = true)
    public List<OfficeResponse> getAll(ActiveStatus status, String keyword) {

        List<Office> list;

        if (status != null && StringUtils.hasText(keyword)) {
            list = officeRepo.findByStatusAndOfficeNameContainingIgnoreCase(status, keyword);
        } else if (status != null)
        {
            list = officeRepo.findByStatus(status);
        } else if (StringUtils.hasText(keyword))
        {
            list = officeRepo.findByOfficeNameContainingIgnoreCase(keyword);
        } else {
            list = officeRepo.findAllByOrderByOfficeNameAsc();
        }
        return list.stream().map(officeMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OfficeResponse getById(Integer id) {

        return officeMapper.toResponse(findById(id));
    }

    @Override
    public OfficeResponse create(OfficeRequest request) {

        if (officeRepo.existsByOfficeCode(request.getOfficeCode())){
            throw new DuplicateResourceException(
                    "លេខកូដ [" + request.getOfficeCode()
                            + "] មានស្ទួន");
        }

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("ការិយាល័យ", request.getDepartmentId()));

        Office office = officeMapper.toEntity(request);
        office.setDepartment(department);

        Office saved = officeRepo.save(office);

        activityLogService.log("CREATE", "Office",
                saved.getOfficeId(), "បង្កើត: " + saved.getOfficeName());

        return officeMapper.toResponse(saved);
    }

    @Override
    public OfficeResponse update(Integer id, OfficeRequest request) {

        Office office = findById(id);

        if (request.getStatus() == ActiveStatus.INACTIVE
                && office.getStatus() == ActiveStatus.ACTIVE)
        {
            long officerCount = officerRepo.countByDepartment_DepartmentId(id);
            if (officerCount > 0){
                throw new BusinessException("...");
            }
        }

        officeMapper.updateEntity(request, office);

        if (request.getDepartmentId() != null
                && !request.getDepartmentId().equals(office.getDepartment().getDepartmentId())) {
            Department newDept = departmentRepo.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("ការិយាល័យ", request.getDepartmentId()));
            office.setDepartment(newDept);
        }

        activityLogService.log("UPDATE","Office",id,"កែប្រែ:" + office.getOfficeName(), buildContext());

        return officeMapper.toResponse(officeRepo.save(office));
    }

    @Override
    public void delete(Integer id) {

    Office office = findById(id);

    long officerCount = officerRepo.countByDepartment_DepartmentId(id);

    if (officerCount > 0){
        throw new BusinessException("មិនអាចលុបការិយាល័យនេះបានឡើយ ព្រោះបច្ចុប្បន្នមានមន្ត្រីក្នុងឱវាទចំនួន " +
                officerCount + "នាក់ ។ សូមផ្លាស់ប្តូរស្ថានភាពការិយាល័យទៅជា «មិនសកម្ម» ជំនួសវិញ។");
    }

    long contractCount = contractRepo.countByDepartment_DepartmentId(id);

    if (contractCount > 0){
        throw new BusinessException("មិនអាចលុបការិយាល័យនេះបានឡើយ ព្រោះបច្ចុប្បន្នមានមន្ត្រីជាប់កិច្ចសន្យាក្នុងឱវាទចំនួន "
                + contractCount + " នាក់ ។ សូមផ្លាស់ប្តូរស្ថានភាពការិយាល័យទៅជា «មិនសកម្ម» ជំនួសវិញ។");
    }

    }

    private Office findById(Integer id) {
        return officeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("កិរិយាល័យ", id));
    }

    private ActivityLogContext buildContext() {
        try {
            var req = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest();
            return securityUtils.buildLogContext(req);

        } catch (Exception e) {
            return ActivityLogContext
                    .builder().build();
        }
    }
}
