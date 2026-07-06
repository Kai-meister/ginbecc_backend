package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.DeviceTokenRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.entity.UserDevice;
import gov.kh.mcr.inspectorate.exception.UnauthorizedException;
import gov.kh.mcr.inspectorate.repository.UserDeviceRepository;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final UserDeviceRepository userDeviceRepository;
    private final SecurityUtils securityUtils;

    // Register or refresh device token
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> registerDevice(
            @Valid @RequestBody DeviceTokenRequest request) {
        userDeviceRepository.findByToken(request.getToken())
                .ifPresentOrElse(
                        existing -> {
                            existing.setLastSeenAt(LocalDateTime.now());
                            userDeviceRepository.save(existing);
                        },
                        () -> {
                            UserDevice newDevice = UserDevice.builder()
                                    .user(securityUtils.getCurrentUser()
                                            .orElseThrow(() ->
                                                    new UnauthorizedException(
                                                            "ត្រូវ Login")))
                                    .token(request.getToken())
                                    .platform(request.getPlatform())
                                    .lastSeenAt(LocalDateTime.now())
                                    .build();
                            userDeviceRepository.save(newDevice);
                        });

        return ResponseEntity.ok(ApiResponse.success(null, "Device registered successfully"));
    }

    // Unregister device token on logout
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> unregisterDevice(
            @RequestParam String token) {
        userDeviceRepository.deleteByToken(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Device unregistered successfully"));
    }
}