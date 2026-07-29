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
    /**
     * Meetings not yet started, from now onward, excluding cancelled and
     * completed. Department-scoped exactly like {@link #todayMeetings}, so the
     * number always matches what the viewer could actually open.
     */
    private Long upcomingMeetings;
    private Long expiringDocuments;
    private Long expiringContracts;
    private Long  unreadNotifications;
}
