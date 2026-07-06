package gov.kh.mcr.inspectorate.exception;

import lombok.Getter;

@Getter
public class PermissionDeniedException
        extends RuntimeException {
    private final String action;

    private final String requiredPermission;

    public PermissionDeniedException(
            String action,
            String requiredPermission) {
        super(buildMessage(
                action, requiredPermission));
        this.action = action;
        this.requiredPermission =
                requiredPermission;
    }

    public PermissionDeniedException(
            String message) {
        super(message);
        this.action = null;
        this.requiredPermission = null;
    }

    private static String buildMessage(
            String action,
            String requiredPermission) {
        return "អ្នកមិនមានសិទ្ធគ្រប់គ្រាន់"
                + " ដើម្បី " + action
                + " — ត្រូវការ Permission: "
                + requiredPermission
                + " — សូមទាក់ទង Admin"
                + " ប្រសិនបើអ្នកគិតថា"
                + " នេះជាកំហុស";
    }
}