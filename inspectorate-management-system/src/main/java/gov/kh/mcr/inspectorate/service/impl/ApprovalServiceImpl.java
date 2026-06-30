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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalServiceImpl
        implements ApprovalService {

    private final ApprovalRepository
            approvalRepo;
    private final DocumentRepository
            documentRepo;
    private final DepartmentManagerRepository
            deptManagerRepo;
    private final LookupDocumentStatusRepository
            statusRepo;
    private final ApprovalMapper
            approvalMapper;
    private final SecurityUtils
            securityUtils;
    private final NotificationService
            notificationService;
    private final ActivityLogService
            activityLogService;

    @Override
    public void autoCreateApproval(
            Document document) {

        boolean hasPending =
                approvalRepo
                        .existsByDocument_DocumentIdAndStatusCode_StatusCode(
                                document.getDocumentId(),
                                DocumentStatusCode
                                        .PENDING.getCode());

        if (hasPending) {
            log.warn(
                    "Approval already PENDING"
                            + " for doc={}",
                    document.getDocumentId());
            return;
        }

        LookupDocumentStatus pending =
                findStatus(
                        DocumentStatusCode.PENDING
                                .getCode());

        Department dept =
                resolveDepartment(
                        document.getUser());

        Approval approval = Approval.builder()
                .document(document)
                 .department(dept)
                .statusCode(pending)
                .requestedAt(
                        LocalDateTime.now())
                .build();

        Approval saved =
                approvalRepo.save(approval);

        activityLogService.log(
                "CREATE", "Approval",
                saved.getApprovalId(),
                "ស្នើស្វ័យប្រវត្តិ: "
                        + document.getDocumentName(),
                buildContext());
        notifyAllManagers(dept, document);
    }

    private Department resolveDepartment(
            User u) {

        if (u == null) return null;

        if (u.getOfficer() != null
                && u.getOfficer()
                .getDepartment() != null) {
            return u.getOfficer()
                    .getDepartment();
        }

        if (u.getContractOfficer() != null
                && u.getContractOfficer()
                .getDepartment() != null) {
            return u.getContractOfficer()
                    .getDepartment();
        }

        return null;
    }

    @Override
    public ApprovalResponse requestApproval(
            ApprovalRequest request) {

        Document document =
                documentRepo.findById(
                                request.getDocumentId())
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "ឯកសារ",
                                        request
                                                .getDocumentId()));

        validateDocumentStatus(document);

        User currentUser =
                securityUtils.getCurrentUser()
                        .orElseThrow(() ->
                                new
                                        UnauthorizedException(
                                        "សូមចូលប្រើប្រាស់ប្រព័ន្ធ (Login) ជាមុនសិន ដើម្បីមានសិទ្ធិអនុវត្តមុខងារនេះ។"));

        if (document.getUser() == null
                || !document.getUser()
                .getUserId()
                .equals(currentUser
                        .getUserId())) {
            throw new BusinessException(
                    "លោកអ្នកមិនអាចស្នើសុំការអនុម័តលើឯកសារនេះបានឡើយ ព្រោះលោកអ្នកមិនមែនជាម្ចាស់ នៃឯកសារនេះទេ។");
        }


        checkNoDuplicatePending(
                request.getDocumentId());

        Department dept =
                resolveDepartment(
                        document.getUser());

        if (dept == null) {
            throw new BusinessException(
                    "គណនីមន្ត្រី «" + currentUser.getUserNameKh()
                            + "» មិនទាន់ត្រូវបានភ្ជាប់ទៅនឹងនាយកដ្ឋាន ឬអង្គភាពណាមួយនៅក្នុងប្រព័ន្ធឡើយ។");
        }

        LookupDocumentStatus pending =
                findStatus(
                        DocumentStatusCode.PENDING
                                .getCode());

        Approval approval = Approval.builder()
                .document(document)
                .department(dept)
                .statusCode(pending)
                .comment(request.getNote())
                .requestedAt(
                        LocalDateTime.now())
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

        notifyAllManagers(dept, document);

        return approvalMapper.toResponse(
                saved);
    }

    private void checkNoDuplicatePending(
            Integer documentId) {

        boolean hasPending =
                approvalRepo
                        .existsByDocument_DocumentIdAndStatusCode_StatusCode(
                                documentId,
                                DocumentStatusCode
                                        .PENDING.getCode());

        if (hasPending) {
            throw new BusinessException(
                    "មិនអាចបញ្ជូនឯកសារនេះបានឡើយ ព្រោះឯកសារនេះកំពុងស្ថិតក្នុង «ដំណើរការរង់ចាំការអនុម័ត» រួចរាល់ហើយ។");
        }
    }

    private void
    validateIsDepartmentManager(
            Approval approval,
            User approver) {

        if (securityUtils
                .canBypassDepartmentScope()) {
            return;
        }

        Department dept =
                approval.getDepartment();
        if (dept == null) {
            throw new
                    PermissionDeniedException(
                    "ពិនិត្យ និងសម្រេចលើការស្នើសុំនេះ",
                    "មិនអាចដំណើរការបានឡើយ ព្រោះទិន្នន័យនាយកដ្ឋាន ឬអង្គភាពមិនទាន់ត្រូវបានកំណត់។ "
                            + "សូមទាក់ទងទៅកាន់អ្នកគ្រប់គ្រងប្រព័ន្ធ (SUPER_ADMIN/ADMIN) ដើម្បីពិនិត្យមើលគណនីរបស់ម្ចាស់ឯកសារឡើងវិញ។");
        }

        boolean isManager =
                deptManagerRepo
                        .existsByDepartment_DepartmentIdAndUser_UserId(
                                dept.getDepartmentId(),
                                approver.getUserId());

        if (!isManager) {
            throw new
                    DepartmentScopeException(
                    resolveCurrentUserDeptName(
                            approver),
                    dept.getDepartmentName());
        }
    }
    private String
    resolveCurrentUserDeptName(
            User u) {

        if (u.getOfficer() != null
                && u.getOfficer()
                .getDepartment() != null) {
            return u.getOfficer()
                    .getDepartment()
                    .getDepartmentName();
        }

        if (u.getContractOfficer() != null
                && u.getContractOfficer()
                .getDepartment()
                != null) {
            return u.getContractOfficer()
                    .getDepartment()
                    .getDepartmentName();
        }

        return "គ្មាន Department";
    }
    private void notifyAllManagers(
            Department dept,
            Document document) {

        if (dept == null) {
            log.warn(
                    "No department resolved"
                            + " for doc={} — cannot"
                            + " notify managers",
                    document.getDocumentId());
            return;
        }

        List<DepartmentManager> managers =
                deptManagerRepo
                        .findByDepartment_DepartmentId(
                                dept.getDepartmentId());

        if (managers.isEmpty()) {
            log.warn(
                    "Department {} has no"
                            + " Manager assigned",
                    dept.getDepartmentName());
            return;
        }

        for (DepartmentManager dm :
                managers) {
            notificationService
                    .createByUserId(
                            dm.getUser()
                                    .getUserId(),
                            "មានសំណើអនុម័តថ្មី",
                            "ឯកសារ \""
                                    + document
                                    .getDocumentName()
                                    + "\" ត្រូវការ"
                                    + " ការអនុម័តរបស់អ្នក",
                            NotificationType
                                    .DOCUMENT,
                            document
                                    .getDocumentId());
        }
    }

    @Override
    public ApprovalResponse decide(
            Integer approvalId,
            DecideRequest request) {

        request.validate();

        Approval approval =
                approvalRepo.findById(
                                approvalId)
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "ការស្នើសុំអនុម័ត",
                                        approvalId));

        validateApprovalPending(approval);

        User approver =
                securityUtils.getCurrentUser()
                        .orElseThrow(() ->
                                new
                                        UnauthorizedException(
                                        "សូមចូលប្រើប្រាស់ប្រព័ន្ធ (Login) ជាមុនសិន ដើម្បីមានសិទ្ធិអនុវត្តមុខងារនេះ។"));

        validateIsDepartmentManager(
                approval, approver);

        validateNotSelfApproval(
                approval, approver);

        LookupDocumentStatus newStatus =
                findStatus(
                        request.getStatusCode());

        approval.setStatusCode(newStatus);
        approval.setApprovedBy(approver);
        approval.setComment(
                request.getComment());
        approval.setDecidedAt(
                LocalDateTime.now());

        Document doc =
                approval.getDocument();
        doc.setStatusCode(newStatus);

        Approval saved =
                approvalRepo.save(approval);

        triggerNotification(
                approval,
                request.getStatusCode());

        activityLogService.log(
                "UPDATE", "Approval",
                approvalId,
                request.getStatusCode()
                        + ": "
                        + doc.getDocumentName(),
                buildContext());

        return approvalMapper.toResponse(
                saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApprovalResponse>
    getAll(int page, int size,
           String status,
           Integer officerId,
           Integer documentId) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("requestedAt")
                        .descending());

         boolean canSeeAll =
                securityUtils
                        .canBypassDepartmentScope();

        Page<Approval> result;

        if (documentId != null) {
            result = approvalRepo
                    .findByDocument_DocumentId(
                            documentId, pageable);
        } else if (canSeeAll) {
            result = status != null
                    ? approvalRepo
                    .findByStatusCode_StatusCode(
                            status, pageable)
                    : approvalRepo
                    .findAll(pageable);
        } else {
            Integer currentUserId =
                    securityUtils
                            .getCurrentUserId();

            result = status != null
                    ? approvalRepo
                    .findByStatusCodeAndVisibleToUser(
                            status,
                            currentUserId,
                            pageable)
                    : approvalRepo
                    .findVisibleToUser(
                            currentUserId,
                            pageable);
        }

        return PageResponse.of(
                result.map(
                        approvalMapper::toResponse));
    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApprovalResponse>
    getMyPending(int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("requestedAt")
                        .ascending());

        if (securityUtils
                .canBypassDepartmentScope()) {
            return PageResponse.of(
                    approvalRepo
                            .findByStatusCode_StatusCode(
                                    DocumentStatusCode
                                            .PENDING.getCode(),
                                    pageable)
                            .map(
                                    approvalMapper
                                            ::toResponse));
        }

        Integer currentUserId =
                securityUtils
                        .getCurrentUserId();

        List<Integer> managedDeptIds =
                deptManagerRepo
                        .findByUser_UserId(
                                currentUserId)
                        .stream()
                        .map(dm ->
                                dm.getDepartment()
                                        .getDepartmentId())
                        .toList();

        if (managedDeptIds.isEmpty()) {
            return PageResponse
                    .<ApprovalResponse>builder()
                    .content(List.of())
                    .pageNumber(page)
                    .pageSize(size)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .build();
        }

        return PageResponse.of(
                approvalRepo
                        .findByStatusCode_StatusCodeAndDepartment_DepartmentIdIn(
                                DocumentStatusCode
                                        .PENDING.getCode(),
                                managedDeptIds,
                                pageable)
                        .map(
                                approvalMapper
                                        ::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalResponse getById(
            Integer id) {

        Approval approval =
                approvalRepo.findById(id)
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "ការស្នើសុំអនុម័ត", id));

        if (!securityUtils
                .canBypassDepartmentScope()) {

            Integer currentUserId =
                    securityUtils
                            .getCurrentUserId();

            boolean isRequester =
                    approval.getDocument()
                            .getUser() != null
                            && approval.getDocument()
                            .getUser()
                            .getUserId()
                            .equals(currentUserId);

            boolean isDeptManager =
                    approval.getDepartment()
                            != null
                            && deptManagerRepo
                            .existsByDepartment_DepartmentIdAndUser_UserId(
                                    approval
                                            .getDepartment()
                                            .getDepartmentId(),
                                    currentUserId);

            boolean isApprover =
                    approval.getApprovedBy()
                            != null
                            && approval.getApprovedBy()
                            .getUserId()
                            .equals(currentUserId);

            if (!isRequester
                    && !isDeptManager
                    && !isApprover) {
                throw new
                        ResourceNotFoundException(
                        "ការស្នើសុំអនុម័ត", id);
            }
        }

        return approvalMapper.toResponse(
                approval);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public PageResponse<ApprovalResponse>
//    getMyPending(int page, int size) {
//
//        Pageable pageable = PageRequest.of(
//                page, size,
//                Sort.by("requestedAt")
//                        .ascending());
//
//        Integer currentUserId =
//                securityUtils
//                        .getCurrentUserId();
//
//        // Fix — Get all Departments this
//        // user manages
//        List<Integer> managedDeptIds =
//                deptManagerRepo
//                        .findByUser_UserId(
//                                currentUserId)
//                        .stream()
//                        .map(dm ->
//                                dm.getDepartment()
//                                        .getDepartmentId())
//                        .toList();
//
//        // Fix — LOG for debugging
//        log.info(
//                "getMyPending: user={}"
//                        + " managedDeptIds={}",
//                currentUserId, managedDeptIds);
//
//        // Fix — Empty handling — REMOVED
//        // broken Page.empty().map(null)
//        // pattern, use PageResponse
//        // builder directly for empty
//        if (managedDeptIds.isEmpty()) {
//            log.warn(
//                    "User {} is not assigned"
//                            + " as Manager to ANY"
//                            + " department — check"
//                            + " DepartmentManager"
//                            + " table",
//                    currentUserId);
//
//            return PageResponse
//                    .<ApprovalResponse>builder()
//                    .content(List.of())
//                    .pageNumber(page)
//                    .pageSize(size)
//                    .totalElements(0)
//                    .totalPages(0)
//                    .first(true)
//                    .last(true)
//                    .build();
//        }
//
//        Page<Approval> result =
//                approvalRepo
//                        .findByStatusCode_StatusCodeAndDepartment_DepartmentIdIn(
//                                DocumentStatusCode
//                                        .PENDING.getCode(),
//                                managedDeptIds,
//                                pageable);
//
//        log.info(
//                "getMyPending: found {}"
//                        + " approvals for"
//                        + " managedDeptIds={}",
//                result.getTotalElements(),
//                managedDeptIds);
//
//        return PageResponse.of(
//                result.map(
//                        approvalMapper::toResponse));
//    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApprovalResponse>
    getMyRequests(
            int page, int size,
            String status) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("requestedAt")
                        .descending());

        Integer currentUserId =
                securityUtils
                        .getCurrentUserId();

        Page<Approval> result =
                status != null
                        ? approvalRepo
                        .findByStatusCode_StatusCodeAndDocument_User_UserId(
                                status,
                                currentUserId,
                                pageable)
                        : approvalRepo
                        .findByDocument_User_UserId(
                                currentUserId,
                                pageable);

        return PageResponse.of(
                result.map(
                        approvalMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApprovalResponse>
    getMyDecided(
            int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("decidedAt")
                        .descending());

        Integer currentUserId =
                securityUtils
                        .getCurrentUserId();

        return PageResponse.of(
                approvalRepo
                        .findByApprovedBy_UserId(
                                currentUserId,
                                pageable)
                        .map(
                                approvalMapper
                                        ::toResponse));
    }


    private void validateDocumentStatus(
            Document document) {

        String code =
                document.getStatusCode()
                        != null
                        ? document.getStatusCode()
                        .getStatusCode()
                        : "";

        if (!DocumentStatusCode.isDraft(code)) {

            String currentStatusKh = "PENDING".equals(code) ? "«កំពុងរង់ចាំការអនុម័ត»"
                    : "APPROVED".equals(code) ? "«ត្រូវបានអនុម័តរួចរាល់»"
                      : "«" + code + "»";

            throw new BusinessException(
                    "មិនអាចស្នើសុំការអនុម័តបានឡើយ ព្រោះឯកសារនេះត្រូវតែស្ថិតក្នុងស្ថានភាព «ព្រាង (Draft)»។ "
                            + "ស្ថានភាពបច្ចុប្បន្ន៖ " + currentStatusKh
            );
        }
    }

    private void validateApprovalPending(
            Approval approval) {

        String code =
                approval.getStatusCode()
                        != null
                        ? approval.getStatusCode()
                        .getStatusCode()
                        : "";

        if (!DocumentStatusCode
                .isPending(code)) {
            throw new BusinessException(
                    "មិនអាចធ្វើការសម្រេចបានឡើយ ព្រោះស្ថានភាពសំណើ "
                            + "មិនស្ថិតក្នុងដំណាក់កាល «រង់ចាំការពិនិត្យ/អនុម័ត» ឡើយ (ស្ថានភាពបច្ចុប្បន្ន: " + code + ")");
        }
    }

    private void validateNotSelfApproval(
            Approval approval,
            User approver) {

        User docUser =
                approval.getDocument()
                        .getUser();

        if (docUser != null
                && docUser.getUserId()
                .equals(approver
                        .getUserId())) {
            throw new BusinessException(
                    "លោកអ្នកមិនអាចធ្វើការពិនិត្យសម្រេច ឬអនុម័តលើឯកសារ/សំណើរបស់ខ្លួនឯងបានឡើយ");
        }
    }

    private LookupDocumentStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new
                                ResourceNotFoundException(
                                "ស្ថានភាព", code));
    }


    private void triggerNotification(
            Approval approval,
            String statusCode) {

        User docUser =
                approval.getDocument()
                        .getUser();
        if (docUser == null) return;

        boolean approved =
                DocumentStatusCode
                        .isApproved(statusCode);

        String docName =
                approval.getDocument()
                        .getDocumentName();

        String approverName =
                approval.getApprovedBy()
                        != null
                        ? approval.getApprovedBy()
                        .getUserNameKh()
                        : "Manager";

        String title = approved
                ? "ឯកសារបានអនុម័ត "
                : "ឯកសារបានបដិសេធ ";

        String message = approved
                ? "ឯកសារ \"" + docName
                  + "\" បានអនុម័តដោយ "
                  + approverName
                : "ឯកសារ \"" + docName
                  + "\" បានបដិសេធ"
                  + (approval.getComment()
                != null
                     ? " — "
                       + approval.getComment()
                     : "");

        notificationService.createByUserId(
                docUser.getUserId(),
                title, message,
                NotificationType.DOCUMENT,
                approval.getDocument()
                        .getDocumentId());
    }

    private ActivityLogContext
    buildContext() {
        try {
            var req =
                    ((ServletRequestAttributes)
                            RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();
            return securityUtils
                    .buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext
                    .builder().build();
        }
    }
}