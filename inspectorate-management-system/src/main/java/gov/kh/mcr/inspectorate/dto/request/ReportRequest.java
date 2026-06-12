package gov.kh.mcr.inspectorate.dto.request;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequest {

    // Officer Report Filters
    private Integer deptId;
    private String  officerStatus;

    // Meeting Report Filters
    private Integer roomId;
    private String  meetingStatus;

    // AuditLog Report Filters
    private Integer userId;
    private String  action;
    private String  entityType;

    // Shared Date Range
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;


}
