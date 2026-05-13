package gov.kh.mcr.inspectorate.dto.response;


import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {

    private Long  totalOfficers;
    private Long   totalContractOfficers;
    private Map<String, Long> officersByGender;
    private Map<String, Long> officersByDepartment;
    private Long todayMeetings;
    private Long expiringDocuments;
    private Long expiringContracts;
    private Long  unreadNotifications;
}
