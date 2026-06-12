package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.response
        .report.*;
import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.service
        .ReportService;
import gov.kh.mcr.inspectorate.util.ExcelUtils;
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

    private final OfficerRepository              officerRepo;
    private final ContractOfficerRepository      contractRepo;
    private final DocumentRepository             documentRepo;
    private final ApprovalRepository             approvalRepo;
    private final MeetingRepository              meetingRepo;
    private final MeetingAttendeeRepository      attendeeRepo;
    private final MeetingMinuteRepository        minuteRepo;
    private final AnnouncementRepository         announcementRepo;
    private final AnnouncementRecipientRepository recipientRepo;
    private final ActivityLogRepository          logRepo;
    private final NotificationRepository         notifRepo;
    private final UserRepository                 userRepo;


    @Override
    public byte[] exportOfficers(
            Integer deptId, String status,
            LocalDate from, LocalDate to) {
        var list = previewOfficers(
                deptId, status, from, to);
        log.info("Export Officers: {}",
                list.size());
        return ExcelUtils.officers(list,deptId,status,from,to);
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
    public byte[] exportDocuments(
            Integer officerId, String status,
            Integer typeId,
            LocalDate from, LocalDate to) {
        var list = previewDocuments(
                officerId, status, typeId,
                from, to);
        log.info("Export Documents: {}",
                list.size());
        return ExcelUtils.documents(list,officerId,status,from,to);
    }

    @Override
    public byte[] exportApprovals(
            String status,
            LocalDate from, LocalDate to) {
        var list = previewApprovals(
                status, from, to);
        log.info("Export Approvals: {}",
                list.size());
        return ExcelUtils.approvals(list,status,from,to);
    }

    @Override
    public byte[] exportMeetings(
            int month, int year,
            String status) {
        var list = previewMeetings(
                month, year, status);
        log.info("Export Meetings: {}",
                list.size());
        return ExcelUtils.meetings(list, month, year,status);
    }

    @Override
    public byte[] exportMeetingMinutes(
            int month, int year) {
        var list = previewMeetingMinutes(
                month, year);
        log.info("Export Minutes: {}",
                list.size());
        return ExcelUtils.meetingMinutes(list);
    }

    @Override
    public byte[] exportAnnouncements(
            String status, String priority,
            LocalDate from, LocalDate to) {
        var list = previewAnnouncements(
                status, priority, from, to);
        log.info("Export Announcements: {}",
                list.size());
        return ExcelUtils.announcements(list,status,priority,from,to);
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
        return ExcelUtils.auditLogs(list,action,entityType,from,to);
    }

    @Override
    public byte[] exportNotifications(
            Integer userId, String type,
            Boolean isRead,
            LocalDate from, LocalDate to) {
        var list = previewNotifications(
                userId, type, isRead, from, to);
        log.info("Export Notifications: {}",
                list.size());
        return ExcelUtils.notifications(list,type,from,to);
    }

    @Override
    public byte[] exportUsers(
            Integer roleId, String status) {
        var list = previewUsers(roleId, status);
        log.info("Export Users: {}",
                list.size());
        return ExcelUtils.users(list,status);
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
                                        o.getOfficerCode())
                                .fullNameKh(
                                        o.getFullNameKh())
                                .fullNameEn(
                                        o.getFullNameEn())
                                .gender(o.getGender())
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
                                                : "")
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
                                                  .getStatusCode() : "")
                                .statusLabel(
                                        o.getStatusCode() != null
                                                ? o.getStatusCode()
                                                  .getLabelKh() : "")
                                .departmentName(
                                        o.getDepartment() != null
                                                ? o.getDepartment().getDepartmentName()
                                                : "គ្មាន")                              // Fix
                                .positionName(
                                        o.getPosition() != null
                                                ? o.getPosition().getPositionName()
                                                : "")
                                .statusCode(
                                        o.getStatusCode() != null
                                                ? o.getStatusCode().getStatusCode()
                                                : "UNKNOWN")                            // Fix
                                .statusLabel(
                                        o.getStatusCode() != null
                                                ? o.getStatusCode().getLabelKh()
                                                : "មិនស្គាល់")
                                .build())
                .toList();
    }

    @Override
    public List<ContractOfficerReportResponse>
    previewContractOfficers(
            Integer days) {

        LocalDate expiry = LocalDate.now()
                .plusDays(days != null ? days : 30);

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
                                    c.getContractOfficerCode())
                            .fullNameKh(c.getFullNameKh())
                            .fullNameEn(c.getFullNameEn())
                            .gender(c.getGender())
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
                                            : "")
                            .jobLevel(c.getJobLevel())
                            .accountingCode(
                                    c.getAccountingCode())
                            .startDate(c.getStartDate())
                            .endDate(c.getEndDate())
                            .daysUntilExpiry(
                                    daysLeft < 0
                                            ? 0L : daysLeft)
                            .expiryLabel(
                                    expiryLabel(daysLeft))
                            .note(c.getNote())
                            .statusCode(
                                    c.getStatusCode() != null
                                            ? c.getStatusCode()
                                              .getStatusCode() : "")
                            .statusLabel(
                                    c.getStatusCode() != null
                                            ? c.getStatusCode()
                                              .getLabelKh() : "")
                            .departmentName(
                                    c.getDepartment() != null
                                            ? c.getDepartment().getDepartmentName()
                                            : "គ្មាន")                              // Fix
                            .daysUntilExpiry(
                                    c.getEndDate() != null
                                            ? Math.max(0L, ChronoUnit.DAYS.between(
                                            LocalDate.now(), c.getEndDate()))
                                            : 0L)                                   // Fix — 0 not null
                            .expiryLabel(
                                    c.getEndDate() != null
                                            ? expiryLabel(ChronoUnit.DAYS.between(
                                            LocalDate.now(), c.getEndDate()))
                                            : "គ្មានកំណត់")
                            .build();
                })
                .toList();
    }

    @Override
    public List<DocumentReportResponse>
    previewDocuments(
            Integer officerId,
            String status,
            Integer typeId,
            LocalDate from, LocalDate to) {

        AtomicInteger no = new AtomicInteger(1);

        return documentRepo
                .findForReport(
                        officerId, status,
                        typeId, from, to)
                .stream()
                .map(d ->
                        DocumentReportResponse.builder()
                                .no(no.getAndIncrement())
                                .documentNumber(
                                        d.getDocumentNumber())
                                .documentName(
                                        d.getDocumentName())
                                .documentTypeName(
                                        d.getDocumentType() != null
                                                ? d.getDocumentType()
                                                  .getDocumentTypeName()
                                                : "")
                                .officerName(
                                        d.getOfficer() != null
                                                ? d.getOfficer()
                                                  .getFullNameKh()
                                                : "")
                                .departmentName(
                                        d.getOfficer() != null
                                                && d.getOfficer()
                                                .getDepartment()
                                                != null
                                                ? d.getOfficer()
                                                  .getDepartment()
                                                  .getDepartmentName()
                                                : "")
                                .expiryDate(d.getExpiryDate())
                                .isExpired(
                                        d.getExpiryDate() != null
                                                && d.getExpiryDate()
                                                .isBefore(
                                                        LocalDate.now()))
                                .statusCode(
                                        d.getStatusCode() != null
                                                ? d.getStatusCode()
                                                  .getStatusCode() : "")
                                .statusLabel(
                                        d.getStatusCode() != null
                                                ? d.getStatusCode()
                                                  .getLabelKh() : "")
                                .uploadedBy(
                                        d.getUploadedBy() != null
                                                ? d.getUploadedBy()
                                                  .getUserNameKh()
                                                : "")
                                .createdAt(d.getCreatedAt())
                                .documentTypeName(
                                        d.getDocumentType() != null
                                                ? d.getDocumentType()
                                                .getDocumentTypeName()
                                                : "គ្មានប្រភេទ")                        // Fix
                                .officerName(
                                        d.getOfficer() != null
                                                ? d.getOfficer().getFullNameKh()
                                                : "គ្មានមន្ត្រី")                       // Fix
                                .isExpired(
                                        d.getExpiryDate() != null
                                                && d.getExpiryDate()
                                                .isBefore(LocalDate.now()))
                                .build())
                .toList();
    }

    @Override
    public List<ApprovalReportResponse>
    previewApprovals(
            String status,
            LocalDate from, LocalDate to) {

        LocalDateTime fromDt = from != null
                ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null
                ? to.atTime(23, 59, 59) : null;

        AtomicInteger no = new AtomicInteger(1);

        return approvalRepo
                .findForReport(status, fromDt, toDt)
                .stream()
                .map(a ->
                        ApprovalReportResponse.builder()
                                .no(no.getAndIncrement())
                                .documentName(
                                        a.getDocument() != null
                                                ? a.getDocument()
                                                  .getDocumentName()
                                                : "")
                                .documentNumber(
                                        a.getDocument() != null
                                                ? a.getDocument()
                                                  .getDocumentNumber()
                                                : "")
                                .requestedBy(
                                        a.getRequestedBy() != null
                                                ? a.getRequestedBy()
                                                  .getFullNameKh()
                                                : "")
                                .requestedByDept(
                                        a.getRequestedBy() != null
                                                && a.getRequestedBy()
                                                .getDepartment()
                                                != null
                                                ? a.getRequestedBy()
                                                  .getDepartment()
                                                  .getDepartmentName()
                                                : "")
                                .approvedBy(
                                        a.getApprovedBy() != null
                                                ? a.getApprovedBy()
                                                  .getUserNameKh()
                                                : "")
                                .statusCode(
                                        a.getStatusCode() != null
                                                ? a.getStatusCode()
                                                  .getStatusCode() : "")
                                .statusLabel(
                                        a.getStatusCode() != null
                                                ? a.getStatusCode()
                                                  .getLabelKh() : "")
                                .comment(a.getComment())
                                .requestedAt(
                                        a.getRequestedAt())
                                .decidedAt(a.getDecidedAt())
                                .documentName(
                                        a.getDocument() != null
                                                ? a.getDocument().getDocumentName()
                                                : "គ្មានឯកសារ")                        // Fix
                                .requestedBy(
                                        a.getRequestedBy() != null
                                                ? a.getRequestedBy().getFullNameKh()
                                                : "គ្មាន")
                                .build())
                .toList();
    }

    @Override
    public List<MeetingReportResponse>
    previewMeetings(
            int month, int year,
            String status) {

        AtomicInteger no = new AtomicInteger(1);

        return meetingRepo
                .findForReport(month, year, status)
                .stream()
                .map(m -> {
                    Integer meetingId =
                            m.getMeetingId();
                    int total = (int) attendeeRepo
                            .countByMeeting_MeetingId(
                                    meetingId);
                    int attended = (int) attendeeRepo
                            .countByMeeting_MeetingIdAndAttendanceStatus(
                                    meetingId,
                                    gov.kh.mcr.inspectorate
                                            .enums.AttendanceStatus
                                            .ATTENDED);
                    int absent = (int) attendeeRepo
                            .countByMeeting_MeetingIdAndAttendanceStatus(
                                    meetingId,
                                    gov.kh.mcr.inspectorate
                                            .enums.AttendanceStatus
                                            .ABSENT);

                    return MeetingReportResponse
                            .builder()
                            .no(no.getAndIncrement())
                            .title(m.getTitle())
                            .meetingType(
                                    m.getMeetingType() != null
                                            ? m.getMeetingType()
                                              .name() : "")
                            .meetingDate(
                                    m.getMeetingDate())
                            .startTime(m.getStartTime())
                            .endTime(m.getEndTime())
                            .roomCode(
                                    m.getRoom() != null
                                            ? m.getRoom().getRoomCode()
                                            : "Online")
                            .organizerName(
                                    m.getOrganizer() != null
                                            ? m.getOrganizer()
                                              .getUserNameKh()
                                            : "")
                            .totalAttendees(total)
                            .attendedCount(attended)
                            .absentCount(absent)
                            .statusCode(
                                    m.getStatusCode() != null
                                            ? m.getStatusCode()
                                              .getStatusCode() : "")
                            .statusLabel(
                                    m.getStatusCode() != null
                                            ? m.getStatusCode()
                                              .getLabelKh() : "")
                            .meetingType(
                                    m.getMeetingType() != null
                                            ? m.getMeetingType().name()
                                            : "UNKNOWN")                            // Fix
                            .organizerName(
                                    m.getOrganizer() != null
                                            ? m.getOrganizer().getUserNameKh()
                                            : "គ្មាន")                              // Fix
                            .totalAttendees(total)                      // already 0 if none
                            .attendedCount(attended)                    // already 0
                            .absentCount(absent)
                            .build();
                })
                .toList();
    }

    @Override
    public List<MeetingMinuteReportResponse>
    previewMeetingMinutes(
            int month, int year) {

        AtomicInteger no = new AtomicInteger(1);

        return minuteRepo
                .findForReport(month, year)
                .stream()
                .map(m ->
                        MeetingMinuteReportResponse
                                .builder()
                                .no(no.getAndIncrement())
                                .meetingTitle(
                                        m.getMeeting() != null
                                                ? m.getMeeting()
                                                  .getTitle() : "")
                                .meetingDate(
                                        m.getMeeting() != null
                                                ? m.getMeeting()
                                                  .getMeetingDate()
                                                : null)
                                .recordedBy(
                                        m.getRecordedBy() != null
                                                ? m.getRecordedBy()
                                                  .getUserNameKh()
                                                : "")
                                .summary(m.getSummary())
                                .decisions(m.getDecisions())
                                .actionItems(
                                        m.getActionItems())
                                .hasAttachment(
                                        m.getAttachment() != null)
                                .createdAt(m.getCreatedAt())
                                .meetingTitle(
                                        m.getMeeting() != null
                                                ? m.getMeeting().getTitle()
                                                : "គ្មានប្រជុំ")                        // Fix
                                .meetingDate(
                                        m.getMeeting() != null
                                                ? m.getMeeting().getMeetingDate()
                                                : null)
                                .hasAttachment(
                                        m.getAttachment() != null)
                                .build())
                .toList();
    }

    @Override
    public List<AnnouncementReportResponse>
    previewAnnouncements(
            String status, String priority,
            LocalDate from, LocalDate to) {

        AtomicInteger no = new AtomicInteger(1);

        return announcementRepo
                .findForReport(
                        status, priority, from, to)
                .stream()
                .map(a -> {
                    long total = recipientRepo
                            .countByAnnouncement_AnnouncementId(
                                    a.getAnnouncementId());
                    long read = recipientRepo
                            .countRead(
                                    a.getAnnouncementId());

                    return AnnouncementReportResponse
                            .builder()
                            .no(no.getAndIncrement())
                            .title(a.getTitle())
                            .createdBy(
                                    a.getCreatedBy() != null
                                            ? a.getCreatedBy()
                                              .getUserNameKh()
                                            : "")
                            .priority(
                                    a.getPriority() != null
                                            ? a.getPriority().name()
                                            : "")
                            .priorityLabel(
                                    a.getPriority() != null
                                            ? priorityKh(
                                            a.getPriority().name())
                                            : "")
                            .statusCode(
                                    a.getStatusCode() != null
                                            ? a.getStatusCode()
                                              .getStatusCode() : "")
                            .statusLabel(
                                    a.getStatusCode() != null
                                            ? a.getStatusCode()
                                              .getLabelKh() : "")
                            .totalRecipients(total)
                            .readCount(read)
                            .unreadCount(total - read)
                            .publishAt(a.getPublishAt())
                            .createdAt(a.getCreatedAt())
                            .createdBy(
                                    a.getCreatedBy() != null
                                            ? a.getCreatedBy().getUserNameKh()
                                            : "គ្មាន")                              // Fix
                            .priority(
                                    a.getPriority() != null
                                            ? a.getPriority().name()
                                            : "MEDIUM")                             // Fix
                            .priorityLabel(
                                    a.getPriority() != null
                                            ? priorityKh(a.getPriority().name())
                                            : "មធ្យម")                              // Fix
                            .totalRecipients(total)                     // already 0
                            .readCount(read)                            // already 0
                            .unreadCount(total - read)
                            .build();
                })
                .toList();
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
                                .userEmail(l.getUserEmail())
                                .action(l.getAction())
                                .actionLabel(
                                        actionKh(l.getAction()))
                                .entityType(l.getEntityType())
                                .entityId(l.getEntityId())
                                .details(l.getDetails())
                                .ipAddress(l.getIpAddress())
                                .createdAt(l.getCreatedAt())
                                .userNameKh(
                                        l.getUser() != null
                                                ? l.getUser().getUserNameKh()
                                                : "SYSTEM")
                                .userEmail(
                                        l.getUserEmail() != null
                                                ? l.getUserEmail()
                                                : "system")                             // Fix
                                .actionLabel(
                                        actionKh(l.getAction()))
                                .build())
                .toList();
    }

    @Override
    public List<NotificationReportResponse>
    previewNotifications(
            Integer userId, String type,
            Boolean isRead,
            LocalDate from, LocalDate to) {

        AtomicInteger no = new AtomicInteger(1);


        String validType = null;
        if (type != null && !type.isBlank()) {
            try {
                // Validate enum value exists
                NotificationType.valueOf(
                        type.toUpperCase());
                validType = type.toUpperCase();
            } catch (IllegalArgumentException e) {
                throw new BusinessException(
                        "type មិនត្រឹមត្រូវ: "
                                + type
                                + " — ប្រើ: MEETING, DOCUMENT,"
                                + " ANNOUNCEMENT, SYSTEM");
            }
        }

        return notifRepo
                .findForReport(
                        userId,
                        validType, // ← String
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
                                                ? n.getUser().getEmail()
                                                : "")
                                .title(n.getTitle())
                                .type(n.getType() != null
                                        ? n.getType().name()
                                        : "")
                                .typeLabel(
                                        n.getType() != null
                                                ? n.getType().getLabelKh()
                                                : "")
                                .isRead(n.getIsRead())
                                .readStatus(
                                        Boolean.TRUE.equals(
                                                n.getIsRead())
                                                ? "អានរួច"
                                                : "មិនទាន់អាន")
                                .createdAt(n.getCreatedAt())
                                .readAt(n.getReadAt())
                                .receiverName(
                                        n.getUser() != null
                                                ? n.getUser().getUserNameKh()
                                                : "គ្មាន")                              // Fix
                                .receiverEmail(
                                        n.getUser() != null
                                                ? n.getUser().getEmail()
                                                : "")                                   // Fix
                                .type(
                                        n.getType() != null
                                                ? n.getType().name()
                                                : "SYSTEM")                             // Fix
                                .typeLabel(
                                        n.getType() != null
                                                ? n.getType().getLabelKh()
                                                : "ប្រព័ន្ធ")                           // Fix
                                .readStatus(
                                        Boolean.TRUE.equals(n.getIsRead())
                                                ? "អានរួច"
                                                : "មិនទាន់អាន")
                                .build())
                .toList();
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
                                .userNameKh(u.getUserNameKh())
                                .userNameEn(u.getUserNameEn())
                                .email(u.getEmail())
                                .phone(u.getPhone())
                                .roleName(
                                        u.getRole() != null
                                                ? u.getRole().getRoleName()
                                                : "")
                                .roleDisplay(
                                        u.getRole() != null
                                                ? u.getRole()
                                                  .getDisplayName()
                                                : "")
                                .officerName(
                                        u.getOfficer() != null
                                                ? u.getOfficer()
                                                  .getFullNameKh()
                                                : u.getContractOfficer()
                                                != null
                                                  ? u.getContractOfficer()
                                                    .getFullNameKh()
                                                  : "")
                                .departmentName(
                                        u.getOfficer() != null
                                                && u.getOfficer()
                                                .getDepartment()
                                                != null
                                                ? u.getOfficer()
                                                  .getDepartment()
                                                  .getDepartmentName()
                                                : "")
                                .statusCode(
                                        u.getStatusCode() != null
                                                ? u.getStatusCode()
                                                  .getStatusCode() : "")
                                .statusLabel(
                                        u.getStatusCode() != null
                                                ? u.getStatusCode()
                                                  .getLabelKh() : "")
                                .lastLoginAt(
                                        u.getLastLoginAt())
                                .createdAt(u.getCreatedAt())
                                .roleName(
                                        u.getRole() != null
                                                ? u.getRole().getRoleName()
                                                : "UNKNOWN")                            // Fix
                                .roleDisplay(
                                        u.getRole() != null
                                                ? u.getRole().getDisplayName()
                                                : "មិនស្គាល់")                          // Fix
                                .statusCode(
                                        u.getStatusCode() != null
                                                ? u.getStatusCode().getStatusCode()
                                                : "UNKNOWN")                            // Fix
                                .statusLabel(
                                        u.getStatusCode() != null
                                                ? u.getStatusCode().getLabelKh()
                                                : "មិនស្គាល់")
                                .build())
                .toList();
    }

    // ── Private Helpers ───────────────────────────

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
            return "ផុតកំណត់ថ្ងៃនេះ ⚠️";
        if (days <= 7)
            return "នៅសល់ " + days + " ថ្ងៃ 🔴";
        if (days <= 30)
            return "នៅសល់ " + days + " ថ្ងៃ 🟡";
        return "នៅសល់ " + days + " ថ្ងៃ";
    }

    private String genderKh(String gender) {
        if (gender == null) return "";
        return switch (gender) {
            case "MALE"   -> "ប្រុស";
            case "FEMALE" -> "ស្រី";
            default       -> gender;
        };
    }

    private String priorityKh(String p) {
        if (p == null) return "";
        return switch (p) {
            case "LOW"    -> "ទាប";
            case "MEDIUM" -> "មធ្យម";
            case "HIGH"   -> "ខ្ពស់";
            case "URGENT" -> "បន្ទាន់";
            default       -> p;
        };
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


}