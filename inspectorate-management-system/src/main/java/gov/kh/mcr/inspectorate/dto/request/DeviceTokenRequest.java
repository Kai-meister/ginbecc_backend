package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceTokenRequest {

    @NotBlank(message = "Token must not be blank")
    private String token;

    private String platform; // WEB / ANDROID / IOS
}
