package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.RoleRequest;
import gov.kh.mcr.inspectorate.dto.response.RoleResponse;
import gov.kh.mcr.inspectorate.entity.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:16+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public Role toEntity(RoleRequest request) {
        if ( request == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.roleName( request.getRoleName() );
        role.displayName( request.getDisplayName() );
        role.description( request.getDescription() );

        return role.build();
    }

    @Override
    public RoleResponse toResponse(Role entity) {
        if ( entity == null ) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();

        roleResponse.roleId( entity.getRoleId() );
        roleResponse.roleName( entity.getRoleName() );
        roleResponse.displayName( entity.getDisplayName() );
        roleResponse.description( entity.getDescription() );

        return roleResponse.build();
    }

    @Override
    public void updateEntity(RoleRequest request, Role entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getRoleName() != null ) {
            entity.setRoleName( request.getRoleName() );
        }
        if ( request.getDisplayName() != null ) {
            entity.setDisplayName( request.getDisplayName() );
        }
        if ( request.getDescription() != null ) {
            entity.setDescription( request.getDescription() );
        }
    }
}
