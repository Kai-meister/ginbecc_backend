package gov.kh.mcr.inspectorate.dto.request;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogContext {

    private Integer userId;
    private String  userEmail;
    private String  ipAddress;
    private String  userAgent;
}