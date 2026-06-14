package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.AttendeeResponse;
import java.util.List;

public interface MeetingAttendeeService {

    // GET all attendees of meeting
    List<AttendeeResponse> getByMeetingId(
            Integer meetingId);

    // GET one attendee
    AttendeeResponse getById(
            Integer meetingId, Integer attendeeId);

    // Add one attendee
    AttendeeResponse addAttendee(
            Integer meetingId,
            AttendeeRequest request);

    // Add bulk attendees
    List<AttendeeResponse> addBulk(
            Integer meetingId,
            BulkAttendeeRequest request);

    // Update attendance status
    AttendeeResponse updateAttendance(
            Integer meetingId,
            Integer attendeeId,
            AttendanceUpdateRequest request);

    // Remove attendee
    void removeAttendee(
            Integer meetingId,
            Integer attendeeId);
}