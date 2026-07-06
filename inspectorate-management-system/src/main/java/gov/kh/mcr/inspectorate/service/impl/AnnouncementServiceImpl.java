package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.Priority;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper
        .AnnouncementMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security
        .SecurityUtils;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation
        .Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Propagation;
import org.springframework.transaction.annotation
        .Transactional;
import org.springframework.web.context.request
        .RequestContextHolder;
import org.springframework.web.context.request
        .ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl
        implements AnnouncementService {
    private final AnnouncementRepository
            announcementRepo;
    private final AnnouncementRecipientRepository
            recipientRepo;
    private final LookupAnnouncementStatusRepository
            statusRepo;
    private final AttachmentRepository
            attachmentRepo;
    private final AnnouncementMapper
            announcementMapper;
    private final SecurityUtils
            securityUtils;
    private final ActivityLogService
            activityLogService;
    private final NotificationService
            notificationService;

    private final AttachmentService
            attachmentService;
    private final MinioService
            minioService;
    private final UserRepository userRepository;

    private List<User> resolveTargetUsers(
            User creator) {

        String roleName =
                creator.getRole() != null
                        ? creator.getRole().getRoleName()
                        : "";


        if ("SUPER_ADMIN".equals(roleName)
                || "ADMIN".equals(roleName)) {
            log.info(
                    "Target: ALL users"
                            + " (role={})", roleName);
            return userRepository.findAllActive();
        }

         Integer creatorDeptId =
                resolveUserDeptId(creator);

        if (creatorDeptId == null) {
            log.warn(
                    "Creator {} has no"
                            + " department — fallback"
                            + " to ALL active users",
                    creator.getUserId());
            return userRepository.findAllActive();
        }

        log.info(
                "Target: department={} users",
                creatorDeptId);

        return userRepository
                .findActiveByDepartmentId(
                        creatorDeptId);
    }

    private Integer resolveUserDeptId(
            User u) {
        if (u.getOfficer() != null
                && u.getOfficer()
                .getDepartment()
                != null) {
            return u.getOfficer()
                    .getDepartment()
                    .getDepartmentId();
        }
        if (u.getContractOfficer()
                != null
                && u.getContractOfficer()
                .getDepartment()
                != null) {
            return u.getContractOfficer()
                    .getDepartment()
                    .getDepartmentId();
        }
        return null;
    }

    private void
    validateCreatorDepartmentScope(
            Announcement announcement) {

        if (securityUtils
                .canBypassDepartmentScope()) {
            return;
        }

        Integer creatorDeptId =
                resolveCreatorDeptId(
                        announcement);

        securityUtils
                .validateDepartmentScope(
                        creatorDeptId);
    }

    private Integer resolveCreatorDeptId(
            Announcement announcement) {

        User creator =
                announcement.getCreatedBy();
        if (creator == null) return null;

        return resolveUserDeptId(creator);
    }

    private void validateExpireAt(
            AnnouncementRequest request) {

        if (request.getExpireAt() == null)
            return;

        if (!request.getExpireAt()
                .isAfter(
                        LocalDate.now())) {
            throw new BusinessException(
                    "កាលបរិច្ឆេទផុតកំណត់ ត្រូវតែជាកាលបរិច្ឆេទក្នុងថ្ងៃអនាគត (មិនអាចយកថ្ងៃបច្ចុប្បន្ន ឬថ្ងៃកន្លងទៅបានឡើយ)។");
        }

        if (request.getPublishAt() != null
                && !request.getExpireAt()
                .isAfter(
                        request
                                .getPublishAt()
                                .toLocalDate())) {
            throw new BusinessException(
                    "កាលបរិច្ឆេទផុតកំណត់ ត្រូវតែនៅក្រោយកាលបរិច្ឆេទផ្សព្វផ្សាយសេចក្តីជូនដំណឹងនេះ។");
        }
    }

    private AnnouncementResponse
    toResponseWithReadStatus(
            Announcement a) {

        AnnouncementResponse dto =
                announcementMapper
                        .toResponse(a);

        long total = recipientRepo
                .countByAnnouncement_AnnouncementId(
                        a.getAnnouncementId());
        long read = recipientRepo
                .countRead(
                        a.getAnnouncementId());

        dto.setTotalRecipients(total);
        dto.setReadCount(read);
        dto.setUnreadCount(total - read);

        try {
            Integer currentUserId =
                    securityUtils
                            .getCurrentUserId();

            recipientRepo
                    .findByAnnouncement_AnnouncementIdAndUser_UserId(
                            a.getAnnouncementId(),
                            currentUserId)
                    .ifPresentOrElse(
                            r -> {
                                dto.setCurrentUserRead(
                                        r.getIsRead());
                                dto.setCurrentUserReadAt(
                                        r.getReadAt());
                            },
                            () -> {
                                dto.setCurrentUserRead(
                                        null);
                                dto.setCurrentUserReadAt(
                                        null);
                            });

        } catch (Exception e) {
            dto.setCurrentUserRead(null);
            dto.setCurrentUserReadAt(null);
        }

        return dto;
    }

    private Announcement findById(
            Integer id) {
        return announcementRepo.findById(id)
                .orElseThrow(() ->
                        new
                                ResourceNotFoundException(
                                "ប្រកាស", id));
    }

    private LookupAnnouncementStatus
    findStatus(String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new
                                ResourceNotFoundException(
                                "ស្ថានភាពប្រកាស",
                                code));
    }

    private ActivityLogContext
    buildContext() {
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
//=====================

    @Override
    public void markAsRead(
            Integer announcementId) {

        findById(announcementId);

        Integer currentUserId =
                securityUtils
                        .getCurrentUserId();

        AnnouncementRecipient recipient =
                recipientRepo
                        .findByAnnouncement_AnnouncementIdAndUser_UserId(
                                announcementId,
                                currentUserId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "លោកអ្នកមិនអាចកំណត់ស្ថានភាពថាបានអានរួចឡើយ ព្រោះគណនីរបស់អ្នកមិនស្ថិតក្នុងបញ្ជីរាយនាមអ្នកទទួលសេចក្តីជូនដំណឹងនេះទេ។"));

        if (Boolean.TRUE.equals(
                recipient.getIsRead())) {
            return;
        }

        recipient.setIsRead(true);
        recipient.setReadAt(
                LocalDateTime.now());
        recipientRepo.save(recipient);

        log.info(
                "Mark read: ann={} user={}",
                announcementId, currentUserId);
    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<AnnouncementResponse>
    getAll(int page, int size,
           String status, String priority,
           Boolean includeExpired) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Priority priorityEnum =
                priority != null
                        ? Priority.valueOf(priority)
                        : null;

         boolean canSeeAll =
                securityUtils
                        .canBypassDepartmentScope();

        Page<Announcement> result;

        if (canSeeAll) {
            if (Boolean.TRUE.equals(
                    includeExpired)) {
                result = announcementRepo
                        .findAll(
                                status, priorityEnum,
                                pageable);
            } else {
                result = announcementRepo
                        .findActive(
                                status, priorityEnum,
                                LocalDate.now(),
                                pageable);
            }
        } else {

            Integer currentUserId =
                    securityUtils
                            .getCurrentUserId();

            result = announcementRepo
                    .findVisibleToUser(
                            currentUserId,
                            status, priorityEnum,
                            LocalDate.now(),
                            pageable);
        }

        return PageResponse.of(result.map(
                this::toResponseWithReadStatus));
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse getById(
            Integer id) {

        Announcement a = findById(id);

        if (!securityUtils
                .canBypassDepartmentScope()) {

            Integer currentUserId =
                    securityUtils
                            .getCurrentUserId();


            boolean isRecipient =
                    recipientRepo
                            .existsByAnnouncement_AnnouncementIdAndUser_UserId(
                                    id, currentUserId);

            boolean isCreator =
                    a.getCreatedBy() != null
                            && a.getCreatedBy()
                            .getUserId()
                            .equals(currentUserId);

            if (!isRecipient && !isCreator) {
                throw new
                        ResourceNotFoundException(
                        "ប្រកាស", id);
            }
        }

        return toResponseWithReadStatus(a);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementReadStatusResponse
    getReadStatus(Integer id) {

        findById(id);

        long total = recipientRepo
                .countByAnnouncement_AnnouncementId(
                        id);
        long read = recipientRepo
                .countRead(id);

        return AnnouncementReadStatusResponse
                .builder()
                .announcementId(id)
                .totalRecipients(total)
                .readCount(read)
                .unreadCount(total - read)
                .build();
    }
    @Override
    public AnnouncementResponse create(
            AnnouncementRequest request) {

        validateExpireAt(request);

        Announcement announcement =
                announcementMapper.toEntity(
                        request);

        User creator =
                securityUtils.getCurrentUser()
                        .orElseThrow(() ->
                                new UnauthorizedException(
                                        "សូមចូលប្រើប្រាស់ប្រព័ន្ធ (Login) ជាមុនសិន ដើម្បីមានសិទ្ធិអនុវត្តមុខងារនេះ។"));

        announcement.setCreatedBy(creator);

        announcement.setStatusCode(
                findStatus(
                        request.getStatusCode()));

        Announcement saved =
                announcementRepo.save(
                        announcement);

        createRecipientsSync(saved, creator);

        activityLogService.log(
                "CREATE", "Announcement",
                saved.getAnnouncementId(),
                "បង្កើត: " + saved.getTitle(),
                buildContext());

        return toResponseWithReadStatus(saved);
    }

    private void createRecipientsSync(
            Announcement announcement,
            User creator) {

        List<User> targetUsers =
                resolveTargetUsers(creator);

        if (targetUsers.isEmpty()) {
            log.warn(
                    "No target users for"
                            + " announcement {}",
                    announcement
                            .getAnnouncementId());
            return;
        }

        int created = 0;
        for (User user : targetUsers) {

            if (recipientRepo
                    .existsByAnnouncement_AnnouncementIdAndUser_UserId(
                            announcement
                                    .getAnnouncementId(),
                            user.getUserId())) {
                continue;
            }

            recipientRepo.save(
                    AnnouncementRecipient
                            .builder()
                            .announcement(
                                    announcement)
                            .user(user)
                            .isRead(false)
                            .build());

            created++;
        }

        log.info(
                "Created {} recipients"
                        + " for ann={} (dept-scoped)",
                created,
                announcement
                        .getAnnouncementId());

        notifyRecipientsAsync(
                announcement, targetUsers);
    }

    @Async
    @Transactional(
            propagation = Propagation.REQUIRES_NEW)
    public void notifyRecipientsAsync(
            Announcement announcement,
            List<User> users) {

        for (User user : users) {
            try {
                notificationService
                        .createByUserId(
                                user.getUserId(),
                                "សេចក្ដីប្រកាសថ្មី",
                                announcement.getTitle(),
                                gov.kh.mcr.inspectorate
                                        .enums
                                        .NotificationType
                                        .ANNOUNCEMENT,
                                announcement
                                        .getAnnouncementId());
            } catch (Exception ex) {
                log.error(
                        "Notify failed user={}: {}",
                        user.getUserId(),
                        ex.getMessage());
            }
        }
    }

    @Override
    public AnnouncementResponse update(
            Integer id,
            AnnouncementRequest request) {

        Announcement announcement =
                findById(id);

        validateCreatorDepartmentScope(
                announcement);

        validateExpireAt(request);

        String code =
                announcement.getStatusCode()
                        != null
                        ? announcement.getStatusCode()
                        .getStatusCode()
                        : "";

        if ("PUBLISHED".equals(code)) {
            throw new BusinessException(
                    "មិនអាចកែប្រែសេចក្តីជូនដំណឹងនេះបានឡើយ ព្រោះត្រូវបាន «ផ្សព្វផ្សាយជាផ្លូវការ» រួចរាល់ហើយ។");
        }

        announcementMapper
                .updateEntity(
                        request, announcement);

        announcement.setStatusCode(
                findStatus(
                        request.getStatusCode()));

        activityLogService.log(
                "UPDATE", "Announcement", id,
                "កែប្រែ: "
                        + announcement.getTitle(),
                buildContext());

        return toResponseWithReadStatus(
                announcementRepo.save(
                        announcement));
    }

    @Override
    public void delete(Integer id) {

        Announcement announcement =
                findById(id);

        validateCreatorDepartmentScope(
                announcement);

        String code =
                announcement.getStatusCode()
                        != null
                        ? announcement.getStatusCode()
                        .getStatusCode()
                        : "";

        if ("PUBLISHED".equals(code)) {
            throw new BusinessException(
                    "មិនអាចលុបសេចក្តីជូនដំណឹងនេះបានឡើយ ព្រោះត្រូវបាន «ផ្សព្វផ្សាយជាផ្លូវការ» រួចរាល់ហើយ។");
        }

        recipientRepo
                .deleteByAnnouncement_AnnouncementId(
                        id);

        announcementRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "Announcement", id,
                "លុប: "
                        + announcement.getTitle(),
                buildContext());
    }
    @Override
    public AnnouncementResponse uploadAttachment(
            Integer announcementId,
            MultipartFile file) {

        Announcement announcement =
                findById(announcementId);

        validateCanEdit(announcement);

        var resp = attachmentService.upload(
                file,
                gov.kh.mcr.inspectorate.enums
                        .AttachmentRefType.ANNOUNCEMENT,
                announcementId);

        attachmentRepo
                .findById(resp.getAttachmentId())
                .ifPresent(att -> {
                    announcement.setAttachment(att);
                    announcementRepo.save(announcement);
                });

        activityLogService.log(
                "UPDATE", "Announcement",
                announcementId,
                "Upload: "
                        + resp.getOriginalName(),
                buildContext());

        log.info(
                "Attachment uploaded ann={} file={}",
                announcementId,
                resp.getOriginalName());

        return toResponseWithReadStatus(
                announcementRepo.save(announcement));
    }

    @Override
    @Transactional(readOnly = true)
    public String getAttachmentUrl(
            Integer announcementId) {

        Announcement announcement =
                findById(announcementId);

        if (announcement.getAttachment() == null) {
            throw new ResourceNotFoundException(
                    "ឯកសារភ្ជាប់នៃសេចក្តីជូនដំណឹង",
                    announcementId);
        }

        return minioService.getPresignedUrl(
                announcement.getAttachment()
                        .getFilePath());
    }

    private void validateCanEdit(
            Announcement announcement) {

        String code =
                announcement.getStatusCode() != null
                        ? announcement.getStatusCode()
                        .getStatusCode()
                        : "";

        if ("ARCHIVED".equals(code)
                || "CANCELLED".equals(code)) {
            String statusKh = "ARCHIVED".equals(code) ? "«រក្សាទុកក្នុងបណ្ណសារ»" : "«ត្រូវបានលុបចោល»";
            throw new BusinessException(
                    "មិនអាចបង្ហោះឯកសារភ្ជាប់បានឡើយ ព្រោះសេចក្តីជូនដំណឹងនេះស្ថិតក្នុងស្ថានភាព " + statusKh + " រួចរាល់ហើយ។");
        }
    }

}