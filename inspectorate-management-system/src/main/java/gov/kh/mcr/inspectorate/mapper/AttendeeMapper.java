package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response.AttendeeResponse;
import gov.kh.mcr.inspectorate.entity.MeetingAttendee;
import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
import gov.kh.mcr.inspectorate.enums.AttendeeRole;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttendeeMapper {

    @Mapping(target = "meetingId",
            source = "meeting.meetingId")
    @Mapping(target = "meetingTitle",
            source = "meeting.title")
    @Mapping(target = "officerId",
            source = "officer.officerId")
    @Mapping(target = "officerCode",
            source = "officer.officerCode")
    @Mapping(target = "officerName",
            source = "officer.fullNameKh")
    @Mapping(target = "departmentName",
            source = "officer.department.departmentName")
    @Mapping(target = "roleLabel",
            expression = "java(roleLabel(" + "entity.getRole()))")
    @Mapping(target = "attendanceLabel",
            expression = "java(statusLabel(" + "entity.getAttendanceStatus()))")
    AttendeeResponse toResponse(
            MeetingAttendee entity);

    default String roleLabel(AttendeeRole r) {
        return r != null ? r.getLabelKh() : "";
    }

    default String statusLabel(
            AttendanceStatus s) {
        return s != null ? s.getLabelKh() : "";
    }
}