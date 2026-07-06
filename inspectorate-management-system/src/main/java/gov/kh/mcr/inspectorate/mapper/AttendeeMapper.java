package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response
        .AttendeeResponse;
import gov.kh.mcr.inspectorate.entity
        .MeetingAttendee;
import gov.kh.mcr.inspectorate.entity.User;
import gov.kh.mcr.inspectorate.enums
        .AttendanceStatus;
import gov.kh.mcr.inspectorate.enums
        .AttendeeRole;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy =
                ReportingPolicy.IGNORE)
public interface AttendeeMapper {

    @Mapping(target = "meetingId",
            source = "meeting.meetingId")
    @Mapping(target = "meetingTitle",
            source = "meeting.title")
    @Mapping(target = "userId",
            source = "user.userId")
    @Mapping(target = "userName",
            source = "user.userNameKh")
    @Mapping(target = "departmentName",
            expression =
                    "java(deptName("
                            + "entity.getUser()))")
    @Mapping(target = "roleLabel",
            expression =
                    "java(roleLabel("
                            + "entity.getRole()))")
    @Mapping(target = "attendanceLabel",
            expression =
                    "java(statusLabel("
                            + "entity"
                            + ".getAttendanceStatus()))")
    AttendeeResponse toResponse(
            MeetingAttendee entity);

    default String roleLabel(
            AttendeeRole r) {
        return r != null
                ? r.getLabelKh() : "";
    }

    default String statusLabel(
            AttendanceStatus s) {
        return s != null
                ? s.getLabelKh() : "";
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