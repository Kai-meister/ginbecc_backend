package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.DocumentTypeRequest;
import gov.kh.mcr.inspectorate.dto.response.DocumentTypeResponse;
import gov.kh.mcr.inspectorate.entity.DocumentType;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentTypeMapper {

    @Mapping(target = "documentTypeId", ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    DocumentType toEntity(DocumentTypeRequest request);

    DocumentTypeResponse toResponse(DocumentType entity);

    @BeanMapping(nullValuePropertyMappingStrategy =
            NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "documentTypeId", ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    void updateEntity(DocumentTypeRequest request,
                      @MappingTarget DocumentType entity);
}