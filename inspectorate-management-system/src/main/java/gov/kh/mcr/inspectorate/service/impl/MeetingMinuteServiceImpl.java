package gov.kh.mcr.inspectorate.service.impl;
import gov.kh.mcr.inspectorate.dto.request.MeetingMinuteRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingMinuteResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.Meeting;
import gov.kh.mcr.inspectorate.entity.MeetingMinute;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.MeetingMinuteMapper;
import gov.kh.mcr.inspectorate.repository.AttachmentRepository;
import gov.kh.mcr.inspectorate.repository.MeetingMinuteRepository;
import gov.kh.mcr.inspectorate.repository.MeetingRepository;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.MeetingMinuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingMinuteServiceImpl
        implements MeetingMinuteService {

    private final MeetingMinuteRepository meetingMinuteRepository;
    private final MeetingRepository meetingRepository;
    private final AttachmentRepository attachmentRepository;
    private final MeetingMinuteMapper meetingMinuteMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MeetingMinuteResponse> getAll(
            int page, int size, Integer meetingId) {

        if (meetingId != null) {
            List<MeetingMinuteResponse> list =
                    meetingMinuteRepository
                            .findByMeeting_MeetingId(meetingId)
                            .map(meetingMinuteMapper::toResponse)
                            .map(List::of)
                            .orElse(List.of());
            return PageResponse.<MeetingMinuteResponse>builder()
                    .content(list)
                    .pageNumber(0)
                    .pageSize(list.size())
                    .totalElements(list.size())
                    .totalPages(list.isEmpty() ? 0 : 1)
                    .last(true).first(true)
                    .build();
        }

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        return PageResponse.of(
                meetingMinuteRepository.findAll(pageable)
                        .map(meetingMinuteMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public MeetingMinuteResponse getById(Integer id) {
        return meetingMinuteMapper.toResponse(findById(id));
    }

    @Override
    public MeetingMinuteResponse create(
            MeetingMinuteRequest request) {

        if (meetingMinuteRepository
                .existsByMeeting_MeetingId(
                        request.getMeetingId())) {
            throw new DuplicateResourceException(
                    "ប្រជុំនេះមានកំណត់ហេតុរួចហើយ");
        }

        Meeting meeting = meetingRepository
                .findById(request.getMeetingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ការប្រជុំ", request.getMeetingId()));

        MeetingMinute minute =
                meetingMinuteMapper.toEntity(request);
        minute.setMeeting(meeting);

        securityUtils.getCurrentUser()
                .ifPresent(minute::setRecordedBy);

        if (request.getAttachmentPathId() != null) {
            attachmentRepository
                    .findById(request.getAttachmentPathId())
                    .ifPresent(minute::setAttachmentPath);
        }

        MeetingMinute saved =
                meetingMinuteRepository.save(minute);

        activityLogService.log("CREATE", "MeetingMinute",
                saved.getMinuteId(),
                "បង្កើត: " + meeting.getTitle());

        return meetingMinuteMapper.toResponse(saved);
    }

    @Override
    public MeetingMinuteResponse update(
            Integer id, MeetingMinuteRequest request) {

        MeetingMinute minute = findById(id);
        meetingMinuteMapper.updateEntity(request, minute);

        if (request.getAttachmentPathId() != null) {
            attachmentRepository
                    .findById(request.getAttachmentPathId())
                    .ifPresent(minute::setAttachmentPath);
        }

        activityLogService.log("UPDATE", "MeetingMinute",
                id, "កែប្រែ");

        return meetingMinuteMapper.toResponse(
                meetingMinuteRepository.save(minute));
    }

    private MeetingMinute findById(Integer id) {
        return meetingMinuteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "កំណត់ហេតុ", id));
    }
}