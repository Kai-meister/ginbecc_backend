package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request
        .ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request
        .PositionRequest;
import gov.kh.mcr.inspectorate.dto.response
        .PositionResponse;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper
        .PositionMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security
        .SecurityUtils;
import gov.kh.mcr.inspectorate.service
        .ActivityLogService;
import gov.kh.mcr.inspectorate.service
        .PositionService;
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
public class PositionServiceImpl
        implements PositionService {

    private final PositionRepository   positionRepo;
    private final DepartmentRepository deptRepo;
    private final SecurityUtils        securityUtils;
    private final ActivityLogService   activityLogService;
    private final PositionMapper       positionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PositionResponse> getAll(
            Integer departmentId,
            String keyword) {

        List<Position> list;

        if (departmentId != null
                && StringUtils.hasText(keyword)) {
            list = positionRepo
                    .findByDepartment_DepartmentIdAndPositionNameContainingIgnoreCase(
                            departmentId, keyword);

        } else if (departmentId != null) {
            list = positionRepo
                    .findByDepartment_DepartmentIdOrderByPositionNameAsc(
                            departmentId);

        } else if (StringUtils.hasText(keyword)) {
            list = positionRepo
                    .findByPositionNameContainingIgnoreCase(
                            keyword);

        } else {
            list = positionRepo
                    .findAllByOrderByDepartmentAscPositionNameAsc();
        }

        return list.stream()
                .map(positionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PositionResponse getById(Integer id) {
        return positionMapper.toResponse(
                findById(id));
    }

    @Override
    public PositionResponse create(
            PositionRequest request) {

        if (positionRepo.existsByPositionCode(
                request.getPositionCode())) {
            throw new DuplicateResourceException(
                    "មិនអាចបង្កើតបានទេ ដោយសារលេខកូដតំណែង ["
                            + request.getPositionCode()
                            + "] នេះមានក្នុងប្រព័ន្ធរួចហើយ (ស្ទួន)។");
        }

        Position position =
                positionMapper.toEntity(request);
        position.setDepartment(
                findActiveDept(
                        request.getDepartmentId()));

        Position saved =
                positionRepo.save(position);

        activityLogService.log(
                "CREATE", "Position",
                saved.getPositionId(),
                "បានបង្កើតទិន្នន័យតំណែងថ្មីឈ្មោះ "
                        + saved.getPositionName(),
                buildContext());

        return positionMapper.toResponse(saved);
    }

    @Override
    public PositionResponse update(
            Integer id,
            PositionRequest request) {

        Position position = findById(id);
        if (!position.getPositionCode()
                .equals(request
                        .getPositionCode())
                && positionRepo
                .existsByPositionCode(
                        request.getPositionCode())) {
            throw new DuplicateResourceException(
                    "មិនអាចធ្វើបច្ចុប្បន្នភាពបានទេ ដោយសារលេខកូដតំណែង ["
                            + request.getPositionCode()
                            + "] នេះមានក្នុងប្រព័ន្ធរួចហើយ (ស្ទួន)។");
        }

        if (!position.getDepartment()
                .getDepartmentId()
                .equals(request
                        .getDepartmentId())) {
            position.setDepartment(
                    findActiveDept(
                            request.getDepartmentId()));
        }

        positionMapper.updateEntity(
                request, position);

        activityLogService.log(
                "UPDATE", "Position",
                id,
                "បានកែប្រែទិន្នន័យតំណែងឈ្មោះ "
                        + position.getPositionName(),
                buildContext());

        return positionMapper.toResponse(
                positionRepo.save(position));
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        positionRepo.deleteById(id);
        activityLogService.log(
                "DELETE", "Position",
                id, "បានលុបទិន្នន័យតំណែងលេខសម្គាល់ " + id,
                buildContext());
    }


    private Position findById(Integer id) {
        return positionRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យតំណែងដែលមានលេខសម្គាល់", id));
    }

    private Department findActiveDept(Integer id) {
        Department dept =
                deptRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "មិនមានទិន្នន័យនាយកដ្ឋានដែលមានលេខសម្គាល់ ", id));

        if (dept.getStatus()
                != ActiveStatus.ACTIVE) {
            throw new BusinessException(
                    "នាយកដ្ឋាន \""
                            + dept.getDepartmentName()
                            + "\" មិនអាចប្រើប្រាស់បានឡើយ ដោយសារមានស្ថានភាព៖ "
                            + dept.getStatus().name());
        }

        return dept;
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