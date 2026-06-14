package gov.kh.mcr.inspectorate.exception;

import gov.kh.mcr.inspectorate.dto.response
        .ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
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
        log.warn("Business: {}", ex.getMessage());
        return ApiResponse.error(
                ex.getMessage());
    }

    @ExceptionHandler(
            ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void>
    handleNotFound(
            ResourceNotFoundException ex) {
        return ApiResponse.error(
                ex.getMessage());
    }

    @ExceptionHandler(
            DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void>
    handleDuplicate(
            DuplicateResourceException ex) {
        return ApiResponse.error(
                ex.getMessage());
    }

    @ExceptionHandler(
            UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void>
    handleUnauthorized(
            UnauthorizedException ex) {
        return ApiResponse.error(
                ex.getMessage());
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
                                    int dot = path.lastIndexOf('.');
                                    return dot >= 0
                                            ? path.substring(dot + 1)
                                            : path;
                                },
                                ConstraintViolation::getMessage,
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

    private String buildTypeMismatchMsg(
            String param,
            String required,
            String value) {

        return switch (param) {
            case "month" ->
                    "តម្លៃ [" + value + "] សម្រាប់ " + param + " មិនត្រឹមត្រូវ"
                            + " - សូមបញ្ចូលលេខពី ១ ដល់ ១២ (ឧ. ខែ: 3)";

            case "year" ->
                    "តម្លៃ [" + value + "] សម្រាប់ " + param + " មិនត្រឹមត្រូវ"
                            + " - សូមបញ្ចូលឆ្នាំចាប់ពី ២០០០ ដល់ ២១០០ (ឧ. ឆ្នាំ: 2025)";

            case "from", "to" ->
                    "ទម្រង់កាលបរិច្ឆេទ [" + value + "] សម្រាប់ " + param + " មិនត្រឹមត្រូវ"
                            + " - ទម្រង់ត្រឹមត្រូវគឺ yyyy-MM-dd (ឧ. " + param + ": 2025-01-01)";

            case "departmentId", "officerId", "typeId", "userId", "roleId" ->
                    "លេខសម្គាល់ [" + value + "] សម្រាប់ " + param + " មិនត្រឹមត្រូវ"
                            + " - តម្លៃត្រូវជាលេខធំជាង ០ (ឧ. " + param + ": 1)";

            case "isRead" ->
                    "តម្លៃ [" + value + "] សម្រាប់ isRead មិនត្រឹមត្រូវ"
                            + " - សូមបញ្ចូល true ឬ false";

            case "expiringWithinDays" ->
                    "តម្លៃ [" + value + "] សម្រាប់ សម្រាប់ចំនួនថ្ងៃផុតកំណត់មិនត្រឹមត្រូវ"
                            + " - សូមបញ្ចូលលេខពី ១ ដល់ ៣៦៥ (ឧ. សម្រាប់ចំនួនថ្ងៃផុតកំណត់មិនត្រឹមត្រូវ: 30)";

            default ->
                    "តម្លៃ [" + value + "] សម្រាប់ " + param + " មិនត្រឹមត្រូវ"
                            + " - ទម្រង់ដែលត្រូវការគឺ " + required;
        };
    }
}