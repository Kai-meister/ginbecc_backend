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

    List<MeetingAttendee>
    findByMeeting_MeetingId(
            Integer meetingId);

    // Fix — Officer_OfficerId → User_UserId
    boolean
    existsByMeeting_MeetingIdAndUser_UserId(
            Integer meetingId,
            Integer userId);

    Optional<MeetingAttendee>
    findByMeeting_MeetingIdAndUser_UserId(
            Integer meetingId,
            Integer userId);

    List<MeetingAttendee>
    findByMeeting_MeetingIdAndAttendanceStatus(
            Integer meetingId,
            AttendanceStatus status);

    long countByMeeting_MeetingIdAndAttendanceStatus(
            Integer meetingId,
            AttendanceStatus status);

    long countByMeeting_MeetingId(
            Integer meetingId);

    // Fix — find meetings of specific user
    List<MeetingAttendee>
    findByUser_UserId(
            Integer userId);

    void deleteByMeeting_MeetingId(
            Integer meetingId);
}