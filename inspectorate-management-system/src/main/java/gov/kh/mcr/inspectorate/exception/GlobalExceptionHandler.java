package gov.kh.mcr.inspectorate.exception;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.util.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleDuplicate(DuplicateResourceException ex) {
        return build(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleBusiness(BusinessException ex) {
        return build(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleUnauthorized(UnauthorizedException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleForbidden(ForbiddenException ex) {
        return build(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(RoomConflictException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>>
    handleRoomConflict(RoomConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .data(Map.of(
                                "roomCode",      ex.getRoomCode(),
                                "conflictTitle", ex.getConflictTitle(),
                                "conflictTime",  ex.getConflictTime()))
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>>
    handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach((FieldError fe) ->
                        errors.put(fe.getField(),
                                fe.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .code("VALIDATION_FAILED")
                        .message("ទិន្នន័យបញ្ចូលមិនត្រឹមត្រូវ")
                        .data(errors)
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleConstraint(
            ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "CONSTRAINT_VIOLATION", ex.getMessage()));
    }

    @ExceptionHandler(
            MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleMissingParam(
            MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "MISSING_PARAMETER",
                        "Parameter ខ្វះ: " + ex.getParameterName()));
    }

    @ExceptionHandler(
            MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "TYPE_MISMATCH",
                        "Parameter ប្រភេទខុស: "
                                + ex.getName()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleUnreadable(
            HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "UNREADABLE_BODY",
                        "Request body មិនអាចអានបាន"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(
                        "ACCESS_DENIED", "គ្មានសិទ្ធិ"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        "INVALID_CREDENTIALS",
                        "Email ឬ Password មិនត្រឹមត្រូវ"));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleLocked(LockedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        "ACCOUNT_LOCKED",
                        "គណនីត្រូវបានចាក់សោ"));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleDisabled(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        "ACCOUNT_DISABLED",
                        "គណនីត្រូវបានបិទ"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleUsernameNotFound(
            UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        "USER_NOT_FOUND",
                        "រកមិនឃើញ User"));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleMaxUpload(
            MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(
                        HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error(
                        "FILE_TOO_LARGE",
                        "ទំហំ File ធំពេក (អតិបរមា 10MB)"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>>
    handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(
                        HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "INTERNAL_ERROR", "មានបញ្ហាប្រព័ន្ធ"));
    }

    private ResponseEntity<ApiResponse<Void>> build(
            HttpStatus status, ApiException ex) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(
                        ex.getCode(), ex.getMessage()));
    }
}