package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.response
        .report.*;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    byte[] exportOfficers(
            Integer departmentId, String status,
            LocalDate from, LocalDate to);
    List<OfficerReportResponse>
    previewOfficers(
            Integer departmentId,
            String status,
            LocalDate from, LocalDate to);

    byte[] exportContractOfficers(
            Integer expiringWithinDays);
    List<ContractOfficerReportResponse>
    previewContractOfficers(
            Integer expiringWithinDays);

    byte[] exportDocuments(
            Integer userId, String status,
            Integer typeId,
            LocalDate from, LocalDate to);
    List<DocumentReportResponse>
    previewDocuments(
            Integer userId, String status,
            Integer typeId,
            LocalDate from, LocalDate to);

    byte[] exportApprovals(
            String status, Integer userId,
            LocalDate from, LocalDate to);
    List<ApprovalReportResponse>
    previewApprovals(
            String status, Integer userId,
            LocalDate from, LocalDate to);

    byte[] exportMeetings(
            int month, int year, String status);
    List<MeetingReportResponse>
    previewMeetings(
            int month, int year,
            String status);

    List<MeetingMinuteReportResponse> previewMeetingMinutes(int month, int year, Long meetingId);
    byte[] exportMeetingMinutes(int month, int year, Long meetingId);

    byte[] exportAnnouncements(
            String status, String priority,
            LocalDate from, LocalDate to);
    List<AnnouncementReportResponse>
    previewAnnouncements(
            String status, String priority,
            LocalDate from, LocalDate to);

    byte[] exportAnnouncementRecipients(
            Integer announcementId,
            Boolean isRead,
            LocalDate from, LocalDate to);
    List<AnnouncementRecipientReportResponse>
    previewAnnouncementRecipients(
            Integer announcementId,
            Boolean isRead,
            LocalDate from, LocalDate to);

    byte[] exportAuditLogs(
            Integer userId, String action,
            String entityType,
            LocalDate from, LocalDate to);
    List<AuditLogReportResponse>
    previewAuditLogs(
            Integer userId, String action,
            String entityType,
            LocalDate from, LocalDate to);

    byte[] exportNotifications(
            Integer userId, String type,
            Boolean isRead,
            LocalDate from, LocalDate to);
    List<NotificationReportResponse>
    previewNotifications(
            Integer userId, String type,
            Boolean isRead,
            LocalDate from, LocalDate to);

    byte[] exportUsers(
            Integer roleId, String status);
    List<UserReportResponse>
    previewUsers(
            Integer roleId, String status);
}