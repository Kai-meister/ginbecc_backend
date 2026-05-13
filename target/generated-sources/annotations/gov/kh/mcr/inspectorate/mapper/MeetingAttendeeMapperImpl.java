package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response.AttendeeResponse;
import gov.kh.mcr.inspectorate.entity.Department;
import gov.kh.mcr.inspectorate.entity.MeetingAttendee;
import gov.kh.mcr.inspectorate.entity.Officer;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:16+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class MeetingAttendeeMapperImpl implements MeetingAttendeeMapper {

    @Override
    public AttendeeResponse toResponse(MeetingAttendee entity) {
        if ( entity == null ) {
            return null;
        }

        AttendeeResponse.AttendeeResponseBuilder attendeeResponse = AttendeeResponse.builder();

        attendeeResponse.officerId( entityOfficerOfficerId( entity ) );
        attendeeResponse.officerCode( entityOfficerOfficerCode( entity ) );
        attendeeResponse.officerName( entityOfficerFullNameKh( entity ) );
        attendeeResponse.departmentName( entityOfficerDepartmentDepartmentName( entity ) );
        attendeeResponse.attendeeId( entity.getAttendeeId() );
        attendeeResponse.role( entity.getRole() );
        attendeeResponse.attendanceStatus( entity.getAttendanceStatus() );
        attendeeResponse.checkInTime( entity.getCheckInTime() );
        attendeeResponse.note( entity.getNote() );

        return attendeeResponse.build();
    }

    private Integer entityOfficerOfficerId(MeetingAttendee meetingAttendee) {
        if ( meetingAttendee == null ) {
            return null;
        }
        Officer officer = meetingAttendee.getOfficer();
        if ( officer == null ) {
            return null;
        }
        Integer officerId = officer.getOfficerId();
        if ( officerId == null ) {
            return null;
        }
        return officerId;
    }

    private String entityOfficerOfficerCode(MeetingAttendee meetingAttendee) {
        if ( meetingAttendee == null ) {
            return null;
        }
        Officer officer = meetingAttendee.getOfficer();
        if ( officer == null ) {
            return null;
        }
        String officerCode = officer.getOfficerCode();
        if ( officerCode == null ) {
            return null;
        }
        return officerCode;
    }

    private String entityOfficerFullNameKh(MeetingAttendee meetingAttendee) {
        if ( meetingAttendee == null ) {
            return null;
        }
        Officer officer = meetingAttendee.getOfficer();
        if ( officer == null ) {
            return null;
        }
        String fullNameKh = officer.getFullNameKh();
        if ( fullNameKh == null ) {
            return null;
        }
        return fullNameKh;
    }

    private String entityOfficerDepartmentDepartmentName(MeetingAttendee meetingAttendee) {
        if ( meetingAttendee == null ) {
            return null;
        }
        Officer officer = meetingAttendee.getOfficer();
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
}
