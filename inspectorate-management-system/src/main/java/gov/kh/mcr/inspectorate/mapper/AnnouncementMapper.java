package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request
        .AnnouncementRequest;
import gov.kh.mcr.inspectorate.dto.response
        .AnnouncementResponse;
import gov.kh.mcr.inspectorate.entity
        .Announcement;
import org.mapstruct.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy =
                ReportingPolicy.IGNORE)
public interface AnnouncementMapper {

    @Mapping(target = "announcementId",
            ignore = true)
    @Mapping(target = "createdBy",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "attachment",
            ignore = true)
    @Mapping(target = "meeting",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    Announcement toEntity(
            AnnouncementRequest request);

    @Mapping(target = "priorityLabel",
            expression =
                    "java(priorityKh("
                            + "entity.getPriority()))")
    @Mapping(target = "statusCode",
            source =
                    "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    @Mapping(target = "createdByName",
            source = "createdBy.userNameKh")
    @Mapping(target = "createdById",
            source = "createdBy.userId")
    @Mapping(target = "isExpired",
            expression =
                    "java(isExpired("
                            + "entity.getExpireAt()))")
    @Mapping(target = "daysUntilExpire",
            expression =
                    "java(daysUntilExpire("
                            + "entity.getExpireAt()))")
    @Mapping(target = "totalRecipients",
            ignore = true)
    @Mapping(target = "readCount",
            ignore = true)
    @Mapping(target = "unreadCount",
            ignore = true)
    @Mapping(target = "currentUserRead",
            ignore = true)
    @Mapping(target = "currentUserReadAt",
            ignore = true)
    AnnouncementResponse toResponse(
            Announcement entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy
                            .IGNORE)
    @Mapping(target = "announcementId",
            ignore = true)
    @Mapping(target = "createdBy",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "attachment",
            ignore = true)
    @Mapping(target = "meeting",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    void updateEntity(
            AnnouncementRequest request,
            @MappingTarget Announcement entity);

    default Boolean isExpired(
            LocalDate expireAt) {
        if (expireAt == null) return false;
        return LocalDate.now()
                .isAfter(expireAt);
    }

    default Long daysUntilExpire(
            LocalDate expireAt) {
        if (expireAt == null) return null;
        long days = ChronoUnit.DAYS.between(
                LocalDate.now(), expireAt);
        return days < 0 ? 0L : days;
    }

    default String priorityKh(
            gov.kh.mcr.inspectorate.enums
                    .Priority p) {
        if (p == null) return "";
        return switch (p) {
            case LOW    -> "ទាប";
            case MEDIUM -> "មធ្យម";
            case HIGH   -> "ខ្ពស់";
            case URGENT -> "បន្ទាន់";
        };
    }
}