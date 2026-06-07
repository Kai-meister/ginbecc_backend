package gov.kh.mcr.inspectorate.service.impl;
import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request.MeetingMinuteRequest;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.dto.response.MeetingMinuteResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.Meeting;
import gov.kh.mcr.inspectorate.entity.MeetingMinute;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.MeetingMinuteMapper;
import gov.kh.mcr.inspectorate.repository.AttachmentRepository;
import gov.kh.mcr.inspectorate.repository.MeetingMinuteRepository;
import gov.kh.mcr.inspectorate.repository.MeetingRepository;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import gov.kh.mcr.inspectorate.service.MeetingMinuteService;
import gov.kh.mcr.inspectorate.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingMinuteServiceImpl
        implements MeetingMinuteService {

    private final MeetingMinuteRepository minuteRepo;
    private final MeetingRepository meetingRepo;
    private final AttachmentRepository attachRepo;
    private final MeetingMinuteMapper minuteMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;
    private final AttachmentService attachmentService;
    private final MinioService minioService;

    // ─────────────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponse<MeetingMinuteResponse> getAll(
            int page, int size,
            Integer meetingId) {

        if (meetingId != null) {
            List<MeetingMinuteResponse> list =
                    minuteRepo
                            .findByMeeting_MeetingId(meetingId)
                            .map(minuteMapper::toResponse)
                            .map(List::of)
                            .orElse(List.of());

            return PageResponse
                    .<MeetingMinuteResponse>builder()
                    .content(list)
                    .pageNumber(0)
                    .pageSize(list.size())
                    .totalElements(list.size())
                    .totalPages(list.isEmpty() ? 0 : 1)
                    .first(true).last(true)
                    .build();
        }

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        return PageResponse.of(
                minuteRepo.findAll(pageable)
                        .map(minuteMapper::toResponse));
    }
    
    @Override
    @Transactional(readOnly = true)
    public MeetingMinuteResponse getById(Integer id) {
        return minuteMapper.toResponse(findById(id));
    }

    @Override
    public MeetingMinuteResponse create(
            MeetingMinuteRequest request) {

        if (minuteRepo.existsByMeeting_MeetingId(
                request.getMeetingId())) {
            throw new DuplicateResourceException(
                    "ប្រជុំនេះ មានកំណត់ហេតុរួចហើយ");
        }

        Meeting meeting = meetingRepo
                .findById(request.getMeetingId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ការប្រជុំ",
                                request.getMeetingId()));

        MeetingMinute minute =
                minuteMapper.toEntity(request);
        minute.setMeeting(meeting);
        minute.setAttachment(null);

        securityUtils.getCurrentUser()
                .ifPresent(minute::setRecordedBy);

        MeetingMinute saved =
                minuteRepo.save(minute);

        activityLogService.log(
                "CREATE", "MeetingMinute",
                saved.getMinuteId(),
                "បង្កើត: " + meeting.getTitle(),
                buildContext());

        return minuteMapper.toResponse(saved);
    }

    @Override
    public MeetingMinuteResponse update(
            Integer id,
            MeetingMinuteRequest request) {

        MeetingMinute minute = findById(id);
        minuteMapper.updateEntity(request, minute);

        // Fix — attachment unchanged on update
        // Upload ដោយ POST /{id}/attachment ដាច់ដោយឡែក

        activityLogService.log(
                "UPDATE", "MeetingMinute",
                id, "កែប្រែ",
                buildContext());

        return minuteMapper.toResponse(
                minuteRepo.save(minute));
    }

    @Override
    public MeetingMinuteResponse uploadAttachment(
            Integer minuteId,
            MultipartFile file) {

        MeetingMinute minute = findById(minuteId);

        // Upload to MinIO
        AttachmentResponse resp =
                attachmentService.upload(
                        file,
                        AttachmentRefType.MEETING_MINUTE,
                        minuteId);
        attachRepo.findById(
                        resp.getAttachmentId())
                .ifPresent(att -> {
                    minute.setAttachment(att);
                    minuteRepo.save(minute);
                });

        activityLogService.log(
                "UPDATE", "MeetingMinute",
                minuteId,
                "Upload PDF: "
                        + resp.getOriginalName(),
                buildContext());

        return minuteMapper.toResponse(
                minuteRepo.save(minute));
    }

    // ─────────────────────────────────────────────
    // Fix — Get download URL
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public String getDownloadUrl(Integer minuteId) {
        MeetingMinute minute = findById(minuteId);

        if (minute.getAttachment() == null) {
            throw new ResourceNotFoundException(
                    "កំណត់ហេតុ មិនមាន File",
                    minuteId);
        }

        return minioService.getPresignedUrl(
                minute.getAttachment().getFilePath());
    }

    // ── Private helpers ───────────────────────────
    private MeetingMinute findById(Integer id) {
        return minuteRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "កំណត់ហេតុ", id));
    }

    private ActivityLogContext buildContext() {
        try {
            var req = ((ServletRequestAttributes)
                    RequestContextHolder
                            .currentRequestAttributes())
                    .getRequest();
            return securityUtils.buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext.builder()
                    .build();
        }
    }
}