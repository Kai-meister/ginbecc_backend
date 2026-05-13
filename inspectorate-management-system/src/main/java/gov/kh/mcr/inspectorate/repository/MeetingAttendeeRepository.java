package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.MeetingAttendee;
import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingAttendeeRepository
        extends JpaRepository<MeetingAttendee, Integer> {

    List<MeetingAttendee> findByMeeting_MeetingId(
            Integer meetingId);

    Optional<MeetingAttendee>
    findByMeeting_MeetingIdAndOfficer_OfficerId(
            Integer meetingId, Integer officerId);

    boolean existsByMeeting_MeetingIdAndOfficer_OfficerId(
            Integer meetingId, Integer officerId);

    @Query("""
        SELECT a.officer.officerId,
               a.officer.fullNameKh,
               COUNT(a)
        FROM MeetingAttendee a
        WHERE a.attendanceStatus = :status
        GROUP BY a.officer.officerId,
                 a.officer.fullNameKh
        HAVING COUNT(a) >= :threshold
        ORDER BY COUNT(a) DESC
        """)
    List<Object[]> findFrequentAbsentees(
            @Param("status") AttendanceStatus status,
            @Param("threshold") long threshold);

    long countByMeeting_MeetingId(Integer meetingId);
}