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
    @Mapping(target = "documentNumber",
            source = "document.documentNumber")
    // requestedBy = Officer
    @Mapping(target = "requestedByOfficerId",
            source = "requestedBy.officerId")
    @Mapping(target = "requestedByName",
            source = "requestedBy.fullNameKh")
    // approvedBy = User
    @Mapping(target = "approvedByUserId",
            source = "approvedBy.userId")
    @Mapping(target = "approvedByName",
            source = "approvedBy.userNameKh")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    ApprovalResponse toResponse(Approval entity);
}