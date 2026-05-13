package gov.kh.mcr.inspectorate.mapper;


import gov.kh.mcr.inspectorate.dto.request.MeetingRoomRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingRoomResponse;
import gov.kh.mcr.inspectorate.entity.MeetingRoom;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetingRoomMapper {

    @Mapping(target = "roomId",    ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MeetingRoom toEntity(MeetingRoomRequest request);

    @Mapping(target = "imageUrl",
            source = "imagePath.filePath")
    MeetingRoomResponse toResponse(MeetingRoom entity);

    @BeanMapping(nullValuePropertyMappingStrategy =
            NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roomId",    ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(MeetingRoomRequest request,
                      @MappingTarget MeetingRoom entity);
}
