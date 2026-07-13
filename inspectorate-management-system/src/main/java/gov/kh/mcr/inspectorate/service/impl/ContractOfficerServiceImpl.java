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
import org.springframework.web.multipart
        .MultipartFile;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContractOfficerServiceImpl
        implements ContractOfficerService {

    private final ContractOfficerRepository
            contractRepo;
    private final UserRepository
            userRepo;
    private final DepartmentRepository
            deptRepo;
    private final LookupOfficerStatusRepository
            statusRepo;
    private final AttachmentRepository
            attachmentRepo;
    private final MinioService
            minioService;
    private final ContractOfficerMapper
            contractMapper;
    private final SecurityUtils
            securityUtils;
    private final ActivityLogService
            activityLogService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<
    ContractOfficerResponse>
    getAll(int page, int size,
           String status,
           Integer deptId,
           Integer expiringWithinDays) {

        Integer resolvedDeptId =
                resolveDepartmentScope(deptId);

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("fullNameKh")
                        .ascending());

        Page<ContractOfficer> result;

        if (expiringWithinDays != null) {
            LocalDate expiry =
                    LocalDate.now()
                            .plusDays(
                                    expiringWithinDays);
            List<ContractOfficer>
                    expiring =
                    contractRepo
                            .findExpiring(expiry);
            result = new PageImpl<>(
                    expiring, pageable,
                    expiring.size());
        } else {
            result = contractRepo
                    .findAllWithFilters(
                            resolvedDeptId,
                            status, pageable);
        }

        return PageResponse.of(
                result.map(
                        this::toResponseWithUrl));
    }

    @Override
    @Transactional(readOnly = true)
    public ContractOfficerResponse getById(
            Integer id) {

        ContractOfficer co = findById(id);

        securityUtils.validateDepartmentScope(
                co.getDepartment() != null
                        ? co.getDepartment()
                        .getDepartmentId()
                        : null);

        return toResponseWithUrl(co);
    }


    @Override
    public ContractOfficerResponse create(
            ContractOfficerRequest request) {

        if (securityUtils.isContractOfficer()) {
            throw new PermissionDeniedException(
                    "បង្កើតទិន្នន័យមន្ត្រីកិច្ចសន្យាថ្មី",
                    "សិទ្ធិប្រតិបត្តិការត្រូវបានបដិសេធ។ គណនីប្រភេទមន្ត្រីកិច្ចសន្យាមិនមានសិទ្ធិបង្កើតទិន្នន័យថ្មីបានឡើយ។ "
                            + "មុខងារនេះត្រូវបានអនុញ្ញាតជូនតែអ្នកគ្រប់គ្រងប្រព័ន្ធដែលមានតួនាទីជា ADMIN ឬ SUPER_ADMIN ប៉ុណ្ណោះ។"
            );
        }

        if (contractRepo.existsByContractOfficerCode(request.getContractOfficerCode())) {
            throw new DuplicateResourceException(
                    "មិនអាចរក្សាទុកទិន្នន័យបានឡើយ ព្រោះលេខកូដមន្ត្រីកិច្ចសន្យា «"
                            + request.getContractOfficerCode()
                            + "» នេះមានក្នុងប្រព័ន្ធរួចរាល់ហើយ (ស្ទួន)។"
            );
        }

        securityUtils.validateDepartmentScope(
                request.getDepartmentId());

        validateContractDates(request);

        ContractOfficer co = contractMapper.toEntity(request);

        co.setDepartment(
                findActiveDept(
                        request.getDepartmentId()));
        co.setStatusCode(
                findStatus(
                        request.getStatusCode()));

        ContractOfficer saved =
                contractRepo.save(co);

        activityLogService.log(
                "CREATE", "ContractOfficer",
                saved.getContractOfficerId(),
                "បង្កើត: "
                        + saved.getFullNameKh(),
                buildContext());

        return toResponseWithUrl(saved);
    }

    @Override
    public ContractOfficerResponse update(
            Integer id,
            ContractOfficerRequest request) {

        ContractOfficer co = findById(id);

        securityUtils.validateDepartmentScope(
                co.getDepartment() != null
                        ? co.getDepartment()
                        .getDepartmentId()
                        : null);

        securityUtils.validateDepartmentScope(
                request.getDepartmentId());

        validateContractDates(request);

        if (!co.getDepartment()
                .getDepartmentId()
                .equals(
                        request.getDepartmentId())) {
            co.setDepartment(
                    findActiveDept(
                            request
                                    .getDepartmentId()));
        }

        co.setStatusCode(
                findStatus(
                        request.getStatusCode()));

        contractMapper.updateEntity(
                request, co);

        activityLogService.log(
                "UPDATE", "ContractOfficer",
                id,
                "កែប្រែ: "
                        + co.getFullNameKh(),
                buildContext());

        return toResponseWithUrl(
                contractRepo.save(co));
    }

    @Override
    public void delete(Integer id) {

        ContractOfficer co = findById(id);

        securityUtils.validateDepartmentScope(
                co.getDepartment() != null
                        ? co.getDepartment()
                        .getDepartmentId()
                        : null);

        boolean hasUserLink = userRepo
                .existsByContractOfficer_ContractOfficerId(
                        id);

        if (hasUserLink) {
            throw new BusinessException(
                    "មិនអាចលុបមន្ត្រីកិច្ចសន្យា «" + co.getFullNameKh() + "» បានឡើយ ព្រោះមានគណនីអ្នកប្រើប្រាស់ (User Account) ភ្ជាប់នៅក្នុងប្រព័ន្ធ។ "
                            + "សូមមេត្តាធ្វើការផ្អាកដំណើរការ (Deactivate) ជំនួសវិញ ឬផ្តាច់ទំនាក់ទំនងគណនី (Unlink) នោះចេញជាមុនសិន។"
            );
        }

        if (co.getProfileAttachment()
                != null) {
            deleteAttachmentSafely(
                    co.getProfileAttachment()
                            .getAttachmentId(),
                    "ContractOfficer " + id);
        }

        contractRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "ContractOfficer",
                id,
                "លុបមន្ត្រីកិច្ចសន្យា: "
                        + co.getFullNameKh(),
                buildContext());
    }


    @Override
    public ContractOfficerResponse
    uploadProfileImage(
            Integer contractOfficerId,
            MultipartFile file) {

        ContractOfficer co =
                findById(contractOfficerId);

        securityUtils.validateDepartmentScope(
                co.getDepartment() != null
                        ? co.getDepartment()
                        .getDepartmentId()
                        : null);

        String statusCode =
                co.getStatusCode() != null
                        ? co.getStatusCode()
                        .getStatusCode()
                        : "";

        if (!"ACTIVE".equals(statusCode)) {
            throw new BusinessException(
                    "មិនអាចបង្ហោះរូបភាពប្រវត្តិរូបបានឡើយ ព្រោះមន្ត្រីកិច្ចសន្យារូបនេះកំពុងស្ថិតក្នុងស្ថានភាព «មិនសកម្ម (INACTIVE)» ឬផ្អាកដំណើរការ។"
            );
        }

        validateImageFile(file);
        if (co.getProfileAttachment()
                != null) {
            deleteAttachmentSafely(
                    co.getProfileAttachment()
                            .getAttachmentId(),
                    "ContractOfficer "
                            + contractOfficerId);
            co.setProfileAttachment(null);
            contractRepo.save(co);
        }

        String filePath = buildFilePath(
                AttachmentRefType
                        .CONTRACT_OFFICER_PROFILE,
                contractOfficerId,
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
                        .referenceType(
                                AttachmentRefType
                                        .CONTRACT_OFFICER_PROFILE)
                        .referenceId(
                                contractOfficerId)
                        .uploadedBy(
                                securityUtils
                                        .getCurrentUser()
                                        .orElse(null))
                        .isActive(true)
                        .build();

        Attachment saved =
                attachmentRepo.save(attachment);

        co.setProfileAttachment(saved);
        ContractOfficer updated =
                contractRepo.save(co);

        activityLogService.log(
                "UPDATE", "ContractOfficer",
                contractOfficerId,
                "Upload Profile Image: "
                        + file.getOriginalFilename(),
                buildContext());

        return toResponseWithUrl(updated);
    }

    @Override
    public ContractOfficerResponse
    deleteProfileImage(
            Integer contractOfficerId) {

        ContractOfficer co =
                findById(contractOfficerId);

        securityUtils.validateDepartmentScope(
                co.getDepartment() != null
                        ? co.getDepartment()
                        .getDepartmentId()
                        : null);

        if (co.getProfileAttachment() == null) {
            throw new BusinessException(
                    "មន្ត្រីកិច្ចសន្យា «" + co.getFullNameKh() + "» មិនមានរូបភាពប្រវត្តិរូប (Profile Image) នៅក្នុងប្រព័ន្ធសម្រាប់ធ្វើការលុបឡើយ។"
            );
        }

        Integer attId = co
                .getProfileAttachment()
                .getAttachmentId();

        co.setProfileAttachment(null);
        contractRepo.save(co);

        deleteAttachmentSafely(
                attId,
                "ContractOfficer "
                        + contractOfficerId);

        activityLogService.log(
                "UPDATE", "ContractOfficer",
                contractOfficerId,
                "លុប Profile Image",
                buildContext());

        return toResponseWithUrl(co);
    }

    private ContractOfficerResponse
    toResponseWithUrl(
            ContractOfficer co) {

        ContractOfficerResponse resp =
                contractMapper.toResponse(co);

        if (co.getProfileAttachment()
                != null
                && co.getProfileAttachment()
                .getFilePath() != null) {
            try {
                resp.setProfileImageUrl(
                        minioService
                                .getPresignedUrl(
                                        co.getProfileAttachment()
                                                .getFilePath()));
            } catch (Exception e) {
                log.warn(
                        "Presigned URL failed"
                                + " CO {}: {}",
                        co.getContractOfficerId(),
                        e.getMessage());
                resp.setProfileImageUrl(null);
            }
        } else {
            resp.setProfileImageUrl(null);
        }

        return resp;
    }

    private void validateContractDates(
            ContractOfficerRequest request) {

        if (request.getStartDate() != null
                && request.getEndDate()
                != null
                && !request.getEndDate()
                .isAfter(
                        request
                                .getStartDate())) {
            throw new BusinessException(
                    "កាលបរិច្ឆេទបញ្ចប់ «" + request.getEndDate() + "» ត្រូវតែនៅក្រោយកាលបរិច្ឆេទចាប់ផ្តើម «" + request.getStartDate() + "»។"
            );
        }

        if (request.getEndDate() != null
                && request.getEndDate()
                .isBefore(
                        LocalDate.now())) {
            log.warn(
                    "Contract endDate [{}]"
                            + " is in the past"
                            + " allowed but"
                            + " status should"
                            + " be INACTIVE",
                    request.getEndDate());
        }
    }

    private void validateImageFile(
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    "ឯកសារនេះគឺចាំបាច់មិនអាចខ្វះបានឡើយ។ សូមមេត្តាជ្រើសរើសរូបភាពដើម្បីបង្ហោះ (Upload) ចូលទៅក្នុងប្រព័ន្ធ។"
            );
        }

        String ct = file.getContentType();
        boolean valid =
                ct != null
                        && (ct.equals("image/jpeg")
                        || ct.equals("image/jpg")
                        || ct.equals("image/png")
                        || ct.equals("image/webp"));

        if (!valid) {
            throw new BusinessException(
                    "ប្រភេទឯកសារ «" + ct + "» មិនត្រឹមត្រូវតាមគោលការណ៍ប្រព័ន្ធឡើយ។ សូមប្រើប្រាស់ទម្រង់ឯកសាររូបភាពជាប្រភេទ .jpg, .jpeg, .png ឬ .webp ប៉ុណ្ណោះ។"
            );
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            double currentMb = file.getSize() / 1048576.0;
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
                && originalName.contains(".")) {
            ext = originalName.substring(
                    originalName.lastIndexOf("."));
        }
        return type.getFolder()
                + "/" + refId
                + "/"
                + java.util.UUID.randomUUID()
                .toString()
                .replace("-", "")
                + ext;
    }

    private void deleteAttachmentSafely(
            Integer attId, String ctx) {
        try {
            attachmentRepo.findById(attId)
                    .ifPresent(att -> {
                        minioService.delete(
                                att.getFilePath());
                        attachmentRepo
                                .deleteById(attId);
                    });
        } catch (Exception e) {
            log.warn(
                    "Attachment delete [{}]"
                            + " ctx={}: {}",
                    attId, ctx,
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
                    "មិនអាចដំណើរការបានឡើយ ព្រោះនាយកដ្ឋាន «" + dept.getDepartmentName() + "» កំពុងស្ថិតក្នុងស្ថានភាពមិនសកម្ម ឬផ្អាកដំណើរការជាបណ្តោះអាសន្ន។"
            );
        }
        return dept;
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

    private ContractOfficer findById(
            Integer id) {
        return contractRepo
                .findByIdWithAll(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រីកិច្ចសន្យា", id));
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
    public String getProfileImageUrl(Integer contractOfficerId) {

        ContractOfficer co = findById(contractOfficerId); 

        securityUtils.validateDepartmentScope(
                co.getDepartment() != null
                        ? co.getDepartment().getDepartmentId()
                        : null);

        if (co.getProfileAttachment() != null) {
            return minioService.getPresignedUrl(
                    co.getProfileAttachment().getFilePath());
        }

        // No uploaded photo: null → client placeholder (the static
        // path was never actually served; nothing maps /static/**).
        return null;
    }
}
