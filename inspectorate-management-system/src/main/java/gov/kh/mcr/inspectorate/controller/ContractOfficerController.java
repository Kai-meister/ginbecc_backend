package gov.kh.mcr.inspectorate.controller;
import gov.kh.mcr.inspectorate.dto.request.ContractOfficerRequest;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.ContractOfficerResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.ContractOfficer;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.repository.ContractOfficerRepository;
import gov.kh.mcr.inspectorate.repository.OfficerRepository;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import gov.kh.mcr.inspectorate.service.ContractOfficerService;
import gov.kh.mcr.inspectorate.service.MinioService;
import gov.kh.mcr.inspectorate.service.OfficerService;
import gov.kh.mcr.inspectorate.service.UserProfileImageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/contract-officers")
@RequiredArgsConstructor
public class ContractOfficerController {

    private final ContractOfficerService contractOfficerService;
    private final UserProfileImageService profileImageService;
    private final UserRepository userRepository;
    private final ContractOfficerRepository contractOfficerRepository;
    private final MinioService minioService;


    // GET /contract-officers
    @GetMapping
    @PreAuthorize("hasAuthority('CONTRACT_OFFICER_VIEW')")
    public ResponseEntity<ApiResponse<
            PageResponse<ContractOfficerResponse>>>
    getAll(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size,
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            Integer dept,
            @RequestParam(required = false)
            Integer expiring_within) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        contractOfficerService.getAll(
                                page, size,
                                status, dept,
                                expiring_within),
                        "ទទួលបានបញ្ជីមន្ត្រីកិច្ចសន្យា"));
    }

    // GET /contract-officers/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTRACT_OFFICER_VIEW')")
    public ResponseEntity<ApiResponse<
    ContractOfficerResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        contractOfficerService.getById(id),
                        "ទទួលបានព័ត៌មានមន្ត្រីកិច្ចសន្យា"));
    }

    // POST /contract-officers
    @PostMapping
    @PreAuthorize("hasAuthority('CONTRACT_OFFICER_CREATE')")
    public ResponseEntity<ApiResponse<
    ContractOfficerResponse>>
    create(
            @Valid @RequestBody
            ContractOfficerRequest req) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        contractOfficerService.create(req),
                        "បានបង្កើតមន្ត្រីកិច្ចសន្យាដោយជោគជ័យ"));
    }
    // PUT /contract-officers/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTRACT_OFFICER_UPDATE')")
    public ResponseEntity<ApiResponse<
    ContractOfficerResponse>>
    update(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            ContractOfficerRequest req) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        contractOfficerService.update(id, req),
                        "បានកែប្រែមន្ត្រីកិច្ចសន្យាដោយជោគជ័យ"));
    }

    // PATCH /contract-officers/{id}/status
//    @PatchMapping("/{id}/status")
//    @PreAuthorize("hasAuthority('CONTRACT_OFFICER_UPDATE')")
//    public ResponseEntity<ApiResponse<
//    ContractOfficerResponse>>
//    updateStatus(
//            @PathVariable
//            @Positive Integer id,
//            @Valid @RequestBody
//            StatusRequest req) {
//
//        return ResponseEntity.ok(
//                ApiResponse.success(
//                        contractOfficerService.updateStatus(id, req),
//                        "បានផ្លាស់ប្ដូរស្ថានភាពមន្ត្រីកិច្ចសន្យាដោយជោគជ័យ"));
//    }

    // DELETE /contract-officers/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTRACT_OFFICER_DELETE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        contractOfficerService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "បានលុបមន្ត្រីកិច្ចសន្យាដោយជោគជ័យ"));
    }

    @PostMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ATTACHMENT_UPLOAD')")
    public ResponseEntity<ApiResponse<
    ContractOfficerResponse>>
    uploadProfileImage(
            @PathVariable
            @Positive Integer id,
            @RequestParam("file")
            MultipartFile file) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        contractOfficerService
                                .uploadProfileImage(
                                        id, file),
                        "Upload រូបភាព Profile"
                                + " ជោគជ័យ"));
    }

//    @GetMapping("/{id}/profile-image")
//    @PreAuthorize(
//            "hasAuthority('OFFICER_VIEW')")
//    public ResponseEntity<ApiResponse<String>>
//    getProfileImageUrl(
//            @PathVariable
//            @Positive Integer id) {
//
//        String url = userRepository
//                .findByContractOfficer_ContractOfficerId(
//                        id)
//                .map(u ->
//                        profileImageService
//                                .getProfileImageUrl(
//                                        u.getUserId()))
//                .orElseGet(() -> {
//                    ContractOfficer co =
//                            contractOfficerRepository.findById(id)
//                                    .orElseThrow(() ->
//                                            new
//                                                    ResourceNotFoundException(
//                                                    "មន្ត្រីកិច្ចសន្យា",
//                                                    id));
//                    return co.getProfileAttachment()
//                            != null
//                            ? minioService
//                            .getPresignedUrl(
//                                    co
//                                            .getProfileAttachment()
//                                            .getFilePath())
//                            : "/api/v1/static/avatars"
//                              + "/contract_officer"
//                              + ".png";
//                });
//
//        return ResponseEntity.ok(
//                ApiResponse.success(
//                        url, "ទាញយកតំណភ្ជាប់រូបភាពប្រវត្តិរូបបានជោគជ័យ"));
//    }

    @DeleteMapping("/{id}/profile-image")
    @PreAuthorize(
            "hasAuthority('ATTACHMENT_DELETE')")
    public ResponseEntity<ApiResponse<
    ContractOfficerResponse>>
    deleteProfileImage(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        contractOfficerService
                                .deleteProfileImage(id),
                        "លុប Profile Image"
                                + " ជោគជ័យ"));
    }

    @GetMapping("/{id}/profile-image")
    @PreAuthorize("hasAuthority('OFFICER_VIEW')")
    public ResponseEntity<ApiResponse<String>>
    getProfileImageUrl(@PathVariable @Positive Integer id) {

        String url = contractOfficerService.getProfileImageUrl(id);

        return ResponseEntity.ok(
                ApiResponse.success(url,
                        "ទាញយកតំណភ្ជាប់រូបភាពប្រវត្តិរូបបានជោគជ័យ"));
    }
}
