package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionResponse {

    private Integer   positionId;
    private Integer   departmentId;
    private String        departmentName;
    private String        positionCode;
    private String        positionName;
    private String        positionNameEn;
    private String        description;
    private LocalDateTime createdAt;
}