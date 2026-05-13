package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.ChangePasswordRequest;
import gov.kh.mcr.inspectorate.dto.request.LoginRequest;
import gov.kh.mcr.inspectorate.dto.request.RefreshTokenRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.LoginResponse;
import gov.kh.mcr.inspectorate.dto.response.ResetPasswordResponse;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>>
    login(
            @Valid @RequestBody LoginRequest req) {

        return ResponseEntity.ok(ApiResponse.success(
                authService.login(req),
                "ចូលប្រព័ន្ធជោគជ័យ"));
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization")
            String authHeader) {

        if (!StringUtils.hasText(authHeader)
                || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(
                    ApiResponse.success(null,
                            "ចាកចេញជោគជ័យ"));
        }

        authService.logout(
                authHeader.substring(7).trim());

        return ResponseEntity.ok(
                ApiResponse.success(null,
                        "ចាកចេញជោគជ័យ"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>>
    refreshToken(
            @RequestHeader(
                    value = "Refresh-Token",
                    required = false)
            String headerToken,
            @RequestBody(required = false)
            RefreshTokenRequest bodyReq) {

        String token = StringUtils.hasText(headerToken)
                ? headerToken.trim()
                : (bodyReq != null
                   ? bodyReq.getRefreshToken()
                   : null);

        if (!StringUtils.hasText(token)) {
            throw new BusinessException(
                    "Refresh Token ចាំបាច់");
        }

        return ResponseEntity.ok(ApiResponse.success(
                authService.refreshToken(token),
                "Token ថ្មីជោគជ័យ"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>>
    changePassword(
            @RequestParam
            @Positive(message = "userId > 0")
            Integer userId,
            @Valid @RequestBody
            ChangePasswordRequest req) {

        authService.changePassword(userId, req);

        return ResponseEntity.ok(
                ApiResponse.success(null,
                        "ប្ដូរ Password ជោគជ័យ"));
    }

    @PostMapping("/reset-password/{userId}")
    @PreAuthorize(
            "hasAnyAuthority('USER_CREATE',"
                    + "'USER_UPDATE')")
    public ResponseEntity<ApiResponse <ResetPasswordResponse>>
    resetPassword(
            @PathVariable
            @Positive Integer userId) {

        return ResponseEntity.ok(ApiResponse.success(
                authService.resetPassword(userId),
                "Reset Password ជោគជ័យ"));
    }
}