package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .NotificationType;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl
        implements ReportService {

    private final OfficerRepository         officerRepo;
    private final ContractOfficerRepository contractRepo;
    private final DocumentRepository        documentRepo;
    private final ApprovalRepository        approvalRepo;
    private final MeetingRepository         meetingRepo;
    private final MeetingMinuteRepository   minuteRepo;
    private final AnnouncementRepository    announcementRepo;
    private final ActivityLogRepository     logRepo;
    private final NotificationRepository    notifRepo;
    private final UserRepository            userRepo;

    @Override
    public byte[] exportOfficers(
            Integer deptId, String status,
            LocalDate from, LocalDate to) {

        List<Officer> list =
                officerRepo.findForReport(
                        deptId, status, from, to);
        log.info("Officers: {}", list.size());
        return ExcelUtils.officers(list);
    }

    @Override
    public byte[] exportContractOfficers(
            Integer days) {

        LocalDate expiry =
                LocalDate.now().plusDays(days);
        List<ContractOfficer> list =
                contractRepo.findExpiring(expiry);
        log.info("ContractOfficers: {}",
                list.size());
        return ExcelUtils.contractOfficers(
                list, days);
    }

    @Override
    public byte[] exportDocuments(
            Integer officerId, String status,
            Integer typeId,
            LocalDate from, LocalDate to) {

        List<Document> list =
                documentRepo.findForReport(
                        officerId, status,
                        typeId, from, to);
        log.info("Documents: {}", list.size());
        return ExcelUtils.documents(list);
    }

    @Override
    public byte[] exportApprovals(
            String status,
            LocalDate from, LocalDate to) {

        LocalDateTime fromDt = from != null
                ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null
                ? to.atTime(23, 59, 59) : null;

        List<Approval> list =
                approvalRepo.findForReport(
                        status, fromDt, toDt);
        log.info("Approvals: {}", list.size());
        return ExcelUtils.approvals(list);
    }

    @Override
    public byte[] exportMeetings(
            int month, int year,
            String status) {

        List<Meeting> list =
                meetingRepo.findForReport(
                        month, year, status);
        log.info("Meetings: {}", list.size());
        return ExcelUtils.meetings(
                list, month, year);
    }

    @Override
    public byte[] exportMeetingMinutes(
            int month, int year) {

        List<MeetingMinute> list =
                minuteRepo.findForReport(
                        month, year);
        log.info("Minutes: {}", list.size());
        return ExcelUtils.meetingMinutes(list);
    }

    @Override
    public byte[] exportAnnouncements(
            String status, String priority,
            LocalDate from, LocalDate to) {

        List<Announcement> list =
                announcementRepo.findForReport(
                        status, priority, from, to);
        log.info("Announcements: {}",
                list.size());
        return ExcelUtils.announcements(list);
    }

    @Override
    public byte[] exportAuditLogs(
            Integer userId, String action,
            String entityType,
            LocalDate from, LocalDate to) {

        LocalDateTime fromDt = from != null
                ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null
                ? to.atTime(23, 59, 59) : null;

        List<ActivityLog> list =
                logRepo.findWithFilters(
                                userId, action, entityType,
                                fromDt, toDt,
                                Pageable.unpaged())
                        .getContent();
        log.info("AuditLogs: {}", list.size());
        return ExcelUtils.auditLogs(list);
    }

    @Override
    public byte[] exportNotifications(
            Integer userId, String type,
            Boolean isRead,
            LocalDate from, LocalDate to) {

//        NotificationType notifType =
//                type != null
//                        ? NotificationType.valueOf(type)
//                        : null;

        List<Notification> list =
                notifRepo.findForReport(userId,type,isRead, from, to);
        log.info("Notifications: {}",
                list.size());
        return ExcelUtils.notifications(list);
    }

    @Override
    public byte[] exportUsers(
            Integer roleId, String status) {

        List<User> list =
                userRepo.findForReport(
                        roleId, status);
        log.info("Users: {}", list.size());
        return ExcelUtils.users(list);
    }
}