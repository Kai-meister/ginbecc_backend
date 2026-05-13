package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.DashboardSummaryResponse;
import gov.kh.mcr.inspectorate.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>>
    getSummary(
            @RequestParam(required = false) Integer userId) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getSummary(userId),
                "ព័ត៌មានសង្ខេប"));
    }
}
