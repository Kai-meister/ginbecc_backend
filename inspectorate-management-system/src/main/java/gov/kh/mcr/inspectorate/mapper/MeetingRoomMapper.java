package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.MeetingRoomRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingRoomResponse;
import gov.kh.mcr.inspectorate.entity.MeetingRoom;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetingRoomMapper {

    @Mapping(target = "roomId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentMeeting", ignore = true)
    @Mapping(target = "attachment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MeetingRoom toEntity(MeetingRoomRequest request);

    @Mapping(target = "statusLabel",
            expression = "java(entity.getStatus() != null ? entity.getStatus().getLabelKh() : \"\")")
    @Mapping(target = "currentMeetingId", source = "currentMeeting.meetingId")
    @Mapping(target = "currentMeetingTitle", source = "currentMeeting.title")
    @Mapping(target = "currentMeetingOrganizer", source = "currentMeeting.organizer.userNameKh")
    @Mapping(target = "imageUrl", ignore = true)
//    @Mapping(target = "imageUrl",
//            expression =
//                    "java(entity"
//                            + ".getAttachment() != null"
//                            + " ? entity.getAttachment()"
//                            + ".getFilePath() : null)")
  MeetingRoomResponse toResponse(MeetingRoom entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roomId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentMeeting", ignore = true)
    @Mapping(target = "attachment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(MeetingRoomRequest request, @MappingTarget MeetingRoom entity);
}