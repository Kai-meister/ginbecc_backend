package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request
        .OfficerRequest;
import gov.kh.mcr.inspectorate.dto.response
        .OfficerResponse;
import gov.kh.mcr.inspectorate.entity
        .Officer;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy =
                ReportingPolicy.IGNORE)
public interface OfficerMapper {

    @Mapping(target = "officerId",
            ignore = true)
    @Mapping(target = "department",
            ignore = true)
    @Mapping(target = "position",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "profileAttachment",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    Officer toEntity(
            OfficerRequest request);

    @Mapping(target = "departmentName", source = "department" + ".departmentName")
    @Mapping(target = "departmentId", source = "department" + ".departmentId")
    @Mapping(target = "positionName", source = "position" + ".positionName")
    @Mapping(target = "positionId", source = "position" + ".positionId")
    @Mapping(target = "statusCode", source = "statusCode.statusCode")
    @Mapping(target = "statusLabel", source = "statusCode.labelKh")
    @Mapping(target = "profileImageUrl", ignore = true)
    OfficerResponse toResponse(
            Officer entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy
                            .IGNORE)
    @Mapping(target = "officerId",
            ignore = true)
    @Mapping(target = "department",
            ignore = true)
    @Mapping(target = "position",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "profileAttachment",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    void updateEntity(
            OfficerRequest request,
            @MappingTarget Officer entity);
}
