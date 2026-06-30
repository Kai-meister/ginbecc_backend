package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.response
        .report.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import gov.kh.mcr.inspectorate.enums
        .Priority;
import gov.kh.mcr.inspectorate.exception
        .BusinessException;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.service
        .ReportService;
import gov.kh.mcr.inspectorate.util
        .ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl
        implements ReportService {

    private final OfficerRepository
            officerRepo;
    private final ContractOfficerRepository
            contractRepo;
    private final DocumentRepository
            documentRepo;
    private final ApprovalRepository
            approvalRepo;
    private final MeetingRepository
            meetingRepo;
    private final MeetingAttendeeRepository
            attendeeRepo;
    private final MeetingMinuteRepository
            minuteRepo;
    private final AnnouncementRepository
            announcementRepo;
    private final AnnouncementRecipientRepository
            recipientRepo;
    private final ActivityLogRepository
            logRepo;
    private final NotificationRepository
            notifRepo;
    private final UserRepository
            userRepo;


    @Override
    public byte[] exportOfficers(
            Integer deptId, String status,
            LocalDate from, LocalDate to) {
        var list = previewOfficers(
                deptId, status, from, to);
        log.info("Export Officers: {}",
                list.size());
        return ExcelUtils.officers(list, deptId, status, from, to);
    }

    @Override
    public List<OfficerReportResponse>
    previewOfficers(
            Integer deptId, String status,
            LocalDate from, LocalDate to) {

        AtomicInteger no = new AtomicInteger(1);

        return officerRepo
                .findForReport(
                        deptId, status, from, to)
                .stream()
                .map(o ->
                        OfficerReportResponse.builder()
                                .no(no.getAndIncrement())
                                .officerCode(
                                        nvl(o.getOfficerCode()))
                                .fullNameKh(
                                        nvl(o.getFullNameKh()))
                                .fullNameEn(
                                        o.getFullNameEn())
                                .genderLabel(
                                        o.getGender() != null
                                                ? genderKh(o.getGender()
                                                .name()) : "")
                                .dob(o.getDob())
                                .age(calcAge(o.getDob()))
                                .departmentName(
                                        o.getDepartment() != null
                                                ? o.getDepartment()
                                                .getDepartmentName()
                                                : "គ្មាន")
                                .positionName(
                                        o.getPosition() != null
                                                ? o.getPosition()
                                                .getPositionName()
                                                : "")
                                .joinDate(o.getJoinDate())
                                .phone(o.getPhone())
                                .email(o.getEmail())
                                .educationLevel(
                                        o.getEducationLevel())
                                .statusCode(
                                        o.getStatusCode() != null
                                                ? o.getStatusCode()
                                                .getStatusCode()
                                                : "UNKNOWN")
                                .statusLabel(
                                        o.getStatusCode() != null
                                                ? o.getStatusCode()
                                                .getLabelKh()
                                                : "មិនស្គាល់")
                                .build())
                .toList();
    }



    @Override
    public byte[] exportContractOfficers(
            Integer days) {
        var list =
                previewContractOfficers(days);
        log.info("Export Contract: {}",
                list.size());
        return ExcelUtils.contractOfficers(list,days);
    }

    @Override
    public List<ContractOfficerReportResponse>
    previewContractOfficers(
            Integer days) {

        LocalDate expiry = LocalDate.now()
                .plusDays(days != null
                        ? days : 30);

        AtomicInteger no = new AtomicInteger(1);

        return contractRepo
                .findExpiring(expiry)
                .stream()
                .map(c -> {
                    long daysLeft =
                            c.getEndDate() != null
                                    ? ChronoUnit.DAYS.between(
                                    LocalDate.now(),
                                    c.getEndDate())
                                    : 0;

                    return ContractOfficerReportResponse
                            .builder()
                            .no(no.getAndIncrement())
                            .contractOfficerCode(
                                    nvl(c
                                            .getContractOfficerCode()))
                            .fullNameKh(
                                    nvl(c.getFullNameKh()))
                            .fullNameEn(
                                    c.getFullNameEn())
                            .genderLabel(
                                    c.getGender() != null
                                            ? genderKh(c.getGender()
                                            .name()) : "")
                            .dob(c.getDob())
                            .age(calcAge(c.getDob()))
                            .departmentName(
                                    c.getDepartment() != null
                                            ? c.getDepartment()
                                            .getDepartmentName()
                                            : "គ្មាន")
                            .jobLevel(c.getJobLevel())
                            .accountingCode(
                                    c.getAccountingCode())
                            .startDate(c.getStartDate())
                            .endDate(c.getEndDate())
                            .daysUntilExpiry(
                                    Math.max(0L, daysLeft))
                            .expiryLabel(
                                    expiryLabel(daysLeft))
                            .note(c.getNote())
                            .statusCode(
                                    c.getStatusCode() != null
                                            ? c.getStatusCode()
                                            .getStatusCode()
                                            : "UNKNOWN")
                            .statusLabel(
                                    c.getStatusCode() != null
                                            ? c.getStatusCode()
                                            .getLabelKh()
                                            : "មិនស្គាល់")
                            .build();
                })
                .toList();
    }

    @Override
    public byte[] exportDocuments(
            Integer userId, String status,
            Integer typeId,
            LocalDate from, LocalDate to) {
        var list = previewDocuments(
                userId, status, typeId,
                from, to);
        log.info("Export Documents: {}",
                list.size());
        return ExcelUtils.documents(list);
    }

    @Override
    public List<DocumentReportResponse>
    previewDocuments(
            Integer userId, String status,
            Integer typeId,
            LocalDate from, LocalDate to) {

        AtomicInteger no = new AtomicInteger(1);

        return documentRepo
                .findForReport(
                        userId, status, typeId,
                        from, to)
                .stream()
                .map(d ->
                        DocumentReportResponse.builder()
                                .no(no.getAndIncrement())
                                .documentNumber(
                                        d.getDocumentNumber())
                                .documentName(
                                        nvl(d.getDocumentName()))
                                .documentTypeName(
                                        d.getDocumentType() != null
                                                ? d.getDocumentType()
                                                .getDocumentTypeName()
                                                : "គ្មានប្រភេទ")
                                .userName(
                                        d.getUser() != null
                                                ? d.getUser()
                                                .getUserNameKh()
                                                : "គ្មាន User")
                                .departmentName(
                                        resolveUserDeptName(
                                                d.getUser()))
                                .expiryDate(
                                        d.getExpiryDate())
                                .isExpired(
                                        d.getExpiryDate() != null
                                                && d.getExpiryDate()
                                                .isBefore(
                                                        LocalDate.now()))
                                .statusCode(
                                        d.getStatusCode() != null
                                                ? d.getStatusCode()
                                                .getStatusCode()
                                                : "UNKNOWN")
                                .statusLabel(
                                        d.getStatusCode() != null
                                                ? d.getStatusCode()
                                                .getLabelKh()
                                                : "មិនស្គាល់")
                                .uploadedBy(
                                        d.getUploadedBy() != null
                                                ? d.getUploadedBy()
                                                .getUserNameKh()
                                                : "")
                                .createdAt(d.getCreatedAt())
                                .build())
                .toList();
    }

    @Override
    public byte[] exportApprovals(
            String status, Integer userId,
            LocalDate from, LocalDate to) {
        var list = previewApprovals(
                status, userId, from, to);
        log.info("Export Approvals: {}",
                list.size());
        return ExcelUtils.approvals(list);
    }

    @Override
    public List<ApprovalReportResponse>
    previewApprovals(
            String status, Integer userId,
            LocalDate from, LocalDate to) {

        LocalDateTime fromDt = from != null
                ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null
                ? to.atTime(23, 59, 59) : null;

        AtomicInteger no = new AtomicInteger(1);

        return approvalRepo
                .findForReport(
                        status, userId,
                        fromDt, toDt)
                .stream()
                .map(a -> {

                    Document doc = a.getDocument();
                    User requester =
                            doc != null
                                    ? doc.getUser() : null;

                    return ApprovalReportResponse
                            .builder()
                            .no(no.getAndIncrement())
                            .documentName(
                                    doc != null
                                            ? doc.getDocumentName()
                                            : "គ្មានឯកសារ")
                            .documentNumber(
                                    doc != null
                                            ? doc.getDocumentNumber()
                                            : "")
                            .requesterName(
                                    requester != null
                                            ? requester
                                            .getUserNameKh()
                                            : "គ្មាន")
                            .requesterDept(
                                    resolveUserDeptName(
                                            requester))
                            .departmentName(
                                    a.getDepartment()
                                            != null
                                            ? a.getDepartment()
                                            .getDepartmentName()
                                            : "")
                            .approvedBy(
                                    a.getApprovedBy()
                                            != null
                                            ? a.getApprovedBy()
                                            .getUserNameKh()
                                            : "")
                            .statusCode(
                                    a.getStatusCode()
                                            != null
                                            ? a.getStatusCode()
                                            .getStatusCode()
                                            : "UNKNOWN")
                            .statusLabel(
                                    a.getStatusCode()
                                            != null
                                            ? a.getStatusCode()
                                            .getLabelKh()
                                            : "មិនស្គាល់")
                            .comment(a.getComment())
                            .requestedAt(
                                    a.getRequestedAt())
                            .decidedAt(a.getDecidedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public byte[] exportMeetings(
            int month, int year,
            String status) {
        var list = previewMeetings(
                month, year, status);
        log.info("Export Meetings: {}",
                list.size());
        return ExcelUtils.meetings(list, month, year, status);
    }

    @Override
    public List<MeetingReportResponse>
    previewMeetings(
            int month, int year,
            String status) {

        AtomicInteger no = new AtomicInteger(1);

        return meetingRepo
                .findForReport(
                        month, year, status)
                .stream()
                .map(m -> {
                    Integer mid =
                            m.getMeetingId();
                    int total = (int) attendeeRepo
                            .countByMeeting_MeetingId(
                                    mid);
                    int attended = (int) attendeeRepo
                            .countByMeeting_MeetingIdAndAttendanceStatus(
                                    mid,
                                    gov.kh.mcr.inspectorate
                                            .enums.AttendanceStatus
                                            .ATTENDED);
                    int absent = (int) attendeeRepo
                            .countByMeeting_MeetingIdAndAttendanceStatus(
                                    mid,
                                    gov.kh.mcr.inspectorate
                                            .enums.AttendanceStatus
                                            .ABSENT);

                    return MeetingReportResponse
                            .builder()
                            .no(no.getAndIncrement())
                            .title(nvl(m.getTitle()))
                            .meetingType(
                                    m.getMeetingType()
                                            != null
                                            ? m.getMeetingType()
                                            .name()
                                            : "")
                            .meetingDate(
                                    m.getMeetingDate())
                            .startTime(
                                    m.getStartTime())
                            .endTime(m.getEndTime())
                            .roomCode(
                                    m.getRoom() != null
                                            ? m.getRoom()
                                            .getRoomCode()
                                            : "Online")
                            .organizerName(
                                    m.getOrganizer()
                                            != null
                                            ? m.getOrganizer()
                                            .getUserNameKh()
                                            : "")
                            .totalAttendees(total)
                            .attendedCount(attended)
                            .absentCount(absent)
                            .statusCode(
                                    m.getStatusCode()
                                            != null
                                            ? m.getStatusCode()
                                            .getStatusCode()
                                            : "UNKNOWN")
                            .statusLabel(
                                    m.getStatusCode()
                                            != null
                                            ? m.getStatusCode()
                                            .getLabelKh()
                                            : "មិនស្គាល់")
                            .build();
                })
                .toList();
    }

    @Override
    public byte[] exportMeetingMinutes(
            int month, int year, Long meetingId) {
        var list = previewMeetingMinutes(month, year, meetingId);
        log.info("Export Minutes: {}", list.size());
        return ExcelUtils.meetingMinutes(list);
    }

    @Override
    public List<MeetingMinuteReportResponse> previewMeetingMinutes(
            int month, int year, Long meetingId) {

        AtomicInteger no = new AtomicInteger(1);
        return minuteRepo
                .findForReport(month, year, meetingId)
                .stream()
                .map(m ->
                        MeetingMinuteReportResponse
                                .builder()
                                .no(no.getAndIncrement())
                                .meetingId(
                                        m.getMeeting() != null
                                                ? Long.valueOf(m.getMeeting().getMeetingId())
                                                : null).meetingTitle(
                                        m.getMeeting() != null
                                                ? m.getMeeting().getTitle()
                                                : "គ្មានប្រជុំ")
                                .meetingDate(
                                        m.getMeeting() != null
                                                ? m.getMeeting().getMeetingDate()
                                                : null)
                                .recordedBy(
                                        m.getRecordedBy() != null
                                                ? m.getRecordedBy().getUserNameKh()
                                                : "")
                                .summary(m.getSummary())
                                .decisions(m.getDecisions())
                                .actionItems(m.getActionItems())
                                .hasAttachment(m.getAttachment() != null)
                                .createdAt(m.getCreatedAt())
                                .build())
                .toList();
    }

    @Override
    public byte[] exportAnnouncements(
            String status, String priority,
            LocalDate from, LocalDate to) {
        var list = previewAnnouncements(
                status, priority, from, to);
        log.info("Export Announcements: {}",
                list.size());
        return ExcelUtils.announcements(list,status, priority, from, to);
    }

    @Override
    public List<AnnouncementReportResponse>
    previewAnnouncements(
            String status, String priority,
            LocalDate from, LocalDate to) {

        Priority priorityEnum =
                resolvePriority(priority);

        AtomicInteger no = new AtomicInteger(1);

        return announcementRepo
                .findForReport(
                        status, priorityEnum,
                        from, to)
                .stream()
                .map(a -> {
                    Integer annId =
                            a.getAnnouncementId();

                    long total = recipientRepo
                            .countByAnnouncement_AnnouncementId(
                                    annId);
                    long read = recipientRepo
                            .countRead(annId);
                    long unread = total - read;

                    String rate = total > 0
                            ? String.format("%.0f%%",
                            (read * 100.0)
                            / total)
                            : "0%";

                    boolean expired =
                            a.getExpireAt() != null
                                    && LocalDate.now()
                                    .isAfter(
                                            a.getExpireAt());

                    return AnnouncementReportResponse
                            .builder()
                            .no(no.getAndIncrement())
                            .title(nvl(a.getTitle()))
                            .content(
                                    trunc(a.getContent(),
                                            100))
                            .createdBy(
                                    a.getCreatedBy()
                                            != null
                                            ? a.getCreatedBy()
                                            .getUserNameKh()
                                            : "គ្មាន")
                            .createdByDept(
                                    resolveUserDeptName(
                                            a.getCreatedBy()))
                            .priority(
                                    a.getPriority()
                                            != null
                                            ? a.getPriority()
                                            .name()
                                            : "MEDIUM")
                            .priorityLabel(
                                    a.getPriority()
                                            != null
                                            ? priorityKh(
                                            a.getPriority()
                                                    .name())
                                            : "មធ្យម")
                            .statusCode(
                                    a.getStatusCode()
                                            != null
                                            ? a.getStatusCode()
                                            .getStatusCode()
                                            : "")
                            .statusLabel(
                                    a.getStatusCode()
                                            != null
                                            ? a.getStatusCode()
                                            .getLabelKh()
                                            : "")
                            .publishAt(
                                    a.getPublishAt())
                            .expireAt(
                                    a.getExpireAt())
                            .isExpired(expired)
                            .totalRecipients(total)
                            .readCount(read)
                            .unreadCount(unread)
                            .readRate(rate)
                            .createdAt(
                                    a.getCreatedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public byte[] exportAnnouncementRecipients(
            Integer announcementId,
            Boolean isRead,
            LocalDate from, LocalDate to) {
        var list =
                previewAnnouncementRecipients(
                        announcementId, isRead,
                        from, to);
        log.info("Export Recipients: {}",
                list.size());
        return ExcelUtils
                .announcementRecipients(list);
    }

    @Override
    public List<AnnouncementRecipientReportResponse>
    previewAnnouncementRecipients(
            Integer announcementId,
            Boolean isRead,
            LocalDate from, LocalDate to) {

        AtomicInteger no = new AtomicInteger(1);

        return recipientRepo
                .findRecipientsForReport(
                        announcementId, isRead,
                        from, to)
                .stream()
                .map(r -> {
                    User u = r.getUser();

                    return AnnouncementRecipientReportResponse
                            .builder()
                            .no(no.getAndIncrement())
                            .announcementTitle(
                                    r.getAnnouncement()
                                            != null
                                            ? r.getAnnouncement()
                                            .getTitle()
                                            : "")
                            .receiverName(
                                    u != null
                                            ? u.getUserNameKh()
                                            : "")
                            .receiverEmail(
                                    u != null
                                            ? u.getEmail()
                                            : "")
                            .departmentName(
                                    resolveUserDeptName(u))
                            .isRead(
                                    Boolean.TRUE.equals(
                                            r.getIsRead()))
                            .readStatus(
                                    Boolean.TRUE.equals(
                                            r.getIsRead())
                                            ? "អានរួច"
                                            : "មិនទាន់អាន")
                            .readAt(r.getReadAt())
                            .createdAt(
                                    r.getCreatedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public byte[] exportAuditLogs(
            Integer userId, String action,
            String entityType,
            LocalDate from, LocalDate to) {
        var list = previewAuditLogs(
                userId, action, entityType,
                from, to);
        log.info("Export AuditLogs: {}",
                list.size());
        return ExcelUtils.auditLogs(list,action,entityType,from, to);
    }

    @Override
    public List<AuditLogReportResponse>
    previewAuditLogs(
            Integer userId, String action,
            String entityType,
            LocalDate from, LocalDate to) {

        LocalDateTime fromDt = from != null
                ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null
                ? to.atTime(23, 59, 59) : null;

        AtomicInteger no = new AtomicInteger(1);

        return logRepo
                .findWithFilters(
                        userId, action, entityType,
                        fromDt, toDt,
                        Pageable.unpaged())
                .getContent()
                .stream()
                .map(l ->
                        AuditLogReportResponse.builder()
                                .no(no.getAndIncrement())
                                .userNameKh(
                                        l.getUser() != null
                                                ? l.getUser()
                                                .getUserNameKh()
                                                : "SYSTEM")
                                .userEmail(
                                        l.getUserEmail()
                                                != null
                                                ? l.getUserEmail()
                                                : "system")
                                .action(l.getAction())
                                .actionLabel(
                                        actionKh(
                                                l.getAction()))
                                .entityType(
                                        l.getEntityType())
                                .entityId(
                                        l.getEntityId())
                                .details(l.getDetails())
                                .ipAddress(
                                        l.getIpAddress())
                                .createdAt(
                                        l.getCreatedAt())
                                .build())
                .toList();
    }

    @Override
    public byte[] exportNotifications(
            Integer userId, String type,
            Boolean isRead,
            LocalDate from, LocalDate to) {
        var list = previewNotifications(
                userId, type, isRead,
                from, to);
        log.info("Export Notifications: {}",
                list.size());
        return ExcelUtils.notifications(list,type,from,to);
    }

    @Override
    public List<NotificationReportResponse>
    previewNotifications(
            Integer userId, String type,
            Boolean isRead,
            LocalDate from, LocalDate to) {

        String validType =
                resolveNotifType(type);

        AtomicInteger no = new AtomicInteger(1);

        return notifRepo
                .findForReport(
                        userId, validType,
                        isRead, from, to)
                .stream()
                .map(n ->
                        NotificationReportResponse
                                .builder()
                                .no(no.getAndIncrement())
                                .receiverName(
                                        n.getUser() != null
                                                ? n.getUser()
                                                .getUserNameKh()
                                                : "")
                                .receiverEmail(
                                        n.getUser() != null
                                                ? n.getUser()
                                                .getEmail()
                                                : "")
                                .title(nvl(n.getTitle()))
                                .message(
                                        trunc(
                                                n.getMessage(),
                                                80))
                                .type(
                                        n.getType() != null
                                                ? n.getType().name()
                                                : "")
                                .typeLabel(
                                        n.getType() != null
                                                ? n.getType()
                                                .getLabelKh()
                                                : "")
                                .isRead(
                                        Boolean.TRUE.equals(
                                                n.getIsRead()))
                                .readStatus(
                                        Boolean.TRUE.equals(
                                                n.getIsRead())
                                                ? "អានរួច"
                                                : "មិនទាន់អាន")
                                .createdAt(
                                        n.getCreatedAt())
                                .readAt(n.getReadAt())
                                .build())
                .toList();
    }

    @Override
    public byte[] exportUsers(
            Integer roleId, String status) {
        var list = previewUsers(
                roleId, status);
        log.info("Export Users: {}",
                list.size());
        return ExcelUtils.users(list,status);
    }

    @Override
    public List<UserReportResponse>
    previewUsers(
            Integer roleId, String status) {

        AtomicInteger no = new AtomicInteger(1);

        return userRepo
                .findForReport(roleId, status)
                .stream()
                .map(u ->
                        UserReportResponse.builder()
                                .no(no.getAndIncrement())
                                .userNameKh(
                                        u.getUserNameKh())
                                .userNameEn(
                                        u.getUserNameEn())
                                .email(u.getEmail())
                                .phone(u.getPhone())
                                .roleName(
                                        u.getRole() != null
                                                ? u.getRole()
                                                .getRoleName()
                                                : "UNKNOWN")
                                .roleDisplay(
                                        u.getRole() != null
                                                ? u.getRole()
                                                .getDisplayName()
                                                : "មិនស្គាល់")
                                .officerName(
                                        u.getOfficer() != null
                                                ? u.getOfficer()
                                                .getFullNameKh()
                                                : u.getContractOfficer()
                                                != null
                                                  ? u
                                                .getContractOfficer()
                                                .getFullNameKh()
                                                  : "")
                                .departmentName(
                                        resolveUserDeptName(
                                                u))
                                .statusCode(
                                        u.getStatusCode()
                                                != null
                                                ? u.getStatusCode()
                                                .getStatusCode()
                                                : "UNKNOWN")
                                .statusLabel(
                                        u.getStatusCode()
                                                != null
                                                ? u.getStatusCode()
                                                .getLabelKh()
                                                : "មិនស្គាល់")
                                .lastLoginAt(
                                        u.getLastLoginAt())
                                .createdAt(
                                        u.getCreatedAt())
                                .build())
                .toList();
    }

    private String resolveUserDeptName(
            User u) {
        if (u == null) return "";

        if (u.getOfficer() != null
                && u.getOfficer()
                .getDepartment()
                != null) {
            return u.getOfficer()
                    .getDepartment()
                    .getDepartmentName();
        }

        if (u.getContractOfficer()
                != null
                && u.getContractOfficer()
                .getDepartment()
                != null) {
            return u.getContractOfficer()
                    .getDepartment()
                    .getDepartmentName();
        }

        return "";
    }

    private Integer calcAge(LocalDate dob) {
        if (dob == null) return null;
        return Period.between(
                dob, LocalDate.now()).getYears();
    }

    private String expiryLabel(long days) {
        if (days < 0)
            return "ផុតកំណត់រួច "
                    + Math.abs(days) + " ថ្ងៃ";
        if (days == 0)
            return "ផុតកំណត់ថ្ងៃនេះ";
        if (days <= 7)
            return "នៅសល់ " + days
                    + " ថ្ងៃ";
        if (days <= 30)
            return "នៅសល់ " + days
                    + " ថ្ងៃ";
        return "នៅសល់ " + days + " ថ្ងៃ";
    }

    private String genderKh(String g) {
        if (g == null) return "";
        return switch (g) {
            case "MALE"   -> "ប្រុស";
            case "FEMALE" -> "ស្រី";
            case "MONK" -> "បព្វជិត";
            default       -> g;
        };
    }

    private Priority resolvePriority(
            String priority) {
        if (priority == null
                || priority.isBlank())
            return null;
        try {
            return Priority.valueOf(
                    priority.trim()
                            .toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "កម្រិតអាទិភាព «" + priority + "» មិនត្រឹមត្រូវឡើយ។ "
                            + "សូមជ្រើសរើសតម្លៃដែលមានក្នុងប្រព័ន្ធដូចជា៖ ទាប, មធ្យម, ខ្ពស់ ឬ បន្ទាន់។");
        }
    }

    private String priorityKh(String p) {
        if (p == null) return "";
        try {
            return Priority.valueOf(
                            p.toUpperCase())
                    .getLabelKh();
        } catch (Exception e) {
            return p;
        }
    }

    private String resolveNotifType(
            String type) {
        if (type == null
                || type.isBlank())
            return null;
        try {
            NotificationType.valueOf(
                    type.toUpperCase());
            return type.toUpperCase();
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "ប្រភេទនៃការជូនដំណឹង «" + type + "» មិនត្រឹមត្រូវឡើយ។ "
                            + "សូមជ្រើសរើសតម្លៃដែលមានក្នុងប្រព័ន្ធដូចជា៖ ការប្រជុំ, ឯកសារ, សេចក្តីប្រកាស ឬ ប្រព័ន្ធ។");
        }
    }

    private String actionKh(String action) {
        if (action == null) return "";
        return switch (action) {
            case "CREATE"          -> "បង្កើត";
            case "UPDATE"          -> "កែប្រែ";
            case "DELETE"          -> "លុប";
            case "LOGIN"           -> "ចូលប្រព័ន្ធ";
            case "LOGOUT"          -> "ចេញ";
            case "RESET_PASSWORD"  -> "Reset PW";
            case "CHANGE_PASSWORD" -> "ប្ដូរ PW";
            default -> action;
        };
    }

    private String trunc(String t, int max) {
        if (t == null) return "";
        return t.length() > max
                ? t.substring(0, max) + "..."
                : t;
    }

    private String nvl(String v) {
        return v != null ? v : "";
    }
}