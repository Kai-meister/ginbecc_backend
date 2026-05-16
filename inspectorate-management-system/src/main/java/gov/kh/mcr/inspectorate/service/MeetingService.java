package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.AttendanceRequest;
import gov.kh.mcr.inspectorate.dto.request.AttendeeRequest;
import gov.kh.mcr.inspectorate.dto.request.MeetingRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.dto.response.RoomAvailabilityResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MeetingService {

    PageResponse<MeetingResponse> getAll(int page, int size, String status, Integer roomId);

    MeetingResponse getById(Integer id);

    List<MeetingResponse> getCalendar(int month, int year);

    RoomAvailabilityResponse checkAvailability(Integer roomId, LocalDate date, LocalTime startTime, LocalTime endTime);

    List<MeetingResponse> getRoomSchedule(Integer roomId, LocalDate date);

    MeetingResponse create(MeetingRequest request);

    MeetingResponse update(Integer id, MeetingRequest request);

    void addAttendeesBulk(Integer meetingId, List<AttendeeRequest> attendees);

    void updateAttendance(Integer meetingId, Integer attendeeId, AttendanceRequest request);

    void delete(Integer id);
}