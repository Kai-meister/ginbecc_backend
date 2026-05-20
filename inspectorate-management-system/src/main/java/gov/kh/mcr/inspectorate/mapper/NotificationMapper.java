package gov.kh.mcr.inspectorate.mapper;
import gov.kh.mcr.inspectorate.dto.response.NotificationResponse;
import gov.kh.mcr.inspectorate.entity.Notification;
import gov.kh.mcr.inspectorate.enums.NotificationType;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(target = "typeLabel",
            expression = "java(getTypeLabel(entity.getType()))")
    @Mapping(target = "referenceType",
            source = "referenceType")
    NotificationResponse toResponse(
            Notification entity);

    default String getTypeLabel(
            NotificationType type) {
        if (type == null) return "";
        return switch (type) {
            case MEETING      -> "ការប្រជុំ";
            case DOCUMENT     -> "ឯកសារ";
            case ANNOUNCEMENT -> "សេចក្តីប្រកាស";
            case SYSTEM       -> "ប្រព័ន្ធ";
        };
    }
}