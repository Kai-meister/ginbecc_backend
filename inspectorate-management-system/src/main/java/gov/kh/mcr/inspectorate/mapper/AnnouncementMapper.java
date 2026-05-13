package gov.kh.mcr.inspectorate.mapper;
import gov.kh.mcr.inspectorate.dto.request.AnnouncementRequest;
import gov.kh.mcr.inspectorate.dto.response.AnnouncementResponse;
import gov.kh.mcr.inspectorate.entity.Announcement;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnnouncementMapper {

    @Mapping(target = "announcementId",  ignore = true)
    @Mapping(target = "attachmentPath",  ignore = true)
    @Mapping(target = "createdBy",       ignore = true)
    @Mapping(target = "meeting",         ignore = true)
    @Mapping(target = "statusCode",      ignore = true)
    @Mapping(target = "createdAt",       ignore = true)
    @Mapping(target = "updatedAt",       ignore = true)
    Announcement toEntity(AnnouncementRequest request);

    @Mapping(target = "createdByName",
            source = "createdBy.userNameKh")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    @Mapping(target = "attachmentUrl",
            source = "attachmentPath.filePath")
    @Mapping(target = "totalRecipients", ignore = true)
    @Mapping(target = "readCount",       ignore = true)
    AnnouncementResponse toResponse(Announcement entity);

    @BeanMapping(nullValuePropertyMappingStrategy =
            NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "announcementId",  ignore = true)
    @Mapping(target = "attachmentPath",  ignore = true)
    @Mapping(target = "createdBy",       ignore = true)
    @Mapping(target = "meeting",         ignore = true)
    @Mapping(target = "statusCode",      ignore = true)
    @Mapping(target = "createdAt",       ignore = true)
    @Mapping(target = "updatedAt",       ignore = true)
    void updateEntity(AnnouncementRequest request,
                      @MappingTarget Announcement entity);
}