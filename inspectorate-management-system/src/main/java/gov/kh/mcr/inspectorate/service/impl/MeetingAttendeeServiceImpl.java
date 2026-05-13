package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.AttendeeRequest;
import gov.kh.mcr.inspectorate.dto.response.AttendeeResponse;
import gov.kh.mcr.inspectorate.entity.Meeting;
import gov.kh.mcr.inspectorate.entity.MeetingAttendee;
import gov.kh.mcr.inspectorate.entity.Officer;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.MeetingAttendeeMapper;
import gov.kh.mcr.inspectorate.repository.MeetingAttendeeRepository;
import gov.kh.mcr.inspectorate.repository.MeetingRepository;
import gov.kh.mcr.inspectorate.repository.OfficerRepository;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.MeetingAttendeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingAttendeeServiceImpl
        implements MeetingAttendeeService {

    private final MeetingAttendeeRepository attendeeRepository;
    private final MeetingRepository meetingRepository;
    private final OfficerRepository officerRepository;
    private final MeetingAttendeeMapper attendeeMapper;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional(readOnly = true)
    public List<AttendeeResponse> getByMeetingId(Integer meetingId) {
        return attendeeRepository
                .findByMeeting_MeetingId(meetingId)
                .stream()
                .map(attendeeMapper::toResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public AttendeeResponse getById(
            Integer meetingId,
            Integer attendeeId) {

        MeetingAttendee attendee =
                attendeeRepository.findById(attendeeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "អ្នកចូលរួម", attendeeId));

        if (!attendee.getMeeting()
                .getMeetingId()
                .equals(meetingId)) {
            throw new ResourceNotFoundException(
                    "អ្នកចូលរួម", attendeeId);
        }

        return attendeeMapper.toResponse(attendee);
    }

    @Override
    public AttendeeResponse addAttendee(
            Integer meetingId, AttendeeRequest request) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ការប្រជុំ", meetingId));

        Officer officer = officerRepository
                .findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "មន្ត្រី", request.getOfficerId()));

        attendeeRepository
                .findByMeeting_MeetingIdAndOfficer_OfficerId(
                        meetingId, request.getOfficerId())
                .ifPresent(a -> {
                    throw new DuplicateResourceException(
                            "មន្ត្រីនេះចុះឈ្មោះប្រជុំរួចហើយ");
                });

        MeetingAttendee saved = attendeeRepository.save(
                MeetingAttendee.builder()
                        .meeting(meeting)
                        .officer(officer)
                        .role(request.getRole())
                        .build());

        activityLogService.log("CREATE", "MeetingAttendee",
                saved.getAttendeeId(),
                "បន្ថែម: " + officer.getFullNameKh());

        return attendeeMapper.toResponse(saved);
    }

    @Override
    public void removeAttendee(Integer meetingId, Integer attendeeId) {
        MeetingAttendee attendee = attendeeRepository
                .findById(attendeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "អ្នកចូលរួម", attendeeId));

        if (!attendee.getMeeting().getMeetingId().equals(meetingId)) {
            throw new ResourceNotFoundException(
                    "អ្នកចូលរួម", attendeeId);
        }

        attendeeRepository.deleteById(attendeeId);
        activityLogService.log("DELETE", "MeetingAttendee",
                attendeeId, "លុបអ្នកចូលរួម");
    }
}
