package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request
        .PositionRequest;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.service
        .PositionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost
        .PreAuthorize;
import org.springframework.validation.annotation
        .Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;
    @GetMapping
    @PreAuthorize("hasAuthority('POSITION_VIEW')")
    public ResponseEntity<ApiResponse<
    List<PositionResponse>>>
    getAll(
            @RequestParam(required = false)
            Integer departmentId,
            @RequestParam(required = false)
            String keyword) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        positionService.getAll(
                                departmentId, keyword),
                        "ទទួលបានបញ្ជីតំណែង"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('POSITION_VIEW')")
    public ResponseEntity<ApiResponse<
    PositionResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        positionService.getById(id),
                        "ទទួលបានព័ត៌មានតំណែង"));
    }

    @PostMapping
    @PreAuthorize(
            "hasAuthority('POSITION_MANAGE')")
    public ResponseEntity<ApiResponse<
    PositionResponse>>
    create(
            @Valid @RequestBody
            PositionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        positionService.create(request),
                        "បង្កើតតំណែងបានជោគជ័យ"));
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('POSITION_MANAGE')")
    public ResponseEntity<ApiResponse<
    PositionResponse>>
    update(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            PositionRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        positionService.update(
                                id, request),
                        "កែប្រែតំណែងបានជោគជ័យ"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('POSITION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        positionService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "លុបតំណែងបានជោគជ័យ"));
    }
}