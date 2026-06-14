package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response
        .NotificationResponse;
import gov.kh.mcr.inspectorate.entity.Notification;
import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy =
                ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(target = "typeLabel",
            expression =
                    "java(label("
                            + "entity.getType()))")
    @Mapping(target = "navigatePath",
            expression =
                    "java(path("
                            + "entity.getType(),"
                            + "entity.getReferenceId()))")
    NotificationResponse toResponse(
            Notification entity);

    default String label(NotificationType t) {
        return t != null
                ? t.getLabelKh() : "";
    }

    default String path(
            NotificationType t,
            Integer refId) {
        return t != null
                ? t.buildPath(refId) : "/";
    }
}