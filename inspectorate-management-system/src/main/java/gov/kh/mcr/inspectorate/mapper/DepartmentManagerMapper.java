package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response
        .DepartmentManagerResponse;
import gov.kh.mcr.inspectorate.entity
        .DepartmentManager;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy =
                ReportingPolicy.IGNORE)
public interface DepartmentManagerMapper {

    @Mapping(target = "departmentId",
            source =
                    "department.departmentId")
    @Mapping(target = "departmentName",
            source =
                    "department.departmentName")
    @Mapping(target = "userId",
            source = "user.userId")
    @Mapping(target = "userNameKh",
            source = "user.userNameKh")
    @Mapping(target = "userEmail",
            source = "user.email")
    @Mapping(target = "roleName",
            source = "user.role.roleName")
    DepartmentManagerResponse toResponse(
            DepartmentManager entity);
}