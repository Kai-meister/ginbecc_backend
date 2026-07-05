package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.UserRequest;
import gov.kh.mcr.inspectorate.dto.response.UserResponse;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.UserType;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userId",          ignore = true)
    @Mapping(target = "officer",         ignore = true)
    @Mapping(target = "contractOfficer", ignore = true)
    @Mapping(target = "role",            ignore = true)
    @Mapping(target = "statusCode",      ignore = true)
    @Mapping(target = "passwordHash",    ignore = true)
    @Mapping(target = "failedLoginCount",   ignore = true)
    @Mapping(target = "lockedUntil",     ignore = true)
    @Mapping(target = "lastLoginAt",     ignore = true)
    @Mapping(target = "createdAt",       ignore = true)
    @Mapping(target = "updatedAt",       ignore = true)
    User toEntity(UserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy =
            NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId",          ignore = true)
    @Mapping(target = "officer",         ignore = true)
    @Mapping(target = "contractOfficer", ignore = true)
    @Mapping(target = "role",            ignore = true)
    @Mapping(target = "statusCode",      ignore = true)
    @Mapping(target = "passwordHash",    ignore = true)
    @Mapping(target = "failedLoginCount",   ignore = true)
    @Mapping(target = "lockedUntil",     ignore = true)
    @Mapping(target = "lastLoginAt",     ignore = true)
    @Mapping(target = "createdAt",       ignore = true)
    @Mapping(target = "updatedAt",       ignore = true)

    void updateEntity(UserRequest request, @MappingTarget User entity);
    default UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .userId(user.getUserId())
                .userType(user.getUserType())
                .userNameKh(user.getUserNameKh())
                .userNameEn(user.getUserNameEn())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImageUrl(profileUrl(user))
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roleId(user.getRole() != null
                        ? user.getRole().getRoleId() : null)
                .roleName(user.getRole() != null
                        ? user.getRole().getRoleName() : null)
                .roleDisplay(user.getRole() != null
                        ? user.getRole().getDisplayName() : null)
                .statusCode(user.getStatusCode() != null
                        ? user.getStatusCode().getStatusCode() : null)
                .statusLabel(user.getStatusCode() != null
                        ? user.getStatusCode().getLabelKh() : null)

                .officerInfo(resolveOfficerInfo(user))
                .build();
    }
    default UserResponse.OfficerInfo resolveOfficerInfo(
            User user) {

        if (user.getOfficer() != null) {
            Officer o = user.getOfficer();
            return UserResponse.OfficerInfo.builder()
                    .id(o.getOfficerId())
                    .code(o.getOfficerCode())
                    .fullNameKh(o.getFullNameKh())
                    .fullNameEn(o.getFullNameEn())
                    .departmentName(
                            o.getDepartment() != null
                                    ? o.getDepartment()
                                      .getDepartmentName()
                                    : null)
                    .phone(o.getPhone())
                    .email(o.getEmail())
                    .type(UserType.OFFICER)
                    .build();
        }

        if (user.getContractOfficer() != null) {
            ContractOfficer co =
                    user.getContractOfficer();
            return UserResponse.OfficerInfo.builder()
                    .id(co.getContractOfficerId())
                    .code(co.getContractOfficerCode())
                    .fullNameKh(co.getFullNameKh())
                    .fullNameEn(co.getFullNameEn())
                    .departmentName(
                            co.getDepartment() != null
                                    ? co.getDepartment()
                                      .getDepartmentName()
                                    : null)
                    .phone(null)
                    .email(null)
                    .type(UserType.CONTRACT_OFFICER)
                    .build();
        }

        return null;
    }

    default String profileUrl(User u) {
        if (u == null) return null;

        // Priority 1: Officer profile
        if (u.getOfficer() != null
                && u.getOfficer()
                .getProfileAttachment()
                != null) {
            return u.getOfficer()
                    .getProfileAttachment()
                    .getFilePath();
        }

        // Priority 2: ContractOfficer
        if (u.getContractOfficer() != null
                && u.getContractOfficer()
                .getProfileAttachment()
                != null) {
            return u.getContractOfficer()
                    .getProfileAttachment()
                    .getFilePath();
        }

        // Priority 3: Default by Role
        return null;
    }

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