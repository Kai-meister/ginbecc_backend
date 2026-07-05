package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response
        .ApprovalResponse;
import gov.kh.mcr.inspectorate.entity
        .Approval;
import gov.kh.mcr.inspectorate.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy =
                ReportingPolicy.IGNORE)
public interface ApprovalMapper {

    @Mapping(target = "documentId",
            source = "document.documentId")
    @Mapping(target = "documentName",
            source = "document.documentName")
    @Mapping(target = "requestedByName",
            source =
                    "document.user.userNameKh")
    @Mapping(target = "requestedByDept",
            expression =
                    "java(deptName("
                            + "entity.getDocument()"
                            + ".getUser()))")
    // Fix — department instead of
    // assignedManager
    @Mapping(target = "departmentId",
            source =
                    "department.departmentId")
    @Mapping(target = "departmentName",
            source =
                    "department.departmentName")
    @Mapping(target = "approvedByName",
            source = "approvedBy.userNameKh")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    ApprovalResponse toResponse(
            Approval entity);

    default String deptName(User u) {
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