package gov.kh.mcr.inspectorate.service.impl;
import gov.kh.mcr.inspectorate.dto.request.AnnouncementRequest;
import gov.kh.mcr.inspectorate.dto.response.AnnouncementReadStatusResponse;
import gov.kh.mcr.inspectorate.dto.response.AnnouncementResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.Announcement;
import gov.kh.mcr.inspectorate.entity.AnnouncementRecipient;
import gov.kh.mcr.inspectorate.enums.Priority;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.AnnouncementMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.AnnouncementService;
import gov.kh.mcr.inspectorate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementRecipientRepository recipientRepository;
    private final OfficerRepository officerRepository;
    private final AttachmentRepository attachmentRepository;
    private final LookupAnnouncementStatusRepository lookupStatusRepository;
    private final AnnouncementMapper announcementMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AnnouncementResponse> getAll(
            int page, int size, String status, String priority) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<Announcement> result;

        if (status != null && priority != null) {
            result = announcementRepository
                    .findByStatusCode_StatusCodeAndPriority(
                            status,
                            Priority.valueOf(priority),
                            pageable);
        } else if (status != null) {
            result = announcementRepository
                    .findByStatusCode_StatusCode(status, pageable);
        } else {
            result = announcementRepository.findAll(pageable);
        }

        return PageResponse.of(result.map(a -> {
            AnnouncementResponse dto =
                    announcementMapper.toResponse(a);
            dto.setTotalRecipients(
                    (long) recipientRepository
                            .findByAnnouncement_AnnouncementId(
                                    a.getAnnouncementId()).size());
            dto.setReadCount(
                    recipientRepository.countRead(
                            a.getAnnouncementId()));
            return dto;
        }));
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse getById(Integer id) {
        Announcement a = findById(id);
        AnnouncementResponse dto = announcementMapper.toResponse(a);
        dto.setTotalRecipients(
                (long) recipientRepository
                        .findByAnnouncement_AnnouncementId(id).size());
        dto.setReadCount(recipientRepository.countRead(id));
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementReadStatusResponse getReadStatus(Integer id) {
        findById(id);
        List<AnnouncementRecipient> recipients =
                recipientRepository.findByAnnouncement_AnnouncementId(id);

        List<AnnouncementReadStatusResponse.RecipientStatus> statuses =
                recipients.stream()
                        .map(r -> AnnouncementReadStatusResponse
                                .RecipientStatus.builder()
                                .officerId(r.getOfficer().getOfficerId())
                                .officerName(r.getOfficer().getFullNameKh())
                                .isRead(r.getIsRead())
                                .readAt(r.getReadAt())
                                .build())
                        .toList();

        long read   = recipients.stream()
                .filter(AnnouncementRecipient::getIsRead).count();
        long unread = recipients.size() - read;

        return AnnouncementReadStatusResponse.builder()
                .announcementId(id)
                .totalRecipients((long) recipients.size())
                .readCount(read)
                .unreadCount(unread)
                .recipients(statuses)
                .build();
    }

    @Override
    public AnnouncementResponse create(AnnouncementRequest request) {
        Announcement announcement =
                announcementMapper.toEntity(request);

        securityUtils.getCurrentUser()
                .ifPresent(announcement::setCreatedBy);

        lookupStatusRepository.findById(request.getStatusCode())
                .ifPresent(announcement::setStatusCode);

        if (request.getAttachmentPathId() != null) {
            attachmentRepository
                    .findById(request.getAttachmentPathId())
                    .ifPresent(announcement::setAttachmentPath);
        }

        Announcement saved = announcementRepository.save(announcement);

        if (request.getRecipientOfficerIds() != null) {
            request.getRecipientOfficerIds().forEach(officerId ->
                    officerRepository.findById(officerId)
                            .ifPresent(officer -> {
                                recipientRepository.save(
                                        AnnouncementRecipient.builder()
                                                .announcement(saved)
                                                .officer(officer)
                                                .isRead(false)
                                                .build());
                                notificationService.createNotification(
                                        officerId,
                                        "សេចក្តីប្រកាសថ្មី",
                                        saved.getTitle(),
                                        "ANNOUNCEMENT",
                                        saved.getAnnouncementId());
                            }));
        }

        activityLogService.log("CREATE", "Announcement",
                saved.getAnnouncementId(),
                "បង្កើត: " + saved.getTitle());

        AnnouncementResponse dto = announcementMapper.toResponse(saved);
        dto.setTotalRecipients(
                request.getRecipientOfficerIds() != null
                        ? (long) request.getRecipientOfficerIds().size()
                        : 0L);
        dto.setReadCount(0L);
        return dto;
    }

    @Override
    public AnnouncementResponse update(
            Integer id, AnnouncementRequest request) {
        Announcement announcement = findById(id);

        announcementMapper.updateEntity(request, announcement);

        lookupStatusRepository
                .findById(request.getStatusCode())
                .ifPresent(announcement::setStatusCode);

        if (request.getAttachmentPathId() != null) {
            attachmentRepository
                    .findById(request.getAttachmentPathId())
                    .ifPresent(announcement::setAttachmentPath);
        }

        activityLogService.log("UPDATE", "Announcement",
                id, "កែប្រែ: " + announcement.getTitle());

        Announcement saved =
                announcementRepository.save(announcement);

        AnnouncementResponse dto =
                announcementMapper.toResponse(saved);
        dto.setTotalRecipients(
                (long) recipientRepository
                        .findByAnnouncement_AnnouncementId(id).size());
        dto.setReadCount(recipientRepository.countRead(id));
        return dto;
    }

    @Override
    public void markAsRead(Integer announcementId, Integer officerId) {
        AnnouncementRecipient recipient = recipientRepository
                .findByAnnouncement_AnnouncementIdAndOfficer_OfficerId(
                        announcementId, officerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ព័ត៌មានទទួល", announcementId));
        recipient.setIsRead(true);
        recipient.setReadAt(LocalDateTime.now());
        recipientRepository.save(recipient);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        announcementRepository.deleteById(id);
        activityLogService.log("DELETE", "Announcement",
                id, "លុបសេចក្តីប្រកាស");
    }

    private Announcement findById(Integer id) {
        return announcementRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "សេចក្តីប្រកាស", id));
    }
}
