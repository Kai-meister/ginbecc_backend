package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.MeetingRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingResponse;
import gov.kh.mcr.inspectorate.entity.Meeting;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetingMapper {

    @Mapping(target = "meetingId",  ignore = true)
    @Mapping(target = "room",       ignore = true)
    @Mapping(target = "organizer",  ignore = true)
    @Mapping(target = "statusCode", ignore = true)
    @Mapping(target = "attendees",  ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    Meeting toEntity(MeetingRequest request);

    @Mapping(target = "roomId",
            source = "room.roomId")
    @Mapping(target = "roomCode",
            source = "room.roomCode")
    @Mapping(target = "roomLocation",
            source = "room.location")
    @Mapping(target = "organizerId",
            source = "organizer.userId")
    @Mapping(target = "organizerName",
            source = "organizer.userNameKh")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    @Mapping(target = "totalAttendees",
            ignore = true)
    @Mapping(target = "attendedCount",
            ignore = true)
    @Mapping(target = "absentCount",
            ignore = true)
    @Mapping(target = "invitedCount",
            ignore = true)
    @Mapping(target = "attendees",
            ignore = true)

    MeetingResponse toResponse(Meeting entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "meetingId",  ignore = true)
    @Mapping(target = "room",       ignore = true)
    @Mapping(target = "organizer",  ignore = true)
    @Mapping(target = "statusCode", ignore = true)
    @Mapping(target = "attendees",  ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    void updateEntity(
            MeetingRequest request,
            @MappingTarget Meeting entity);
}