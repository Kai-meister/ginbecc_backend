package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response.ApprovalResponse;
import gov.kh.mcr.inspectorate.entity.Approval;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApprovalMapper {

    @Mapping(target = "documentId",
            source = "document.documentId")
    @Mapping(target = "documentName",
            source = "document.documentName")
    @Mapping(target = "requestedByName",
            source = "requestedBy.userNameKh")
    @Mapping(target = "approvedByName",
            source = "approvedBy.userNameKh")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    ApprovalResponse toResponse(Approval entity);
}