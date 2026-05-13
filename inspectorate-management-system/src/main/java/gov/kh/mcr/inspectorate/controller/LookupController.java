package gov.kh.mcr.inspectorate.controller;
import gov.kh.mcr.inspectorate.dto.response.AllLookupsResponse;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.LookupResponse;
import gov.kh.mcr.inspectorate.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ពន្យល់:
 *  - Public endpoints (no auth required)
 *  - Frontend ហៅ onLoad ដើម្បី populate Dropdowns
 *  - Cached ក្នុង Redis — Response លឿន
 */
@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
public class LookupController {

    private final LookupService lookupService;

    @GetMapping("/officer-statuses")
    public ResponseEntity<ApiResponse<
    List<LookupResponse>>>
    getOfficerStatuses() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        lookupService.getOfficerStatuses(),
                        "ស្ថានភាពមន្ត្រី"));
    }


    @GetMapping("/document-statuses")
    public ResponseEntity<ApiResponse<
    List<LookupResponse>>>
    getDocumentStatuses(
            @RequestParam(required = false)
            String appliesTo) {

        List<LookupResponse> result =
                (appliesTo != null
                        && !appliesTo.isBlank())
                        ? lookupService
                          .getDocumentStatusesByTarget(
                                  appliesTo.toUpperCase())
                        : lookupService.getDocumentStatuses();

        return ResponseEntity.ok(
                ApiResponse.success(
                        result, "ស្ថានភាពឯកសារ"));
    }

    @GetMapping("/meeting-statuses")
    public ResponseEntity<ApiResponse<
    List<LookupResponse>>>
    getMeetingStatuses() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        lookupService.getMeetingStatuses(),
                        "ស្ថានភាពប្រជុំ"));
    }

    @GetMapping("/announcement-statuses")
    public ResponseEntity<ApiResponse<
    List<LookupResponse>>>
    getAnnouncementStatuses() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        lookupService
                                .getAnnouncementStatuses(),
                        "ស្ថានភាពប្រកាស"));
    }


    @GetMapping("/user-statuses")
    public ResponseEntity<ApiResponse<
    List<LookupResponse>>>
    getUserStatuses() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        lookupService.getUserStatuses(),
                        "ស្ថានភាពអ្នកប្រើ"));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<
    AllLookupsResponse>>
    getAllLookups() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        AllLookupsResponse.builder()
                                .officerStatuses(
                                        lookupService
                                                .getOfficerStatuses())
                                .documentStatuses(
                                        lookupService
                                                .getDocumentStatuses())
                                .meetingStatuses(
                                        lookupService
                                                .getMeetingStatuses())
                                .announcementStatuses(
                                        lookupService
                                                .getAnnouncementStatuses())
                                .userStatuses(
                                        lookupService
                                                .getUserStatuses())
                                .build(),
                        "Lookups ទាំងអស់"));
    }
}