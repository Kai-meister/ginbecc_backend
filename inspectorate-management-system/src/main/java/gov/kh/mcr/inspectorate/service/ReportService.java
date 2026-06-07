package gov.kh.mcr.inspectorate.service;

import java.time.LocalDate;

public interface ReportService {

    byte[] exportOfficers(
            Integer departmentId,
            String status,
            LocalDate from,
            LocalDate to);

    byte[] exportContractOfficers(
            Integer expiringWithinDays);

    byte[] exportDocuments(
            Integer officerId,
            String status,
            Integer typeId,
            LocalDate from,
            LocalDate to);

    byte[] exportApprovals(
            String status,
            LocalDate from,
            LocalDate to);

    byte[] exportMeetings(
            int month, int year,
            String status);

    byte[] exportMeetingMinutes(
            int month, int year);

    byte[] exportAnnouncements(
            String status,
            String priority,
            LocalDate from,
            LocalDate to);

    byte[] exportAuditLogs(
            Integer userId,
            String action,
            String entityType,
            LocalDate from,
            LocalDate to);

    byte[] exportNotifications(
            Integer userId,
            String type,
            Boolean isRead,
            LocalDate from,
            LocalDate to);

    byte[] exportUsers(
            Integer roleId,
            String status);
}