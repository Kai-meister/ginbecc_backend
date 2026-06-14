package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookupResponse {

    private String  statusCode;
    private String  labelKh;
    private String  labelEn;
    private Integer sortOrder;
    private Boolean isActive;
    private String  appliesTo;
    private String  blockReason;
}