package gov.kh.mcr.inspectorate.exception;

import gov.kh.mcr.inspectorate.dto.response
        .ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access
        .AccessDeniedException;
import org.springframework.security.authentication
        .InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation
        .MethodArgumentTypeMismatchException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void>
    handleBusiness(
            BusinessException ex) {
        log.warn("Business: {}",
                ex.getMessage());
        return ApiResponse.error(
                ex.getMessage());
    }

    @ExceptionHandler(
            ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void>
    handleNotFound(
            ResourceNotFoundException ex) {
        log.warn("NotFound: {}",
                ex.getMessage());
        return ApiResponse.error(
                ex.getMessage());
    }

    @ExceptionHandler(
            DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void>
    handleDuplicate(
            DuplicateResourceException ex) {
        log.warn("Duplicate: {}",
                ex.getMessage());
        return ApiResponse.error(
                ex.getMessage());
    }

    @ExceptionHandler(
            UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void>
    handleUnauthorized(
            UnauthorizedException ex) {
        log.warn("Unauthorized: {}",
                ex.getMessage());
        return ApiResponse.error(
                ex.getMessage());
    }

    @ExceptionHandler(
            PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void>
    handlePermissionDenied(
            PermissionDeniedException ex) {

        log.warn(
                "Permission Denied: action={}"
                        + " required={} msg={}",
                ex.getAction(),
                ex.getRequiredPermission(),
                ex.getMessage());

        return ApiResponse.error(
                ex.getMessage());
    }

    @ExceptionHandler(
            DepartmentScopeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void>
    handleDepartmentScope(
            DepartmentScopeException ex) {

        log.warn(
                "Department Scope Violation:"
                        + " own={} target={}",
                ex.getOwnDepartment(),
                ex.getTargetDepartment());

        return ApiResponse.error(
                ex.getMessage());
    }


    @ExceptionHandler(
            AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void>
    handleAccessDenied(
            AccessDeniedException ex,
            jakarta.servlet.http
                    .HttpServletRequest
                    request) {

        String method =
                request.getMethod();
        String uri =
                request.getRequestURI();

        String actionKh =
                describeAction(method, uri);

        log.warn(
                "Access Denied: {} {} — "
                        + "user lacks required"
                        + " permission",
                method, uri);

        return ApiResponse.error(
                "អ្នកមិនមានសិទ្ធគ្រប់គ្រាន់"
                        + " ដើម្បី " + actionKh
                        + " — សូមទាក់ទង Admin"
                        + " ប្រសិនបើអ្នកគិតថា"
                        + " គណនីរបស់អ្នកត្រូវការ"
                        + " សិទ្ធបន្ថែម");
    }

    @ExceptionHandler(
            InsufficientAuthenticationException
                    .class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void>
    handleInsufficientAuth(
            InsufficientAuthenticationException
                    ex) {

        log.warn(
                "Insufficient Authentication:"
                        + " {}", ex.getMessage());

        return ApiResponse.error(
                "ត្រូវ Login ជាមុនសិន"
                        + " — Token មិនត្រឹមត្រូវ"
                        + " ឬផុតកំណត់");
    }

    @ExceptionHandler(
            ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>>
    handleConstraintViolation(
            ConstraintViolationException ex) {

        Map<String, String> errors =
                ex.getConstraintViolations()
                        .stream()
                        .collect(Collectors.toMap(
                                v -> {
                                    String path =
                                            v.getPropertyPath()
                                                    .toString();
                                    int dot =
                                            path.lastIndexOf('.');
                                    return dot >= 0
                                            ? path.substring(
                                            dot + 1)
                                            : path;
                                },
                                ConstraintViolation
                                        ::getMessage,
                                (a, b) -> a));

        log.warn("Validation: {}", errors);

        return ApiResponse.errorWithData(
                "ទិន្នន័យបញ្ចូលមិនត្រឹមត្រូវ",
                errors);
    }

    @ExceptionHandler(
            MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void>
    handleTypeMismatch(
            MethodArgumentTypeMismatchException
                    ex) {

        String param = ex.getName();
        String required =
                ex.getRequiredType() != null
                        ? ex.getRequiredType().getSimpleName()
                        : "unknown";
        String value =
                ex.getValue() != null
                        ? ex.getValue().toString()
                        : "null";

        String msg = buildTypeMismatchMsg(
                param, required, value);

        log.warn("Type mismatch: {}", msg);

        return ApiResponse.error(msg);
    }

    @ExceptionHandler(
            org.springframework.web.bind
                    .MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>>
    handleMethodArgumentNotValid(
            org.springframework.web.bind
                    .MethodArgumentNotValidException
                    ex) {

        Map<String, String> errors =
                new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fe ->
                        errors.put(
                                fe.getField(),
                                fe.getDefaultMessage()));

        return ApiResponse.errorWithData(
                "ទិន្នន័យបញ្ចូលមិនត្រឹមត្រូវ",
                errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(
            HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void>
    handleGenericException(
            Exception ex) {

        log.error(
                "Unhandled Exception: {}",
                ex.getMessage(), ex);

        return ApiResponse.error(
                "មានបញ្ហាក្នុងប្រព័ន្ធ"
                        + " — សូមព្យាយាមម្តងទៀត"
                        + " ឬទាក់ទង Admin"
                        + " ប្រសិនបើបញ្ហានៅតែកើតមាន");
    }

    private String buildTypeMismatchMsg(
            String param,
            String required,
            String value) {

        return switch (param) {
            case "month" ->
                    "តម្លៃខែ «month = " + value + "» មិនត្រឹមត្រូវឡើយ។ ទិន្នន័យចាំបាច់ត្រូវតែជាចំនួនគត់ (Integer) ចន្លោះពី ១ ដល់ ១២ (ឧទាហរណ៍៖ month=3)។";

            case "year" ->
                    "តម្លៃឆ្នាំ «year = " + value + "» មិនត្រឹមត្រូវឡើយ។ ទិន្នន័យចាំបាច់ត្រូវតែជាចំនួនគត់ (Integer) ចន្លោះពី ២០០០ ដល់ ២១០០ (ឧទាហរណ៍៖ year=2026)។";

            case "from", "to" ->
                    "ទម្រង់កាលបរិច្ឆេទ «" + param + " = " + value + "» មិនត្រឹមត្រូវឡើយ។ ទិន្នន័យចាំបាច់ត្រូវតែមានទម្រង់ yyyy-MM-dd (ឧទាហរណ៍៖ " + param + "=2026-01-01)។";

            case "departmentId", "officerId", "typeId", "userId", "roleId" ->
                    "អត្តសញ្ញាណ «" + param + " = " + value + "» មិនត្រឹមត្រូវឡើយ។ ទិន្នន័យចាំបាច់ត្រូវតែជាចំនួនគត់ធំជាង ០ (ឧទាហរណ៍៖ " + param + "=1)។";

            case "isRead" ->
                    "ស្ថានភាពការអាន «isRead = " + value + "» មិនត្រឹមត្រូវឡើយ។ ទិន្នន័យចាំបាច់ត្រូវតែជា «true» (ពិត) ឬ «false» (មិនពិត)។";

            case "expiringWithinDays" ->
                    "ចំនួនថ្ងៃជិតផុតកំណត់ «expiringWithinDays = " + value + "» មិនត្រឹមត្រូវឡើយ។ ទិន្នន័យចាំបាច់ត្រូវតែជាចំនួនគត់ចន្លោះពី ១ ដល់ ៣៦៥ ថ្ងៃ (ឧទាហរណ៍៖ expiringWithinDays=30)។";

            default ->
                    "ទម្រង់ទិន្នន័យ «" + param + " = " + value + "» មិនត្រឹមត្រូវឡើយ។ លក្ខខណ្ឌតម្រូវ៖ " + required;
        };
    }

    private String describeAction(
            String method, String uri) {

        String module = extractModule(uri);
        String moduleKh =
                translateModule(module);

        return switch (method) {
            case "GET" ->
                    "មើល" + moduleKh;
            case "POST" ->
                    "បង្កើត" + moduleKh + "ថ្មី";
            case "PUT", "PATCH" ->
                    "កែប្រែ" + moduleKh;
            case "DELETE" ->
                    "លុប" + moduleKh;
            default ->
                    "ធ្វើសកម្មភាពលើ" + moduleKh;
        };
    }

    private String extractModule(
            String uri) {
        String[] parts = uri.split("/");
        for (int i = 0; i < parts.length;
             i++) {
            if ("v1".equals(parts[i])
                    && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return "ទិន្នន័យ";
    }

    private String translateModule(
            String module) {
        return switch (module) {
            case "officers" -> "មន្ត្រី";
            case "contract-officers" ->
                    "មន្ត្រីកិច្ចសន្យា";
            case "documents" -> "ឯកសារ";
            case "document-types" ->
                    "ប្រភេទឯកសារ";
            case "approvals" ->
                    "ការអនុម័ត";
            case "meetings" -> "ការប្រជុំ";
            case "announcements" ->
                    "សេចក្ដីប្រកាស";
            case "notifications" ->
                    "ការជូនដំណឹង";
            case "users" -> "User";
            case "departments" ->
                    "នាយកដ្ឋាន";
            case "positions" -> "តំណែង";
            case "roles" -> "Role";
            case "permissions" ->
                    "សិទ្ធិ";
            case "reports" ->
                    "របាយការណ៍";
            case "audit-logs" ->
                    "ប្រវត្តិសកម្មភាព";
            case "attachments" ->
                    "ឯកសារភ្ជាប់";
            case "lookups" ->
                    "ទិន្នន័យយោង";
            default -> "ទិន្នន័យនេះ";
        };
    }
}