package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.UserRequest;
import gov.kh.mcr.inspectorate.dto.response.UserResponse;
import gov.kh.mcr.inspectorate.entity.LookupUserStatus;
import gov.kh.mcr.inspectorate.entity.Officer;
import gov.kh.mcr.inspectorate.entity.Role;
import gov.kh.mcr.inspectorate.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:15+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.userNameKh( request.getUserNameKh() );
        user.userNameEn( request.getUserNameEn() );
        user.email( request.getEmail() );
        user.phone( request.getPhone() );

        return user.build();
    }

    @Override
    public UserResponse toResponse(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.roleId( entityRoleRoleId( entity ) );
        userResponse.roleName( entityRoleRoleName( entity ) );
        userResponse.roleDisplayName( entityRoleDisplayName( entity ) );
        userResponse.officerId( entityOfficerOfficerId( entity ) );
        userResponse.officerName( entityOfficerFullNameKh( entity ) );
        userResponse.officerCode( entityOfficerOfficerCode( entity ) );
        userResponse.statusCode( entityStatusCodeStatusCode( entity ) );
        userResponse.statusLabel( entityStatusCodeLabelKh( entity ) );
        userResponse.userId( entity.getUserId() );
        userResponse.uuid( entity.getUuid() );
        userResponse.userNameKh( entity.getUserNameKh() );
        userResponse.userNameEn( entity.getUserNameEn() );
        userResponse.email( entity.getEmail() );
        userResponse.phone( entity.getPhone() );
        userResponse.lastLoginAt( entity.getLastLoginAt() );
        userResponse.createdAt( entity.getCreatedAt() );
        userResponse.updatedAt( entity.getUpdatedAt() );

        return userResponse.build();
    }

    @Override
    public void updateEntity(UserRequest request, User entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getUserNameKh() != null ) {
            entity.setUserNameKh( request.getUserNameKh() );
        }
        if ( request.getUserNameEn() != null ) {
            entity.setUserNameEn( request.getUserNameEn() );
        }
        if ( request.getEmail() != null ) {
            entity.setEmail( request.getEmail() );
        }
        if ( request.getPhone() != null ) {
            entity.setPhone( request.getPhone() );
        }
    }

    private Integer entityRoleRoleId(User user) {
        if ( user == null ) {
            return null;
        }
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        Integer roleId = role.getRoleId();
        if ( roleId == null ) {
            return null;
        }
        return roleId;
    }

    private String entityRoleRoleName(User user) {
        if ( user == null ) {
            return null;
        }
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        String roleName = role.getRoleName();
        if ( roleName == null ) {
            return null;
        }
        return roleName;
    }

    private String entityRoleDisplayName(User user) {
        if ( user == null ) {
            return null;
        }
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        String displayName = role.getDisplayName();
        if ( displayName == null ) {
            return null;
        }
        return displayName;
    }

    private Integer entityOfficerOfficerId(User user) {
        if ( user == null ) {
            return null;
        }
        Officer officer = user.getOfficer();
        if ( officer == null ) {
            return null;
        }
        Integer officerId = officer.getOfficerId();
        if ( officerId == null ) {
            return null;
        }
        return officerId;
    }

    private String entityOfficerFullNameKh(User user) {
        if ( user == null ) {
            return null;
        }
        Officer officer = user.getOfficer();
        if ( officer == null ) {
            return null;
        }
        String fullNameKh = officer.getFullNameKh();
        if ( fullNameKh == null ) {
            return null;
        }
        return fullNameKh;
    }

    private String entityOfficerOfficerCode(User user) {
        if ( user == null ) {
            return null;
        }
        Officer officer = user.getOfficer();
        if ( officer == null ) {
            return null;
        }
        String officerCode = officer.getOfficerCode();
        if ( officerCode == null ) {
            return null;
        }
        return officerCode;
    }

    private String entityStatusCodeStatusCode(User user) {
        if ( user == null ) {
            return null;
        }
        LookupUserStatus statusCode = user.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String statusCode1 = statusCode.getStatusCode();
        if ( statusCode1 == null ) {
            return null;
        }
        return statusCode1;
    }

    private String entityStatusCodeLabelKh(User user) {
        if ( user == null ) {
            return null;
        }
        LookupUserStatus statusCode = user.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String labelKh = statusCode.getLabelKh();
        if ( labelKh == null ) {
            return null;
        }
        return labelKh;
    }
}
