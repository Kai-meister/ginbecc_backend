package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request.OfficerRequest;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.dto.response.OfficerResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.entity.LookupOfficerStatus;
import gov.kh.mcr.inspectorate.entity.Officer;
import gov.kh.mcr.inspectorate.entity.Position;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.OfficerMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import gov.kh.mcr.inspectorate.service.MinioService;
import gov.kh.mcr.inspectorate.service.OfficerService;
import gov.kh.mcr.inspectorate.util.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OfficerServiceImpl implements OfficerService {

    private final OfficerRepository officerRepository;
    private final DepartmentRepository deptRepository;
    private final PositionRepository positionRepository;
    private final LookupOfficerStatusRepository statusRepository;
    private final OfficerMapper officerMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final MinioService minioService;

    @Override
    public OfficerResponse create(
            OfficerRequest request) {

        if (officerRepository.existsByOfficerCode(
                request.getOfficerCode())) {
            throw new DuplicateResourceException(
                    "លេខកូដ ["
                            + request.getOfficerCode()
                            + "] មានស្ទួន");
        }

        Officer officer =
                officerMapper.toEntity(request);

        // Fix — Check dept ACTIVE
        officer.setDepartment(
                findActiveDept(
                        request.getDepartmentId()));

        officer.setPosition(
                findPos(request.getPositionId()));
        officer.setStatusCode(
                findStatus(request.getStatusCode()));

        Officer saved = officerRepository.save(officer);

        activityLogService.log(
                "CREATE", "Officer",
                saved.getOfficerId(),
                "បង្កើត: " + saved.getFullNameKh(),
                buildContext());

        return toResponseWithAge(saved);
    }

    @Override
    public OfficerResponse update(
            Integer id,
            OfficerRequest request) {

        Officer officer = findById(id);

        // Fix — Check dept ACTIVE on update too
        if (!officer.getDepartment()
                .getDepartmentId()
                .equals(request.getDepartmentId())) {
            officer.setDepartment(
                    findActiveDept(
                            request.getDepartmentId()));
        }

        officer.setPosition(
                findPos(request.getPositionId()));
        officer.setStatusCode(
                findStatus(request.getStatusCode()));

        officerMapper.updateEntity(request, officer);

        activityLogService.log(
                "UPDATE", "Officer",
                id,
                "កែប្រែ: "
                        + officer.getFullNameKh(),
                buildContext());

        return toResponseWithAge(
                officerRepository.save(officer));
    }

    // ── Fix: findActiveDept ───────────────────────
    private Department findActiveDept(Integer id) {

        // ១. Check exists
        Department dept =
                deptRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "នាយកដ្ឋាន", id));

        // ២. Check ACTIVE status
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
    public void delete(Integer id) {
        findById(id);
        officerRepository.deleteById(id);

        activityLogService.log(
                "DELETE", "Officer",
                id, "លុបមន្ត្រី",
                buildContext());           // ← Fix
    }

    // ── Fix: buildContext() ───────────────────────
    // Extract context ពី main thread
    // ហើយ pass ទៅ @Async log()
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

    // ── Other helpers ─────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponse<OfficerResponse> getAll(
            int page, int size,
            Integer deptId, String status) {

        Pageable pageable = PageRequest.of(
                page, size,
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
                    .findByStatusCode_StatusCode(
                            status, pageable);
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
    public OfficerResponse updateStatus(
            Integer id, StatusRequest request) {
        Officer officer = findById(id);
        officer.setStatusCode(
                findStatus(request.getStatusCode()));

        activityLogService.log(
                "UPDATE", "Officer",
                id,
                "បានកែសម្រួលស្ថានភាពមន្ត្រីទៅជា៖ " + request.getStatusCode(),
                buildContext());

        return toResponseWithAge(
                officerRepository.save(officer));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfficerResponse> getNearRetirement() {
        LocalDate cutoff =
                LocalDate.now().minusYears(55);
        return officerRepository
                .findNearRetirement(cutoff)
                .stream()
                .map(this::toResponseWithAge)
                .toList();
    }

    private Officer findById(Integer id) {
        return officerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យមន្ត្រីដែលមានលេខសម្គាល់ ", id));
    }

    private Department findDept(Integer id) {
        return deptRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យនាយកដ្ឋានដែលមានលេខសម្គាល់ ", id));
    }

    private Position findPos(
            Integer id) {
        return positionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យតំណែងដែលមានលេខសម្គាល់ ", id));
    }

    private LookupOfficerStatus findStatus(
            String code) {
        return statusRepository.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យស្ថានភាពដែលមានលេខកូដ ", code));
    }

    private OfficerResponse toResponseWithAge(
            Officer o) {
        OfficerResponse dto =
                officerMapper.toResponse(o);
        dto.setAge(DateUtils.calculateAge(
                o.getDob()));
        return dto;
    }

    @Override
    public OfficerResponse uploadProfileImage(
            Integer officerId,
            MultipartFile file) {
        Officer officer = findById(officerId);

        AttachmentResponse attachmentResponse =
                attachmentService.upload(
                        file,
                        AttachmentRefType.OFFICER,
                        officerId);
        attachmentRepository
                .findById(
                        attachmentResponse.getAttachmentId())
                .ifPresent(att -> {
                    officer.setProfileAttachment(att);
                    officerRepository.save(officer);
                });

        activityLogService.log(
                "UPDATE", "Officer",
                officerId,
                "បានបញ្ចូលរូបភាពប្រវត្តិរូបថ្មីឈ្មោះ "
                        + attachmentResponse.getOriginalName(),
                buildContext());

        return toResponseWithAge(
                officerRepository.save(officer));
    }

    @Override
    @Transactional(readOnly = true)
    public String getProfileImageUrl(
            Integer officerId) {

        Officer officer = findById(officerId);

        if (officer.getProfileAttachment() == null) {
            return null;
        }

        return minioService.getPresignedUrl(
                officer.getProfileAttachment()
                        .getFilePath());
    }


}