package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.DepartmentRequest;
import gov.kh.mcr.inspectorate.dto.response.DepartmentResponse;
import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.DepartmentMapper;
import gov.kh.mcr.inspectorate.repository.DepartmentRepository;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl
        implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final ActivityLogService activityLogService;

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
                    "កូដ ["
                            + request.getDepartmentCode()
                            + "] មានស្ទួន");
        }

        Department saved = departmentRepository.save(
                departmentMapper.toEntity(request));

        activityLogService.log(
                "CREATE", "Department",
                saved.getDepartmentId(),
                "បង្កើត: "
                        + saved.getDepartmentName());

        return departmentMapper.toResponse(saved);
    }

    @Override
    public DepartmentResponse update(
            Integer id, DepartmentRequest request) {

        Department dept = findById(id);
        departmentMapper.updateEntity(request, dept);

        activityLogService.log(
                "UPDATE", "Department", id,
                "កែប្រែ: "
                        + dept.getDepartmentName());

        return departmentMapper.toResponse(
                departmentRepository.save(dept));
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        departmentRepository.deleteById(id);
        activityLogService.log(
                "DELETE", "Department",
                id, "លុបនាយកដ្ឋាន");
    }

    private Department findById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "នាយកដ្ឋាន", id));
    }
}