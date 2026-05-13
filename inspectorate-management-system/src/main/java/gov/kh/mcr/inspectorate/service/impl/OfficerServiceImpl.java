package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.OfficerRequest;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.response.OfficerResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.entity.LookupOfficerStatus;
import gov.kh.mcr.inspectorate.entity.Officer;
import gov.kh.mcr.inspectorate.entity.Position;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.OfficerMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.OfficerService;
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
public class OfficerServiceImpl implements OfficerService {

    private final OfficerRepository officerRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final LookupOfficerStatusRepository lookupStatusRepository;
    private final OfficerMapper officerMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;
    private final AttachmentRepository attachmentRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OfficerResponse> getAll(
            int page, int size,
            Integer deptId, String status) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<Officer> result;
        if (deptId != null && status != null) {
            result = officerRepository
                    .findByDepartment_DepartmentIdAndStatusCode_StatusCode(
                            deptId, status, pageable);
        } else if (deptId != null) {
            result = officerRepository
                    .findByDepartment_DepartmentId(
                            deptId, pageable);
        } else if (status != null) {
            result = officerRepository
                    .findByStatusCode_StatusCode(status, pageable);
        } else {
            result = officerRepository.findAll(pageable);
        }

        return PageResponse.of(
                result.map(this::toResponseWithAge));
    }

    @Override
    @Transactional(readOnly = true)
    public OfficerResponse getById(Integer id) {
        return toResponseWithAge(findById(id));
    }

    @Override
    public OfficerResponse create(OfficerRequest request) {
        if (officerRepository.existsByOfficerCode(
                request.getOfficerCode())) {
            throw new DuplicateResourceException(
                    "លេខកូដ ["
                            + request.getOfficerCode() + "] មានស្ទួន");
        }

        Officer officer = officerMapper.toEntity(request);
        officer.setDepartment(
                findDepartment(request.getDepartmentId()));
        officer.setPosition(
                findPosition(request.getPositionId()));
        officer.setStatusCode(
                findStatus(request.getStatusCode()));

        Officer saved = officerRepository.save(officer);
        activityLogService.log("CREATE", "Officer",
                saved.getOfficerId(),
                "បង្កើត: " + saved.getFullNameKh());

        return toResponseWithAge(saved);
    }

    @Override
    public OfficerResponse update(Integer id,
                                  OfficerRequest request) {
        Officer officer = findById(id);

        // Self-edit: only phone + address
        if (!securityUtils.hasPermission("OFFICER_MANAGE")
                && securityUtils.hasPermission(
                "SELF_PROFILE_EDIT")) {

            officer.setPhone(request.getPhone());
            officer.setCurrentAddress(
                    request.getCurrentAddress());

        } else {
            // Full update for OFFICER_MANAGE
            if (!officer.getOfficerCode()
                    .equals(request.getOfficerCode())
                    && officerRepository.existsByOfficerCode(
                    request.getOfficerCode())) {
                throw new DuplicateResourceException(
                        "លេខកូដ ["
                                + request.getOfficerCode()
                                + "] មានស្ទួន");
            }
            officerMapper.updateEntity(request, officer);
            officer.setDepartment(
                    findDepartment(request.getDepartmentId()));
            officer.setPosition(
                    findPosition(request.getPositionId()));
            officer.setStatusCode(
                    findStatus(request.getStatusCode()));
        }

        activityLogService.log("UPDATE", "Officer", id,
                "កែប្រែ: " + officer.getFullNameKh());

        return toResponseWithAge(
                officerRepository.save(officer));
    }

    @Override
    public OfficerResponse updateStatus(
            Integer id, StatusRequest request) {
        Officer officer = findById(id);
        officer.setStatusCode(
                findStatus(request.getStatusCode()));
        activityLogService.log("UPDATE", "Officer", id,
                "ស្ថានភាព → " + request.getStatusCode());
        return toResponseWithAge(
                officerRepository.save(officer));
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        officerRepository.deleteById(id);
        activityLogService.log("DELETE", "Officer",
                id, "លុបមន្ត្រី");
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfficerResponse> getNearRetirement() {
        LocalDate cutoff = LocalDate.now().minusYears(55);
        return officerRepository
                .findNearRetirement(cutoff)
                .stream()
                .map(this::toResponseWithAge)
                .toList();
    }

    private Officer findById(Integer id) {
        return officerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("មន្ត្រី", id));
    }

    private Department findDepartment(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "នាយកដ្ឋាន", id));
    }

    private Position findPosition(Integer id) {
        return positionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("តំណែង", id));
    }

    private LookupOfficerStatus findStatus(String code) {
        return lookupStatusRepository.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ស្ថានភាព", code));
    }

    private OfficerResponse toResponseWithAge(
            Officer o) {
        OfficerResponse dto =
                officerMapper.toResponse(o);
        dto.setAge(DateUtils.calculateAge(o.getDob()));
        return dto;
    }


    @Override
    public OfficerResponse updateProfileImage(Integer officerId, Integer attachmentId) {
        Officer officer = findById(officerId);

        attachmentRepository
                .findById(attachmentId)
                .ifPresent(officer::setProfileAttachment);

        activityLogService.log(
                "UPDATE", "Officer", officerId,
                "ផ្លាស់ប្ដូររូបភាព Profile");

        return toResponseWithAge(
                officerRepository.save(officer));
    }



}