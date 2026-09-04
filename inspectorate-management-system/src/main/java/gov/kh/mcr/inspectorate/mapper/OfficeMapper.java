package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.OfficeRequest;

import gov.kh.mcr.inspectorate.dto.response.OfficeResponse;
import gov.kh.mcr.inspectorate.entity.Office;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OfficeMapper {

    @Mapping(target = "officeId", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "createdAt",    ignore = true)
    @Mapping(target = "updatedAt",    ignore = true)
    Office toEntity(OfficeRequest request);

    @Mapping(target = "departmentId",
            source = "department.departmentId")
    @Mapping(target = "departmentName",
            source = "department.departmentName")
    OfficeResponse toResponse(Office entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "officeId", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "createdAt",    ignore = true)
    @Mapping(target = "updatedAt",    ignore = true)
    void updateEntity(OfficeRequest request, @MappingTarget Office entity);
}
