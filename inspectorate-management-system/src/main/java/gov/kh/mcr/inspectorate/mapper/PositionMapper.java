package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.PositionRequest;
import gov.kh.mcr.inspectorate.dto.response.PositionResponse;
import gov.kh.mcr.inspectorate.entity.Position;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PositionMapper {

    @Mapping(target = "positionId", ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    Position toEntity(PositionRequest request);

    PositionResponse toResponse(Position entity);
}