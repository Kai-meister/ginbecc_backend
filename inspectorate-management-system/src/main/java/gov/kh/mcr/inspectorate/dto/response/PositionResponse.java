package gov.kh.mcr.inspectorate.dto.response;


import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionResponse {

    private Integer       positionId;
    private String        positionCode;
    private String        positionName;
    private LocalDateTime createdAt;
}
