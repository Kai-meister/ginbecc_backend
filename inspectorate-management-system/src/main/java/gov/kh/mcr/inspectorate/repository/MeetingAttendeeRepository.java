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


    boolean
    existsByMeeting_MeetingIdAndOfficer_OfficerId(
            Integer meetingId,
            Integer officerId);

    long countByMeeting_MeetingId(
            Integer meetingId);

    Optional<MeetingAttendee>
    findByMeeting_MeetingIdAndOfficer_OfficerId(
            Integer meetingId,
            Integer officerId);

    List<MeetingAttendee>
    findByMeeting_MeetingIdAndAttendanceStatus(
            Integer meetingId,
            AttendanceStatus status);

    long countByMeeting_MeetingIdAndAttendanceStatus(
            Integer meetingId,
            AttendanceStatus status);

    List<MeetingAttendee>
    findByOfficer_OfficerId(
            Integer officerId);
    void deleteByMeeting_MeetingId(
            Integer meetingId);
}