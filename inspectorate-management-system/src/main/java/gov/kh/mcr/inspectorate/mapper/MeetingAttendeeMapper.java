package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response.AttendeeResponse;
import gov.kh.mcr.inspectorate.entity.MeetingAttendee;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetingAttendeeMapper {

    @Mapping(target = "officerId",
            source = "officer.officerId")
    @Mapping(target = "officerCode",
            source = "officer.officerCode")
    @Mapping(target = "officerName",
            source = "officer.fullNameKh")
    @Mapping(target = "departmentName",
            source = "officer.department.departmentName")
    AttendeeResponse toResponse(MeetingAttendee entity);
}