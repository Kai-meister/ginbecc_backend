package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .ActiveStatus;
import gov.kh.mcr.inspectorate.enums
        .AttachmentRefType;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper
        .OfficerMapper;
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
import org.springframework.web.multipart
        .MultipartFile;
import java.time.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OfficerServiceImpl
        implements OfficerService {

    private final OfficerRepository
            officerRepo;
    private final UserRepository
            userRepo;
    private final DepartmentRepository
            deptRepo;
    private final OfficeRepository
            officeRepo;
    private final PositionRepository
            positionRepo;
    private final LookupOfficerStatusRepository
            statusRepo;
    private final AttachmentRepository
            attachmentRepo;
    private final AttachmentService
            attachmentService;
    private final MinioService
            minioService;
    private final OfficerMapper
            officerMapper;
    private final SecurityUtils
            securityUtils;
    private final ActivityLogService
            activityLogService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OfficerResponse>
    getAll(int page, int size,
           Integer departmentId,
           String status) {

        Integer resolvedDeptId =
                resolveDepartmentScope(
                        departmentId);

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("fullNameKh")
                        .ascending());

        Page<Officer> result =
                officerRepo.findAllWithFilters(
                        resolvedDeptId,
                        status, pageable);

        return PageResponse.of(
                result.map(
                        this::toResponseWithUrl));
    }

    @Override
    @Transactional(readOnly = true)
    public OfficerResponse getById(
            Integer id) {

        Officer officer = officerRepo
                .findByIdWithAll(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រី", id));

        securityUtils.validateDepartmentScope(
                officer.getDepartment() != null
                        ? officer.getDepartment()
                        .getDepartmentId()
                        : null);

        return toResponseWithUrl(officer);
    }

    @Override
    public OfficerResponse create(
            OfficerRequest request) {

        if (officerRepo.existsByOfficerCode(
                request.getOfficerCode())) {
            throw new
                    DuplicateResourceException(
                    "លេខកូដ ["
                            + request.getOfficerCode()
                            + "] មានស្ទួន");
        }

        securityUtils.validateDepartmentScope(
                request.getDepartmentId());

        Officer officer = officerMapper.toEntity(request);

        officer.setDepartment(
                findActiveDept(request.getDepartmentId()));
        officer.setOffice(findActiveOffice(request.getOfficeId()));
        officer.setPosition(
                findPos(request.getPositionId()));
        officer.setStatusCode(
                findStatus(
                        request.getStatusCode()));

        Officer saved =
                officerRepo.save(officer);

        activityLogService.log(
                "CREATE", "Officer",
                saved.getOfficerId(),
                "បង្កើត: "
                        + saved.getFullNameKh(),
                buildContext());

        return toResponseWithUrl(saved);
    }


    @Override
    public OfficerResponse update(
            Integer id,
            OfficerRequest request) {

        Officer officer = officerRepo
                .findByIdWithAll(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រី", id));

        securityUtils.validateDepartmentScope(
                officer.getDepartment() != null
                        ? officer.getDepartment()
                        .getDepartmentId()
                        : null);

        securityUtils.validateDepartmentScope(
                request.getDepartmentId());

        if (!officer.getDepartment()
                .getDepartmentId()
                .equals(
                        request
                                .getDepartmentId())) {
            officer.setDepartment(
                    findActiveDept(
                            request
                                    .getDepartmentId()));
        }
        officer.setOffice(findActiveOffice(request.getOfficeId()));
        officer.setPosition(
                findPos(request.getPositionId()));
        officer.setStatusCode(
                findStatus(
                        request.getStatusCode()));

        officerMapper.updateEntity(
                request, officer);

        activityLogService.log(
                "UPDATE", "Officer", id,
                "កែប្រែ: "
                        + officer.getFullNameKh(),
                buildContext());

        return toResponseWithUrl(
                officerRepo.save(officer));
    }


    @Override
    public void delete(Integer id) {

        Officer officer = officerRepo
                .findByIdWithAll(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រី", id));

        securityUtils.validateDepartmentScope(
                officer.getDepartment() != null
                        ? officer.getDepartment()
                        .getDepartmentId()
                        : null);

        // Fix — Check if Officer linked
        // to any User Account
        boolean hasUserLink =
                userRepo
                        .existsByOfficer_OfficerId(
                                id);

        if (hasUserLink) {
            throw new BusinessException(
                    "មិនអាចលុបមន្ត្រី \""
                            + officer.getFullNameKh()
                            + "\" — មានគណនី User"
                            + " ភ្ជាប់ — សូម Deactivate"
                            + " ជំនួស ឬ Unlink"
                            + " User Account ជាមុន");
        }

  
        if (officer.getProfileAttachment()
                != null) {
            try {
                attachmentService.delete(
                        officer
                                .getProfileAttachment()
                                .getAttachmentId());
            } catch (Exception e) {
                log.warn(
                        "Profile image delete"
                                + " failed for officer"
                                + " {}: {}",
                        id, e.getMessage());
            }
        }

        officerRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "Officer", id,
                "លុបមន្ត្រី: "
                        + officer.getFullNameKh(),
                buildContext());
    }

    @Override
    public OfficerResponse uploadProfileImage(
            Integer officerId,
            MultipartFile file) {

        Officer officer = officerRepo
                .findByIdWithAll(officerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រី", officerId));

        securityUtils.validateDepartmentScope(
                officer.getDepartment() != null
                        ? officer.getDepartment()
                        .getDepartmentId()
                        : null);

        String statusCode =
                officer.getStatusCode() != null
                        ? officer.getStatusCode()
                        .getStatusCode()
                        : "";

        if (!"ACTIVE".equals(statusCode)) {
            throw new BusinessException(
                    "មិនអាចបង្ហោះរូបភាពប្រវត្តិរូបបានឡើយ ព្រោះមន្ត្រីរូបនេះកំពុងស្ថិតក្នុងស្ថានភាព «មិនសកម្ម (INACTIVE)» ឬផ្អាកដំណើរការ។");
        }
        validateImageFile(file);

        if (officer.getProfileAttachment()
                != null) {
            deleteAttachmentSafely(
                    officer
                            .getProfileAttachment()
                            .getAttachmentId(),
                    "Officer " + officerId);

            officer.setProfileAttachment(null);
            officerRepo.save(officer);
        }

        String filePath =
                buildFilePath(
                        AttachmentRefType
                                .OFFICER_PROFILE,
                        officerId,
                        file.getOriginalFilename());

        String uploadedPath =
                minioService.upload(
                        file, filePath);

        Attachment attachment =
                Attachment.builder()
                        .filePath(uploadedPath)
                        .originalName(
                                file.getOriginalFilename())
                        .fileSize(file.getSize())
                        .fileType(
                                file.getContentType())
                        .referenceType(AttachmentRefType.OFFICER_PROFILE)
                        .referenceId(officerId)
                        .uploadedBy(
                                securityUtils
                                        .getCurrentUser()
                                        .orElse(null))
                        .isActive(true)
                        .build();

        Attachment saved =
                attachmentRepo.save(attachment);

        officer.setProfileAttachment(saved);
        Officer updatedOfficer =
                officerRepo.save(officer);

        activityLogService.log(
                "UPDATE", "Officer",
                officerId,
                "Upload Profile Image: "
                        + file.getOriginalFilename(),
                buildContext());

        log.info(
                "Officer {} profile image"
                        + " uploaded: {}",
                officerId, uploadedPath);

        return toResponseWithUrl(
                updatedOfficer);
    }

    @Override
    public OfficerResponse deleteProfileImage(
            Integer officerId) {

        Officer officer = officerRepo
                .findByIdWithAll(officerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រី", officerId));

        securityUtils.validateDepartmentScope(
                officer.getDepartment() != null
                        ? officer.getDepartment()
                        .getDepartmentId()
                        : null);

        if (officer.getProfileAttachment()
                == null) {
            throw new BusinessException(
        "មន្ត្រី «" + officer.getFullNameKh() + "» មិនមានរូបភាពប្រវត្តិរូប (Profile Image) នៅក្នុងប្រព័ន្ធសម្រាប់ធ្វើការលុបឡើយ។");
        }

        Integer attId = officer
                .getProfileAttachment()
                .getAttachmentId();

        officer.setProfileAttachment(null);
        officerRepo.save(officer);

        deleteAttachmentSafely(
                attId, "Officer " + officerId);

        activityLogService.log(
                "UPDATE", "Officer",
                officerId,
                "លុប Profile Image",
                buildContext());

        return toResponseWithUrl(officer);
    }

    private OfficerResponse
    toResponseWithUrl(
            Officer officer) {

        OfficerResponse resp =
                officerMapper.toResponse(
                        officer);

        if (officer.getProfileAttachment()
                != null
                && officer.getProfileAttachment()
                .getFilePath() != null) {

            try {
                String url =
                        minioService
                                .getPresignedUrl(
                                        officer
                                                .getProfileAttachment()
                                                .getFilePath());
                resp.setProfileImageUrl(url);

            } catch (Exception e) {
                log.warn(
                        "Presigned URL failed"
                                + " officer {}: {}",
                        officer.getOfficerId(),
                        e.getMessage());
                resp.setProfileImageUrl(null);
            }
        } else {
            resp.setProfileImageUrl(null);
        }

        return resp;
    }

    private void validateImageFile(
            MultipartFile file) {

        if (file == null
                || file.isEmpty()) {
            throw new BusinessException(
                    "File ចាំបាច់ — សូម"
                            + " Upload រូបភាព");
        }

        String ct = file.getContentType();
        boolean validType =
                ct != null
                        && (ct.equals("image/jpeg")
                        || ct.equals("image/jpg")
                        || ct.equals("image/png")
                        || ct.equals("image/webp"));

        if (!validType) {
            throw new BusinessException(
                    "ប្រភេទឯកសារ «" + ct + "» មិនត្រឹមត្រូវតាមគោលការណ៍ប្រព័ន្ធឡើយ។ សូមប្រើប្រាស់ទម្រង់ឯកសាររូបភាពជាប្រភេទ .jpg, .jpeg, .png ឬ .webp ប៉ុណ្ណោះ។"
            );
        }

        long maxSize = 5L * 1024 * 1024;
        if (file.getSize() > maxSize) {
            double currentMb = file.getSize() / 1024.0 / 1024.0;
            throw new BusinessException(
                    "ទំហំឯកសារធំលើសការកំណត់ (ទំហំបច្ចុប្បន្ន៖ "
                            + String.format("%.2f", currentMb)
                            + "MB)។ ប្រព័ន្ធអនុញ្ញាតឱ្យបង្ហោះឯកសារដែលមានទំហំអតិបរមា 5MB ប៉ុណ្ណោះ។"
            );
        }
    }

    private String buildFilePath(
            AttachmentRefType type,
            Integer refId,
            String originalName) {

        String ext = "";
        if (originalName != null
                && originalName
                .contains(".")) {
            ext = originalName.substring(
                    originalName
                            .lastIndexOf("."));
        }

        return type.getFolder()
                + "/" + refId
                + "/" + java.util.UUID
                .randomUUID()
                .toString()
                .replace("-", "")
                + ext;
    }

    private void deleteAttachmentSafely(
            Integer attachmentId,
            String context) {
        try {
            Attachment att =
                    attachmentRepo
                            .findById(attachmentId)
                            .orElse(null);

            if (att != null) {
                minioService.delete(
                        att.getFilePath());
                attachmentRepo
                        .deleteById(
                                attachmentId);
            }
        } catch (Exception e) {
            log.warn(
                    "Attachment delete failed"
                            + " [{}] ctx={}: {}",
                    attachmentId, context,
                    e.getMessage());
        }
    }

    private Department findActiveDept(
            Integer id) {
        Department dept =
                deptRepo.findById(id)
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "នាយកដ្ឋាន", id));

        if (dept.getStatus() != ActiveStatus.ACTIVE) {
            throw new BusinessException(
                    "មិនអាចយកនាយកដ្ឋាន «" + dept.getDepartmentName() + "» មកប្រើប្រាស់បានឡើយ ព្រោះនាយកដ្ឋាននេះកំពុងស្ថិតក្នុងស្ថានភាពមិនសកម្ម ឬផ្អាកដំណើរការ។"
            );
        }
        return dept;
    }

    private Office findActiveOffice(
            Integer id) {
        Office office = officeRepo.findById(id).orElseThrow(() ->
                                new ResourceNotFoundException("ការិយាល័យ", id));

        if (office.getStatus() != ActiveStatus.ACTIVE) {
            throw new BusinessException(
                    "មិនអាចយកការិយាល័យ «" + office.getOfficeName() + "» មកប្រើប្រាស់បានឡើយ ព្រោះការិយាល័យនេះកំពុងស្ថិតក្នុងស្ថានភាពមិនសកម្ម ឬផ្អាកដំណើរការ។"
            );
        }
        return office;
    }

    private Position findPos(Integer id) {
        if (id == null) return null;
        return positionRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "តំណែង", id));
    }

    private LookupOfficerStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ស្ថានភាព", code));
    }

    private Integer resolveDepartmentScope(
            Integer requestedDeptId) {
        if (securityUtils
                .canBypassDepartmentScope()) {
            return requestedDeptId;
        }
        Integer ownDeptId =
                securityUtils
                        .getCurrentDepartmentId();
        return ownDeptId != null
                ? ownDeptId : requestedDeptId;
    }

    private Officer findById(Integer id) {
        return officerRepo
                .findByIdWithAll(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រី", id));
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
            return ActivityLogContext
                    .builder().build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getProfileImageUrl(Integer officerId) {

        Officer officer = officerRepo
                .findByIdWithAll(officerId)   // eager fetch profileAttachment
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រី", officerId));

        securityUtils.validateDepartmentScope(
                officer.getDepartment() != null
                        ? officer.getDepartment().getDepartmentId()
                        : null);

        if (officer.getProfileAttachment() != null) {
            return minioService.getPresignedUrl(
                    officer.getProfileAttachment().getFilePath());
        }

        // No uploaded photo: return null so clients show their own
        // placeholder — the old "/api/v1/static/avatars/officer.png"
        // path was never actually served (nothing maps /static/**).
        return null;
    }
}
