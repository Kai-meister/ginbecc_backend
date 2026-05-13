package gov.kh.mcr.inspectorate.controller;
import gov.kh.mcr.inspectorate.dto.request.PositionRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.PositionResponse;
import gov.kh.mcr.inspectorate.service.PositionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public ResponseEntity<ApiResponse<
    List<PositionResponse>>> getAll(@RequestParam(required = false) String keyword) {

        return ResponseEntity.ok(ApiResponse.success(
                        positionService.getAll(keyword),
                        "ទទួលបន្ជីតំណែង"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<
    PositionResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        positionService.getById(id),
                        "ទទួលបានតំណែង"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<PositionResponse>> create(
            @Valid @RequestBody PositionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        positionService.create(request),
                        "បង្កើតជោគជ័យ"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<PositionResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody PositionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                positionService.update(id, request),
                "កែប្រែជោគជ័យ"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id) {
        positionService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "លុបជោគជ័យ"));
    }
}
