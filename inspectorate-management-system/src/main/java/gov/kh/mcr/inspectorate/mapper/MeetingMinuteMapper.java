package gov.kh.mcr.inspectorate.mapper;
import gov.kh.mcr.inspectorate.dto.request.MeetingMinuteRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingMinuteResponse;
import gov.kh.mcr.inspectorate.entity.MeetingMinute;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetingMinuteMapper {

    @Mapping(target = "minuteId",       ignore = true)
    @Mapping(target = "meeting",        ignore = true)
    @Mapping(target = "attachmentPath", ignore = true)
    @Mapping(target = "recordedBy",     ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    MeetingMinute toEntity(MeetingMinuteRequest request);

    @Mapping(target = "meetingId",
            source = "meeting.meetingId")
    @Mapping(target = "meetingTitle",
            source = "meeting.title")
    @Mapping(target = "recordedByName",
            source = "recordedBy.userNameKh")
    @Mapping(target = "attachmentUrl",
            source = "attachmentPath.filePath")
    MeetingMinuteResponse toResponse(MeetingMinute entity);

    @BeanMapping(nullValuePropertyMappingStrategy =
            NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "minuteId",       ignore = true)
    @Mapping(target = "meeting",        ignore = true)
    @Mapping(target = "attachmentPath", ignore = true)
    @Mapping(target = "recordedBy",     ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    void updateEntity(MeetingMinuteRequest request,
                      @MappingTarget MeetingMinute entity);
}