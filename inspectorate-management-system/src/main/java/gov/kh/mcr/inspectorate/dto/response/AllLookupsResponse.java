package gov.kh.mcr.inspectorate.dto.response;


import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AllLookupsResponse {

    private List<LookupResponse> officerStatuses;
    private List<LookupResponse> documentStatuses;
    private List<LookupResponse> meetingStatuses;
    private List<LookupResponse> announcementStatuses;
    private List<LookupResponse> userStatuses;
}