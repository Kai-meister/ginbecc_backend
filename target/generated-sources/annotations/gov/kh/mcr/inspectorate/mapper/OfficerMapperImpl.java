package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.OfficerRequest;
import gov.kh.mcr.inspectorate.dto.response.OfficerResponse;
import gov.kh.mcr.inspectorate.entity.Attachment;
import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.entity.LookupOfficerStatus;
import gov.kh.mcr.inspectorate.entity.Officer;
import gov.kh.mcr.inspectorate.entity.Position;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:16+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class OfficerMapperImpl implements OfficerMapper {

    @Override
    public Officer toEntity(OfficerRequest request) {
        if ( request == null ) {
            return null;
        }

        Officer.OfficerBuilder officer = Officer.builder();

        officer.officerCode( request.getOfficerCode() );
        officer.fullNameKh( request.getFullNameKh() );
        officer.fullNameEn( request.getFullNameEn() );
        officer.gender( request.getGender() );
        officer.dob( request.getDob() );
        officer.joinDate( request.getJoinDate() );
        officer.jobDescription( request.getJobDescription() );
        officer.educationLevel( request.getEducationLevel() );
        officer.specialization( request.getSpecialization() );
        officer.salaryGrade( request.getSalaryGrade() );
        officer.currentAddress( request.getCurrentAddress() );
        officer.birthplace( request.getBirthplace() );
        officer.livingStatus( request.getLivingStatus() );
        officer.phone( request.getPhone() );
        officer.email( request.getEmail() );

        return officer.build();
    }

    @Override
    public OfficerResponse toResponse(Officer entity) {
        if ( entity == null ) {
            return null;
        }

        OfficerResponse.OfficerResponseBuilder officerResponse = OfficerResponse.builder();

        officerResponse.positionName( entityPositionPositionName( entity ) );
        officerResponse.departmentName( entityDepartmentDepartmentName( entity ) );
        officerResponse.statusCode( entityStatusCodeStatusCode( entity ) );
        officerResponse.statusLabel( entityStatusCodeLabelKh( entity ) );
        officerResponse.profileImageUrl( entityProfileAttachmentFilePath( entity ) );
        officerResponse.officerId( entity.getOfficerId() );
        officerResponse.officerCode( entity.getOfficerCode() );
        officerResponse.fullNameKh( entity.getFullNameKh() );
        officerResponse.fullNameEn( entity.getFullNameEn() );
        officerResponse.gender( entity.getGender() );
        officerResponse.dob( entity.getDob() );
        officerResponse.joinDate( entity.getJoinDate() );
        officerResponse.jobDescription( entity.getJobDescription() );
        officerResponse.educationLevel( entity.getEducationLevel() );
        officerResponse.specialization( entity.getSpecialization() );
        officerResponse.salaryGrade( entity.getSalaryGrade() );
        officerResponse.currentAddress( entity.getCurrentAddress() );
        officerResponse.birthplace( entity.getBirthplace() );
        officerResponse.livingStatus( entity.getLivingStatus() );
        officerResponse.phone( entity.getPhone() );
        officerResponse.email( entity.getEmail() );
        officerResponse.createdAt( entity.getCreatedAt() );
        officerResponse.updatedAt( entity.getUpdatedAt() );

        return officerResponse.build();
    }

    @Override
    public void updateEntity(OfficerRequest request, Officer entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getOfficerCode() != null ) {
            entity.setOfficerCode( request.getOfficerCode() );
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
        if ( request.getDob() != null ) {
            entity.setDob( request.getDob() );
        }
        if ( request.getJoinDate() != null ) {
            entity.setJoinDate( request.getJoinDate() );
        }
        if ( request.getJobDescription() != null ) {
            entity.setJobDescription( request.getJobDescription() );
        }
        if ( request.getEducationLevel() != null ) {
            entity.setEducationLevel( request.getEducationLevel() );
        }
        if ( request.getSpecialization() != null ) {
            entity.setSpecialization( request.getSpecialization() );
        }
        if ( request.getSalaryGrade() != null ) {
            entity.setSalaryGrade( request.getSalaryGrade() );
        }
        if ( request.getCurrentAddress() != null ) {
            entity.setCurrentAddress( request.getCurrentAddress() );
        }
        if ( request.getBirthplace() != null ) {
            entity.setBirthplace( request.getBirthplace() );
        }
        if ( request.getLivingStatus() != null ) {
            entity.setLivingStatus( request.getLivingStatus() );
        }
        if ( request.getPhone() != null ) {
            entity.setPhone( request.getPhone() );
        }
        if ( request.getEmail() != null ) {
            entity.setEmail( request.getEmail() );
        }
    }

    private String entityPositionPositionName(Officer officer) {
        if ( officer == null ) {
            return null;
        }
        Position position = officer.getPosition();
        if ( position == null ) {
            return null;
        }
        String positionName = position.getPositionName();
        if ( positionName == null ) {
            return null;
        }
        return positionName;
    }

    private String entityDepartmentDepartmentName(Officer officer) {
        if ( officer == null ) {
            return null;
        }
        Department department = officer.getDepartment();
        if ( department == null ) {
            return null;
        }
        String departmentName = department.getDepartmentName();
        if ( departmentName == null ) {
            return null;
        }
        return departmentName;
    }

    private String entityStatusCodeStatusCode(Officer officer) {
        if ( officer == null ) {
            return null;
        }
        LookupOfficerStatus statusCode = officer.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String statusCode1 = statusCode.getStatusCode();
        if ( statusCode1 == null ) {
            return null;
        }
        return statusCode1;
    }

    private String entityStatusCodeLabelKh(Officer officer) {
        if ( officer == null ) {
            return null;
        }
        LookupOfficerStatus statusCode = officer.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String labelKh = statusCode.getLabelKh();
        if ( labelKh == null ) {
            return null;
        }
        return labelKh;
    }

    private String entityProfileAttachmentFilePath(Officer officer) {
        if ( officer == null ) {
            return null;
        }
        Attachment profileAttachment = officer.getProfileAttachment();
        if ( profileAttachment == null ) {
            return null;
        }
        String filePath = profileAttachment.getFilePath();
        if ( filePath == null ) {
            return null;
        }
        return filePath;
    }
}
