package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.DepartmentRequest;
import gov.kh.mcr.inspectorate.dto.response.DepartmentResponse;
import gov.kh.mcr.inspectorate.entity.Department;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:16+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class DepartmentMapperImpl implements DepartmentMapper {

    @Override
    public Department toEntity(DepartmentRequest request) {
        if ( request == null ) {
            return null;
        }

        Department.DepartmentBuilder department = Department.builder();

        department.departmentCode( request.getDepartmentCode() );
        department.departmentName( request.getDepartmentName() );
        department.description( request.getDescription() );
        department.status( request.getStatus() );

        return department.build();
    }

    @Override
    public DepartmentResponse toResponse(Department entity) {
        if ( entity == null ) {
            return null;
        }

        DepartmentResponse.DepartmentResponseBuilder departmentResponse = DepartmentResponse.builder();

        departmentResponse.departmentId( entity.getDepartmentId() );
        departmentResponse.departmentCode( entity.getDepartmentCode() );
        departmentResponse.departmentName( entity.getDepartmentName() );
        departmentResponse.description( entity.getDescription() );
        departmentResponse.status( entity.getStatus() );
        departmentResponse.createdAt( entity.getCreatedAt() );

        return departmentResponse.build();
    }

    @Override
    public void updateEntity(DepartmentRequest request, Department entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getDepartmentCode() != null ) {
            entity.setDepartmentCode( request.getDepartmentCode() );
        }
        if ( request.getDepartmentName() != null ) {
            entity.setDepartmentName( request.getDepartmentName() );
        }
        if ( request.getDescription() != null ) {
            entity.setDescription( request.getDescription() );
        }
        if ( request.getStatus() != null ) {
            entity.setStatus( request.getStatus() );
        }
    }
}
