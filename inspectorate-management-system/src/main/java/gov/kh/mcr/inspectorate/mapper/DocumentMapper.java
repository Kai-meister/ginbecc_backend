package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request
        .DocumentRequest;
import gov.kh.mcr.inspectorate.dto.response
        .DocumentResponse;
import gov.kh.mcr.inspectorate.entity
        .Document;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy =
                ReportingPolicy.IGNORE)
public interface DocumentMapper {

    @Mapping(target = "documentId",
            ignore = true)
    @Mapping(target = "documentType",
            ignore = true)
    // Fix — user (not officer)
    @Mapping(target = "user",
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
    Document toEntity(
            DocumentRequest request);

    @Mapping(target = "documentTypeId",
            source =
                    "documentType.documentTypeId")
    @Mapping(target = "documentTypeName",
            source =
                    "documentType"
                            + ".documentTypeName")
    // Fix — document.user not document.officer
    @Mapping(target = "userId",
            source = "user.userId")
    @Mapping(target = "userName",
            source = "user.userNameKh")
    // Fix — derive department via expression
    @Mapping(target = "departmentName",
            expression =
                    "java(deptName("
                            + "entity.getUser()))")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    @Mapping(target = "fileUrl",
            source = "attachment.filePath")
    DocumentResponse toResponse(
            Document entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy
                            .IGNORE)
    @Mapping(target = "documentId",
            ignore = true)
    @Mapping(target = "documentType",
            ignore = true)
    @Mapping(target = "user",
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

    default String deptName(
            gov.kh.mcr.inspectorate.entity
                    .User u) {
        if (u == null) return "";

        if (u.getOfficer() != null
                && u.getOfficer()
                .getDepartment()
                != null) {
            return u.getOfficer()
                    .getDepartment()
                    .getDepartmentName();
        }

        if (u.getContractOfficer() != null
                && u.getContractOfficer()
                .getDepartment()
                != null) {
            return u.getContractOfficer()
                    .getDepartment()
                    .getDepartmentName();
        }

        return "";
    }
}