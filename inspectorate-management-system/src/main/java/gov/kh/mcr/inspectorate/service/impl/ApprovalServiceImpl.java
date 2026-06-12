package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .DocumentStatusCode;
import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper
        .ApprovalMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security
        .SecurityUtils;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;
import org.springframework.web.context.request
        .RequestContextHolder;
import org.springframework.web.context.request
        .ServletRequestAttributes;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalServiceImpl
        implements ApprovalService {

    private final ApprovalRepository             approvalRepo;
    private final DocumentRepository             documentRepo;
    private final OfficerRepository              officerRepo;
    private final LookupDocumentStatusRepository statusRepo;
    private final ApprovalMapper                 approvalMapper;
    private final SecurityUtils                  securityUtils;
    private final NotificationService            notificationService;
    private final ActivityLogService             activityLogService;

    @Override
    public ApprovalResponse requestApproval(
            ApprovalRequest request) {

        Document document =
                documentRepo.findById(
                                request.getDocumentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "ឯកសារ",
                                        request.getDocumentId()));

        validateDocumentStatus(document);

        checkNoDuplicatePending(
                request.getDocumentId());

        Officer currentOfficer =
                resolveCurrentOfficer();

        validateDocumentOwner(
                document, currentOfficer);

//        validateNotSelfApproval(
//                document, currentOfficer);
//

        LookupDocumentStatus pending =
                findStatus(
                        DocumentStatusCode.PENDING
                                .getCode());

        Approval approval = Approval.builder()
                .document(document)
                .requestedBy(currentOfficer)
                .statusCode(pending)
                .comment(request.getNote())
                .requestedAt(LocalDateTime.now())
                .build();

        document.setStatusCode(pending);
        documentRepo.save(document);

        Approval saved =
                approvalRepo.save(approval);

        activityLogService.log(
                "CREATE", "Approval",
                saved.getApprovalId(),
                "ស្នើ: "
                        + document.getDocumentName(),
                buildContext());

        return approvalMapper.toResponse(saved);
    }

    @Override
    public ApprovalResponse decide(
            Integer approvalId,
            DecideRequest request) {

        request.validate();

        Approval approval =
                approvalRepo.findById(approvalId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Approval", approvalId));

        // ១. Must be PENDING
        validateApprovalPending(approval);

        // Fix ២. Check approver permission
        // by Department
        User approver =
                securityUtils.getCurrentUser()
                        .orElseThrow(() ->
                                new UnauthorizedException(
                                        "សូមចូលប្រើប្រាស់ប្រព័ន្ធជាមុនសិន"));

        validateApproverDepartment(
                approval, approver);

        LookupDocumentStatus newStatus =
                findStatus(request.getStatusCode());

        approval.setStatusCode(newStatus);
        approval.setApprovedBy(approver);
        approval.setComment(request.getComment());
        approval.setDecidedAt(LocalDateTime.now());

        Document doc = approval.getDocument();
        doc.setStatusCode(newStatus);
        documentRepo.save(doc);

        Approval saved =
                approvalRepo.save(approval);

        triggerNotification(
                approval,
                request.getStatusCode());

        activityLogService.log(
                "UPDATE", "Approval",
                approvalId,
                request.getStatusCode()
                        + ": " + doc.getDocumentName(),
                buildContext());

        return approvalMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApprovalResponse> getAll(
            int page, int size,
            String status,
            Integer officerId,
            Integer documentId) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("requestedAt").descending());

        Page<Approval> result;

        if (documentId != null) {
            result = approvalRepo
                    .findByDocument_DocumentId(
                            documentId, pageable);
        } else if (status != null
                && officerId != null) {
            result = approvalRepo
                    .findByStatusCode_StatusCodeAndRequestedBy_OfficerId(
                            status, officerId, pageable);
        } else if (status != null) {
            result = approvalRepo
                    .findByStatusCode_StatusCode(
                            status, pageable);
        } else if (officerId != null) {
            result = approvalRepo
                    .findByRequestedBy_OfficerId(
                            officerId, pageable);
        } else {
            result = approvalRepo
                    .findAll(pageable);
        }

        return PageResponse.of(
                result.map(approvalMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalResponse getById(Integer id) {
        return approvalMapper.toResponse(
                approvalRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Approval", id)));
    }

    // ─────────────────────────────────────────────
    // DECIDE — Admin approve or reject
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApprovalResponse> getMyPending(int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("requestedAt").ascending());

        return PageResponse.of(
                approvalRepo
                        .findByStatusCode_StatusCode(
                                DocumentStatusCode
                                        .PENDING.getCode(),
                                pageable)
                        .map(approvalMapper::toResponse));
    }

    private void validateDocumentStatus(
            Document document) {

        String code =
                document.getStatusCode() != null
                        ? document.getStatusCode()
                          .getStatusCode()
                        : "";

        if (!DocumentStatusCode.isDraft(code)) {
            throw new BusinessException(
                    "សកម្មភាពនេះអាចធ្វើទៅបានតែលើឯកសារដែលស្ថិតក្នុងស្ថានភាព «ពង្រាង» ប៉ុណ្ណោះ"
                            + "  ស្ថានភាពបច្ចុប្បន្ន៖ " + code);
        }
    }

    // ── 2. No duplicate pending ───────────────────
    private void checkNoDuplicatePending(
            Integer documentId) {

        boolean has = approvalRepo
                .existsByDocument_DocumentIdAndStatusCode_StatusCode(
                        documentId,
                        DocumentStatusCode
                                .PENDING.getCode());

        if (has) {
            throw new BusinessException(
                    "ឯកសារនេះ មាន Approval"
                            + " PENDING រួចហើយ");
        }
    }

    // Fix ── 3. Document owner check ───────────────
    // Officer can only submit OWN document
    private void validateDocumentOwner(
            Document document,
            Officer currentOfficer) {

        if (document.getOfficer() == null) {
            throw new BusinessException(
                    "ឯកសារ មិនមាន Officer");
        }

        if (!document.getOfficer()
                .getOfficerId()
                .equals(currentOfficer
                        .getOfficerId())) {
            throw new BusinessException(
                    "មិនអាចស្នើ Approval"
                            + " ឯកសាររបស់ Officer ផ្សេង"
                            + " — ត្រូវជា Owner ប៉ុណ្ណោះ");
        }
    }

//    // ── 4. Self approval check ────────────────────
//    private void validateNotSelfApproval(
//            Document document,
//            Officer officer) {
//
//        if (document.getOfficer() != null
//                && document.getOfficer()
//                .getOfficerId()
//                .equals(officer
//                        .getOfficerId())) {
//            throw new BusinessException(
//                    "មន្ត្រីមិនអាច Approve"
//                            + " ឯកសារខ្លួនឯង");
//        }
//    }

    // ── 5. Approval pending check ─────────────────
    private void validateApprovalPending(
            Approval approval) {

        String code =
                approval.getStatusCode() != null
                        ? approval.getStatusCode()
                          .getStatusCode()
                        : "";

        if (!DocumentStatusCode
                .isPending(code)) {
            throw new BusinessException(
                    "Approval មិន PENDING"
                            + " — " + code
                            + " — មិនអាច Decide");
        }
    }

    // Fix ── 6. Approver department check ──────────
    // Admin can only approve documents
    // from their managed department
    // SUPER_ADMIN bypasses check
    private void validateApproverDepartment(
            Approval approval,
            User approver) {

        // SUPER_ADMIN bypass
        if ("SUPER_ADMIN".equals(
                approver.getRole()
                        .getRoleName())) {
            return;
        }

        // Admin must have officer
        // to determine department
        if (approver.getOfficer() == null) {
            // Admin without officer
            // = system admin → allow
            return;
        }

        // Get document's department
        Department docDept =
                approval.getDocument()
                        .getOfficer()
                        .getDepartment();

        if (docDept == null) return;

        // Get approver's department
        Department approverDept =
                approver.getOfficer()
                        .getDepartment();

        if (approverDept == null) return;

        // Fix — Block cross-department approval
        if (!docDept.getDepartmentId()
                .equals(approverDept
                        .getDepartmentId())) {
            throw new BusinessException(
                    "Admin នាយកដ្ឋាន \""
                            + approverDept
                            .getDepartmentName()
                            + "\" មិនអាច Approve"
                            + " ឯកសារ នាយកដ្ឋាន \""
                            + docDept.getDepartmentName()
                            + "\"");
        }
    }

    // ── Resolve current officer ───────────────────
    private Officer resolveCurrentOfficer() {
        return securityUtils.getCurrentUser()
                .map(user -> {
                    if (user.getOfficer() == null) {
                        throw new BusinessException(
                                "Admin មិនអាច"
                                        + " Submit Approval"
                                        + " — Officer Account"
                                        + " ប៉ុណ្ណោះ");
                    }
                    return user.getOfficer();
                })
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "ត្រូវ Login"));
    }

    private LookupDocumentStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ស្ថានភាព", code));
    }

    private void triggerNotification(
            Approval approval,
            String statusCode) {

        Officer officer =
                approval.getRequestedBy();
        if (officer == null) return;

        boolean approved =
                DocumentStatusCode
                        .isApproved(statusCode);

        String docName =
                approval.getDocument()
                        .getDocumentName();

        String approverName =
                approval.getApprovedBy() != null
                        ? approval.getApprovedBy()
                          .getUserNameKh()
                        : "Admin";

        String title = approved
                ? "ឯកសារបានអនុម័ត ✓"
                : "ឯកសារបានបដិសេធ ✗";

        String message = approved
                ? "ឯកសារ \""
                  + docName
                  + "\" បានអនុម័តដោយ "
                  + approverName
                : "ឯកសារ \""
                  + docName
                  + "\" បានបដិសេធ"
                  + (approval.getComment() != null
                     ? " — "
                       + approval.getComment()
                     : "");

        notificationService.createByOfficerId(
                officer.getOfficerId(),
                title, message,
                NotificationType.DOCUMENT,
                approval.getDocument()
                        .getDocumentId());
    }

    private ActivityLogContext buildContext() {
        try {
            var req =
                    ((ServletRequestAttributes)
                            RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();
            return securityUtils
                    .buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext.builder()
                    .build();
        }
    }
}