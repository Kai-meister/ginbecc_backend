package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.DocumentRequest;
import gov.kh.mcr.inspectorate.dto.response.DocumentResponse;
import gov.kh.mcr.inspectorate.entity.Document;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper {

    @Mapping(target = "documentId",
            ignore = true)
    @Mapping(target = "documentType",
            ignore = true)
    @Mapping(target = "officer",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "attachment",
            ignore = true)
    @Mapping(target = "uploadedBy",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    Document toEntity(DocumentRequest request);

    @Mapping(target = "documentTypeId",
            source = "documentType.documentTypeId")
    @Mapping(target = "documentTypeName",
            source = "documentType.documentTypeName")
    @Mapping(target = "officerId",
            source = "officer.officerId")
    @Mapping(target = "officerName",
            source = "officer.fullNameKh")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    @Mapping(target = "fileUrl",
            source = "attachment.filePath")
    DocumentResponse toResponse(Document entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "documentId",
            ignore = true)
    @Mapping(target = "documentType",
            ignore = true)
    @Mapping(target = "officer",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "attachment",
            ignore = true)
    @Mapping(target = "uploadedBy",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    void updateEntity(
            DocumentRequest request,
            @MappingTarget Document entity);
}