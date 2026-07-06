package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request
        .MeetingRequest;
import gov.kh.mcr.inspectorate.dto.response
        .MeetingResponse;
import gov.kh.mcr.inspectorate.entity.Meeting;
import org.mapstruct.*;
import java.time.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy =
                ReportingPolicy.IGNORE)
public interface MeetingMapper {

    @Mapping(target = "meetingId",
            ignore = true)
    @Mapping(target = "room",
            ignore = true)
    @Mapping(target = "organizer",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    Meeting toEntity(MeetingRequest request);

    @Mapping(target = "roomId",
            source = "room.roomId")
    @Mapping(target = "roomCode",
            source = "room.roomCode")

    @Mapping(target = "roomLocation",
            source = "room.location")
    @Mapping(target = "roomStatus",
            source = "room.status")
    @Mapping(target = "roomStatusLabel",
            expression =
                    "java(entity.getRoom()"
                            + " != null &&"
                            + " entity.getRoom()"
                            + ".getStatus() != null"
                            + " ? entity.getRoom()"
                            + ".getStatus().getLabelKh()"
                            + " : \"\")")
    @Mapping(target = "organizerName",
            source =
                    "organizer.userNameKh")
    @Mapping(target = "organizerDept",
            expression =
                    "java(deptName("
                            + "entity.getOrganizer()))")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    @Mapping(target = "durationMinutes",
            expression =
                    "java(calcDuration("
                            + "entity))")
    @Mapping(target = "totalAttendees",
            ignore = true)
    @Mapping(target = "attendedCount",
            ignore = true)
    @Mapping(target = "absentCount",
            ignore = true)
    @Mapping(target = "invitedCount",
            ignore = true)
    @Mapping(target = "attendees",
            ignore = true)
    MeetingResponse toResponse(
            Meeting entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy
                            .IGNORE)
    @Mapping(target = "meetingId",
            ignore = true)
    @Mapping(target = "room",
            ignore = true)
    @Mapping(target = "organizer",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    void updateEntity(
            MeetingRequest request,
            @MappingTarget Meeting entity);

    default Integer calcDuration(
            Meeting m) {
        if (m.getStartTime() == null
                || m.getEndTime() == null)
            return null;
        return (int) Duration.between(
                m.getStartTime(),
                m.getEndTime()).toMinutes();
    }

    default String deptName(
            gov.kh.mcr.inspectorate
                    .entity.User u) {
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