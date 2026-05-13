package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.ContractOfficerRequest;
import gov.kh.mcr.inspectorate.dto.response.ContractOfficerResponse;
import gov.kh.mcr.inspectorate.entity.ContractOfficer;
import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.entity.LookupOfficerStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:16+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class ContractOfficerMapperImpl implements ContractOfficerMapper {

    @Override
    public ContractOfficer toEntity(ContractOfficerRequest request) {
        if ( request == null ) {
            return null;
        }

        ContractOfficer.ContractOfficerBuilder contractOfficer = ContractOfficer.builder();

        contractOfficer.contractOfficerCode( request.getContractOfficerCode() );
        contractOfficer.fullNameKh( request.getFullNameKh() );
        contractOfficer.fullNameEn( request.getFullNameEn() );
        contractOfficer.gender( request.getGender() );
        contractOfficer.jobLevel( request.getJobLevel() );
        contractOfficer.jobDescription( request.getJobDescription() );
        contractOfficer.startDate( request.getStartDate() );
        contractOfficer.endDate( request.getEndDate() );

        return contractOfficer.build();
    }

    @Override
    public ContractOfficerResponse toResponse(ContractOfficer entity) {
        if ( entity == null ) {
            return null;
        }

        ContractOfficerResponse.ContractOfficerResponseBuilder contractOfficerResponse = ContractOfficerResponse.builder();

        contractOfficerResponse.departmentName( entityDepartmentDepartmentName( entity ) );
        contractOfficerResponse.statusCode( entityStatusCodeStatusCode( entity ) );
        contractOfficerResponse.statusLabel( entityStatusCodeLabelKh( entity ) );
        contractOfficerResponse.contractOfficerId( entity.getContractOfficerId() );
        contractOfficerResponse.contractOfficerCode( entity.getContractOfficerCode() );
        contractOfficerResponse.fullNameKh( entity.getFullNameKh() );
        contractOfficerResponse.fullNameEn( entity.getFullNameEn() );
        contractOfficerResponse.gender( entity.getGender() );
        contractOfficerResponse.jobLevel( entity.getJobLevel() );
        contractOfficerResponse.jobDescription( entity.getJobDescription() );
        contractOfficerResponse.startDate( entity.getStartDate() );
        contractOfficerResponse.endDate( entity.getEndDate() );
        contractOfficerResponse.createdAt( entity.getCreatedAt() );
        contractOfficerResponse.updatedAt( entity.getUpdatedAt() );

        return contractOfficerResponse.build();
    }

    @Override
    public void updateEntity(ContractOfficerRequest request, ContractOfficer entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getContractOfficerCode() != null ) {
            entity.setContractOfficerCode( request.getContractOfficerCode() );
        }
        if ( request.getFullNameKh() != null ) {
            entity.setFullNameKh( request.getFullNameKh() );
        }
        if ( request.getFullNameEn() != null ) {
            entity.setFullNameEn( request.getFullNameEn() );
        }
        if ( request.getGender() != null ) {
            entity.setGender( request.getGender() );
        }
        if ( request.getJobLevel() != null ) {
            entity.setJobLevel( request.getJobLevel() );
        }
        if ( request.getJobDescription() != null ) {
            entity.setJobDescription( request.getJobDescription() );
        }
        if ( request.getStartDate() != null ) {
            entity.setStartDate( request.getStartDate() );
        }
        if ( request.getEndDate() != null ) {
            entity.setEndDate( request.getEndDate() );
        }
    }

    private String entityDepartmentDepartmentName(ContractOfficer contractOfficer) {
        if ( contractOfficer == null ) {
            return null;
        }
        Department department = contractOfficer.getDepartment();
        if ( department == null ) {
            return null;
        }
        String departmentName = department.getDepartmentName();
        if ( departmentName == null ) {
            return null;
        }
        return departmentName;
    }

    private String entityStatusCodeStatusCode(ContractOfficer contractOfficer) {
        if ( contractOfficer == null ) {
            return null;
        }
        LookupOfficerStatus statusCode = contractOfficer.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String statusCode1 = statusCode.getStatusCode();
        if ( statusCode1 == null ) {
            return null;
        }
        return statusCode1;
    }

    private String entityStatusCodeLabelKh(ContractOfficer contractOfficer) {
        if ( contractOfficer == null ) {
            return null;
        }
        LookupOfficerStatus statusCode = contractOfficer.getStatusCode();
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
