package gov.kh.mcr.inspectorate.mapper;
import gov.kh.mcr.inspectorate.dto.response.ActivityLogResponse;
import gov.kh.mcr.inspectorate.entity.ActivityLog;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActivityLogMapper {

    @Mapping(target = "userId",
            source = "user.userId")
    @Mapping(target = "userNameKh",
            source = "user.userNameKh")
    @Mapping(target = "userEmail",
            source = "userEmail")
    @Mapping(target = "actionLabel",
            expression =
                    "java(getActionLabel("
                            + "entity.getAction()))")
    ActivityLogResponse toResponse(ActivityLog entity);

    default String getActionLabel(String action) {
        if (action == null) return "";
        return switch (action) {
            case "CREATE"          -> "បង្កើត";
            case "UPDATE"          -> "កែប្រែ";
            case "DELETE"          -> "លុប";
            case "LOGIN"           -> "ចូលប្រព័ន្ធ";
            case "LOGOUT"          -> "ចាកចេញ";
            case "CHANGE_PASSWORD" ->
                    "ប្ដូរ Password";
            case "RESET_PASSWORD"  ->
                    "Reset Password";
            default -> action;
        };
    }
}