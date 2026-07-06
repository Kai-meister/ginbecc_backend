package gov.kh.mcr.inspectorate.resolver;

import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import gov.kh.mcr.inspectorate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationReferenceResolver {

    private final MeetingRepository
            meetingRepo;
    private final DocumentRepository
            documentRepo;
    private final AnnouncementRepository
            announcementRepo;

    private static final DateTimeFormatter
            DATE_FMT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy");


    public Map<String, Object> resolve(
            NotificationType type,
            Integer referenceId) {

        if (type == null
                || referenceId == null) {
            return null;
        }

        try {
            return switch (type) {
                case MEETING ->
                        resolveMeeting(referenceId);
                case DOCUMENT ->
                        resolveDocument(referenceId);
                case ANNOUNCEMENT ->
                        resolveAnnouncement(
                                referenceId);
                case SYSTEM -> null;
            };
        } catch (Exception e) {
            log.warn(
                    "Resolve reference failed:"
                            + " type={} refId={}: {}",
                    type, referenceId,
                    e.getMessage());
            return null;
        }
    }

    private Map<String, Object>
    resolveMeeting(Integer id) {

        return meetingRepo.findById(id)
                .map(m -> {
                    Map<String, Object> data =
                            new HashMap<>();
                    data.put("meetingId",
                            m.getMeetingId());
                    data.put("title",
                            m.getTitle());
                    data.put("meetingDate",
                            m.getMeetingDate() != null
                                    ? m.getMeetingDate()
                                    .format(DATE_FMT)
                                    : null);
                    data.put("startTime",
                            m.getStartTime() != null
                                    ? m.getStartTime()
                                    .toString()
                                    : null);
                    data.put("endTime",
                            m.getEndTime() != null
                                    ? m.getEndTime()
                                    .toString()
                                    : null);
                    data.put("roomCode",
                            m.getRoom() != null
                                    ? m.getRoom()
                                    .getRoomCode()
                                    : "Online");
                    data.put("statusCode",
                            m.getStatusCode() != null
                                    ? m.getStatusCode()
                                    .getStatusCode()
                                    : null);
                    data.put("statusLabel",
                            m.getStatusCode() != null
                                    ? m.getStatusCode()
                                    .getLabelKh()
                                    : null);
                    return data;
                })
                .orElse(null);
    }


    private Map<String, Object>
    resolveDocument(Integer id) {

        return documentRepo.findById(id)
                .map(d -> {
                    Map<String, Object> data =
                            new HashMap<>();
                    data.put("documentId",
                            d.getDocumentId());
                    data.put("documentName",
                            d.getDocumentName());
                    data.put("documentNumber",
                            d.getDocumentNumber());
                    data.put("documentTypeName",
                            d.getDocumentType() != null
                                    ? d.getDocumentType()
                                    .getDocumentTypeName()
                                    : null);
                    data.put("UserName",
                            d.getUser() != null
                                    ? d.getUser()
                                    .getUserNameKh()
                                    : null);
                    data.put("statusCode",
                            d.getStatusCode() != null
                                    ? d.getStatusCode()
                                    .getStatusCode()
                                    : null);
                    data.put("statusLabel",
                            d.getStatusCode() != null
                                    ? d.getStatusCode()
                                    .getLabelKh()
                                    : null);
                    data.put("expiryDate",
                            d.getExpiryDate() != null
                                    ? d.getExpiryDate()
                                    .format(DATE_FMT)
                                    : null);
                    return data;
                })
                .orElse(null);
    }
    private Map<String, Object>
    resolveAnnouncement(Integer id) {

        return announcementRepo.findById(id)
                .map(a -> {
                    Map<String, Object> data =
                            new HashMap<>();
                    data.put("announcementId",
                            a.getAnnouncementId());
                    data.put("title",
                            a.getTitle());
                    data.put("content",
                            trunc(a.getContent(), 150));
                    data.put("priority",
                            a.getPriority() != null
                                    ? a.getPriority().name()
                                    : null);
                    data.put("statusCode",
                            a.getStatusCode() != null
                                    ? a.getStatusCode()
                                    .getStatusCode()
                                    : null);
                    data.put("statusLabel",
                            a.getStatusCode() != null
                                    ? a.getStatusCode()
                                    .getLabelKh()
                                    : null);
                    data.put("expireAt",
                            a.getExpireAt() != null
                                    ? a.getExpireAt()
                                    .format(DATE_FMT)
                                    : null);
                    return data;
                })
                .orElse(null);
    }

    private String trunc(String t, int max) {
        if (t == null) return "";
        return t.length() > max
                ? t.substring(0, max) + "..."
                : t;
    }
}