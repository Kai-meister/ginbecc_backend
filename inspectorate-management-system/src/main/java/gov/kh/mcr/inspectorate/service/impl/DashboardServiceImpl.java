package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.response.DashboardSummaryResponse;
import gov.kh.mcr.inspectorate.entity.Meeting;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final OfficerRepository officerRepository;
    private final ContractOfficerRepository contractOfficerRepository;
    private final MeetingRepository meetingRepository;
    private final DocumentRepository documentRepository;
    private final NotificationRepository notificationRepository;
    private final SecurityUtils securityUtils;

    @Override
    public DashboardSummaryResponse getSummary(Integer userId) {

        // Officer count by gender
        Map<String, Long> byGender = new LinkedHashMap<>();
        officerRepository.countByGender()
                .forEach(row -> byGender.put(
                        row[0] != null ? row[0].toString() : "UNKNOWN",
                        ((Number) row[1]).longValue()));

        // Officer count by department
        Map<String, Long> byDept = new LinkedHashMap<>();
        officerRepository.countByDepartment()
                .forEach(row -> byDept.put(
                        row[0] != null ? row[0].toString() : "UNKNOWN",
                        ((Number) row[1]).longValue()));

        // Expiring contracts (30 days)
        long expiringContracts = contractOfficerRepository
                .findExpiring(LocalDate.now().plusDays(30))
                .size();

        // Expiring documents (30 days)
        long expiringDocuments = documentRepository
                .findExpiring(LocalDate.now().plusDays(30))
                .size();

        // Today's meetings — "today" must be Cambodia-local: the server
        // runs UTC, so plain LocalDate.now() still points at *yesterday*
        // between 00:00 and 07:00 ICT (QA saw yesterday's count when
        // testing early morning). Cancelled meetings don't count, and the
        // count is department-scoped like the meeting list, so the number
        // matches what the viewer can actually see.
        LocalDate today =
                LocalDate.now(ZoneId.of("Asia/Phnom_Penh"));
        Integer deptScope =
                securityUtils.canBypassDepartmentScope()
                        ? null
                        : securityUtils.getCurrentDepartmentId();
        List<Meeting> todayList = deptScope != null
                ? meetingRepository
                        .findByMeetingDateAndOrganizer_Officer_Department_DepartmentId(
                                today, deptScope)
                : meetingRepository.findByMeetingDate(today);
        long todayMeetings = todayList.stream()
                .filter(m -> m.getStatusCode() == null
                        || !"CANCELLED".equals(
                                m.getStatusCode().getStatusCode()))
                .count();

        // Upcoming meetings — same Cambodia-local "now" and the same department
        // scope as the today count, so the two tiles agree with each other and
        // with the meeting list. Counted in the database because no listing
        // endpoint can answer it: /meetings has no date filter and sorts
        // furthest-future first.
        long upcomingMeetings = meetingRepository.countUpcoming(
                today,
                LocalTime.now(ZoneId.of("Asia/Phnom_Penh")),
                deptScope);

        // Unread notifications
        long unread = userId != null
                ? notificationRepository
                  .countByUser_UserIdAndIsRead(userId, false)
                : 0L;

        return DashboardSummaryResponse.builder()
                .totalOfficers(officerRepository.count())
                .totalContractOfficers(
                        contractOfficerRepository.count())
                .officersByGender(byGender)
                .officersByDepartment(byDept)
                .todayMeetings(todayMeetings)
                .upcomingMeetings(upcomingMeetings)
                .expiringDocuments(expiringDocuments)
                .expiringContracts(expiringContracts)
                .unreadNotifications(unread)
                .build();
    }
}


