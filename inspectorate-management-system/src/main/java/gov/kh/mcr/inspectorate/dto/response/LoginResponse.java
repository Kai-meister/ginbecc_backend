package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Integer userId;
    private String userNameKh;
    private String userNameEn;
    private String email;
    private String  roleName;
    private List<String> permissions;
    private Boolean mustChangePassword;
}
