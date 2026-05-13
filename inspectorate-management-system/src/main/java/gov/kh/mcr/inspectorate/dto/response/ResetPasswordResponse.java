package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ResetPasswordResponse {

    private Integer userId;
    private String  email;
    private String  userNameKh;

    private String  temporaryPassword;

    private Boolean mustChangePassword;

    private String  message;
}