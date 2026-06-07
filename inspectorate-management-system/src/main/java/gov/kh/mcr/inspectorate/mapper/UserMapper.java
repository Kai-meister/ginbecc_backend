package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request
        .UserRequest;
import gov.kh.mcr.inspectorate.dto.response
        .UserResponse;
import gov.kh.mcr.inspectorate.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy =
                ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userId",
            ignore = true)
    @Mapping(target = "officer",
            ignore = true)
    @Mapping(target = "role",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "passwordHash",
            ignore = true)
    @Mapping(target = "mustChangePassword",
            ignore = true)
    @Mapping(target = "failedLoginCount",
            ignore = true)
    @Mapping(target = "lockedUntil",
            ignore = true)
    @Mapping(target = "lastLoginAt",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    User toEntity(UserRequest request);

    @Mapping(target = "officerId",
            source = "officer.officerId")
    @Mapping(target = "officerName",
            source = "officer.fullNameKh")
    @Mapping(target = "departmentName",
            source =
                    "officer.department"
                            + ".departmentName")
    @Mapping(target = "roleId",
            source = "role.roleId")
    @Mapping(target = "roleName",
            source = "role.roleName")
    @Mapping(target = "roleDisplay",
            source = "role.displayName")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    UserResponse toResponse(User entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy
                            .IGNORE)
    @Mapping(target = "userId",
            ignore = true)
    // Service handles officer separately
    @Mapping(target = "officer",
            ignore = true)
    @Mapping(target = "role",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "passwordHash",
            ignore = true)
    @Mapping(target = "mustChangePassword",
            ignore = true)
    @Mapping(target = "failedLoginCount",
            ignore = true)
    @Mapping(target = "lockedUntil",
            ignore = true)
    @Mapping(target = "lastLoginAt",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    void updateEntity(
            UserRequest request,
            @MappingTarget User entity);
}