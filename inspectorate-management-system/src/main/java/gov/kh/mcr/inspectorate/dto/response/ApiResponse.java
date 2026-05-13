package gov.kh.mcr.inspectorate.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp =
            LocalDateTime.now();

    public static <T> ApiResponse<T> success(
            T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(
            String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> created(
            T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("CREATED")
                .message(message)
                .data(data)
                .build();
    }
}
