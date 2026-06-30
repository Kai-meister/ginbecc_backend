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
                        "ទាញយកបញ្ជីតំណែងមន្ត្រីបានដោយជោគជ័យ"));
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
                        "ទាញយកព័ត៌មានតំណែងមន្ត្រីបានដោយជោគជ័យ"));
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
                        "តំណែងមន្ត្រីត្រូវបានបង្កើតដោយជោគជ័យ"));
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
                        "ព័ត៌មានតំណែងមន្ត្រីត្រូវបានកែប្រែដោយជោគជ័យ"));
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
                        null, "តំណែងមន្ត្រីត្រូវបានលុបចេញពីប្រព័ន្ធដោយជោគជ័យ"));
    }
}