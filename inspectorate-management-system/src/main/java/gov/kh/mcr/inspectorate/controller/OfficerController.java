package gov.kh.mcr.inspectorate.controller;


import gov.kh.mcr.inspectorate.dto.request.OfficerRequest;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.dto.response.OfficerResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.Officer;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.repository.OfficerRepository;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import gov.kh.mcr.inspectorate.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/officers")
@RequiredArgsConstructor
public class OfficerController {

    private final UserProfileImageService profileImageService;
    private final OfficerService officerService;
    private final UserRepository userRepository;
    private final OfficerRepository officerRepository;
    private final MinioService minioService;

    // GET /officers
    @GetMapping
    @PreAuthorize("hasAuthority('OFFICER_VIEW')")
    public ResponseEntity<ApiResponse<
    PageResponse<OfficerResponse>>>
    getAll(
            @RequestParam(
                    defaultValue = "0")
            int page,
            @RequestParam(
                    defaultValue = "20")
            int size,
            @RequestParam(required = false)
            Integer departmentId,
            @RequestParam(required = false)
            String status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        officerService.getAll(
                                page, size,
                                departmentId, status),
                        "ទទួលបន្ជីមន្ត្រី"));
    }

    // GET /officers/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_VIEW')")
    public ResponseEntity<ApiResponse<
    OfficerResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        officerService.getById(id),
                        "ទទួលបានមន្ត្រី"));
    }

    // POST /officers
    @PostMapping
    @PreAuthorize("hasAuthority('OFFICER_CREATE')")
    public ResponseEntity<ApiResponse<
    OfficerResponse>>
    create(
            @Valid @RequestBody
            OfficerRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        officerService.create(
                                request),
                        "បង្កើតមន្ត្រីជោគជ័យ"));
    }

    // PUT /officers/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_UPDATE')")
    public ResponseEntity<ApiResponse<
    OfficerResponse>>
    update(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            OfficerRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        officerService.update(
                                id, request),
                        "កែប្រែមន្ត្រីជោគជ័យ"));
    }

    // PATCH /officers/{id}/status
//    @PatchMapping("/{id}/status")
//    @PreAuthorize("hasAuthority('OFFICER_UPDATE')")
//    public ResponseEntity<ApiResponse<OfficerResponse>> updateStatus(
//            @PathVariable Integer id,
//            @Valid @RequestBody StatusRequest request) {
//        return ResponseEntity.ok(ApiResponse.success(
//                officerService.updateStatus(id, request),
//                "ស្ថានភាពមន្ត្រីត្រូវបានផ្លាស់ប្តូរដោយជោគជ័យ"));
//    }

    // DELETE /officers/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_DELETE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        officerService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "លុបមន្ត្រីជោគជ័យ"));
    }

//    // GET /officers/near-retirement
//    @GetMapping("/near-retirement")
//    @PreAuthorize("hasAuthority('OFFICER_VIEW')")
//    public ResponseEntity<ApiResponse<List<OfficerResponse>>>
//    getNearRetirement() {
//        return ResponseEntity.ok(ApiResponse.success(
//                officerService.getNearRetirement(),
//                "ទាញយកបញ្ជីមន្ត្រីជិតដល់អាយុនិវត្តន៍បានដោយជោគជ័យ"));
//    }

    // POST /officers/{id}/profile-image
    @PostMapping(value = "/{id}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ATTACHMENT_UPLOAD')")
    public ResponseEntity<ApiResponse<
    OfficerResponse>>
    uploadProfileImage(
            @PathVariable
            @Positive Integer id,
            @RequestParam("file")
            MultipartFile file) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        officerService
                                .uploadProfileImage(
                                        id, file),
                        "Upload រូបភាព Profile"
                                + " ជោគជ័យ"));
    }

//    @GetMapping("/{id}/profile-image")
//    @PreAuthorize("hasAuthority('OFFICER_VIEW')")
//    public ResponseEntity<ApiResponse<String>>
//    getProfileImageUrl(@PathVariable @Positive Integer id) {
//        String url = userRepository.findByOfficer_OfficerId(id).map(u -> profileImageService.getProfileImageUrl(
//                u.getUserId()))
//                .orElseGet(() -> {
//                    Officer o = officerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("មន្ត្រី", id));
//                    return o.getProfileAttachment() != null ? minioService.getPresignedUrl(o.getProfileAttachment().getFilePath())
//                            : "/api/v1/static/avatars" + "/officer.png";
//                });
//
//
//        return ResponseEntity.ok(
//                ApiResponse.success(url, "ទាញយកតំណភ្ជាប់រូបភាពប្រវត្តិរូបបានជោគជ័យ"));
//    }

//@GetMapping("/{id}/profile-image")
//@PreAuthorize("hasAuthority('OFFICER_VIEW')")
//public ResponseEntity<ApiResponse<String>>
//getProfileImageUrl(@PathVariable @Positive Integer id) {
//    Officer o = officerRepository.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException("មន្ត្រី", id));
//
//    String url = o.getProfileAttachment() != null
//            ? minioService.getPresignedUrl(o.getProfileAttachment().getFilePath())
//            : "/api/v1/static/avatars/officer.png";
//
//    return ResponseEntity.ok(
//            ApiResponse.success(url, "ទាញយកតំណភ្ជាប់រូបភាពប្រវត្តិរូបបានជោគជ័យ"));
//}

    @GetMapping("/{id}/profile-image")
    @PreAuthorize("hasAuthority('OFFICER_VIEW')")
    public ResponseEntity<ApiResponse<String>>
    getProfileImageUrl(@PathVariable @Positive Integer id) {

        String url = officerService.getProfileImageUrl(id);

        return ResponseEntity.ok(
                ApiResponse.success(url,
                        "ទាញយកតំណភ្ជាប់រូបភាពប្រវត្តិរូបបានជោគជ័យ"));
    }


    @DeleteMapping("/{id}/profile-image")
    @PreAuthorize("hasAuthority('OFFICER_DELETE')")
    public ResponseEntity<ApiResponse<
    OfficerResponse>>
    deleteProfileImage(@PathVariable @Positive Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        officerService
                                .deleteProfileImage(id),
                        "លុប Profile Image"
                                + " ជោគជ័យ"));
    }


}
