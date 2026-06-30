package gov.kh.mcr.inspectorate.security;

import com.fasterxml.jackson.databind
        .ObjectMapper;
import gov.kh.mcr.inspectorate.dto.response
        .ApiResponse;
import jakarta.servlet.http
        .HttpServletRequest;
import jakarta.servlet.http
        .HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http
        .MediaType;
import org.springframework.security.access
        .AccessDeniedException;
import org.springframework.security.web.access
        .AccessDeniedHandler;
import org.springframework.stereotype
        .Component;
import java.io.IOException;

// Fix — Component ត្រូវដាក់ក្នុង
// "security" package ដូចគ្នានឹង
// JwtAuthenticationFilter (ដើម្បីងាយ
// Maintain និង Spring Component Scan
// រកឃើញដោយស្វ័យប្រវត្តិ)
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ObjectMapper
            objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException
                    accessDeniedException)
            throws IOException {

        response.setStatus(403);
        response.setContentType(
                MediaType
                        .APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(
                "UTF-8");

        String moduleKh =
                translateModule(
                        extractModule(
                                request
                                        .getRequestURI()));

        String actionKh =
                describeAction(
                        request.getMethod(),
                        moduleKh);

        ApiResponse<Void> body =
                ApiResponse.error(
                        "អ្នកមិនមានសិទ្ធគ្រប់"
                                + "គ្រាន់ ដើម្បី "
                                + actionKh
                                + " — សូមទាក់ទង Admin"
                                + " ប្រសិនបើអ្នកគិតថា"
                                + " នេះជាកំហុស");

        response.getWriter().write(
                objectMapper
                        .writeValueAsString(
                                body));
    }

    private String extractModule(
            String uri) {
        String[] parts = uri.split("/");
        for (int i = 0;
             i < parts.length; i++) {
            if ("v1".equals(parts[i])
                    && i + 1
                    < parts.length) {
                return parts[i + 1];
            }
        }
        return "ទិន្នន័យនេះ";
    }

    private String describeAction(
            String method,
            String moduleKh) {
        return switch (method) {
            case "GET" ->
                    "មើល" + moduleKh;
            case "POST" ->
                    "បង្កើត" + moduleKh
                            + "ថ្មី";
            case "PUT", "PATCH" ->
                    "កែប្រែ" + moduleKh;
            case "DELETE" ->
                    "លុប" + moduleKh;
            default ->
                    "ធ្វើសកម្មភាពលើ"
                            + moduleKh;
        };
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
            case "meetings" ->
                    "ការប្រជុំ";
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