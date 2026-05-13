package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.AttendeeRequest;
import gov.kh.mcr.inspectorate.dto.response.AttendeeResponse;

import java.util.List;

public interface MeetingAttendeeService {

    List<AttendeeResponse> getByMeetingId(
            Integer meetingId);

    AttendeeResponse getById(
            Integer meetingId, Integer attendeeId);

    AttendeeResponse addAttendee(
            Integer meetingId,
            AttendeeRequest request);

    void removeAttendee(
            Integer meetingId, Integer attendeeId);
}