package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.PositionRequest;
import gov.kh.mcr.inspectorate.dto.response.PositionResponse;
import gov.kh.mcr.inspectorate.entity.Position;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:16+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class PositionMapperImpl implements PositionMapper {

    @Override
    public Position toEntity(PositionRequest request) {
        if ( request == null ) {
            return null;
        }

        Position.PositionBuilder position = Position.builder();

        position.positionCode( request.getPositionCode() );
        position.positionName( request.getPositionName() );

        return position.build();
    }

    @Override
    public PositionResponse toResponse(Position entity) {
        if ( entity == null ) {
            return null;
        }

        PositionResponse.PositionResponseBuilder positionResponse = PositionResponse.builder();

        positionResponse.positionId( entity.getPositionId() );
        positionResponse.positionCode( entity.getPositionCode() );
        positionResponse.positionName( entity.getPositionName() );
        positionResponse.createdAt( entity.getCreatedAt() );

        return positionResponse.build();
    }
}
