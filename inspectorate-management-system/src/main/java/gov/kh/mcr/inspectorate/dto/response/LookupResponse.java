package gov.kh.mcr.inspectorate.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LookupResponse {

    private String  statusCode;
    private String  labelKh;
    private String  labelEn;
    private Integer sortOrder;
    private Boolean isActive;
    private String  appliesTo;
    private String  blockReason;
}