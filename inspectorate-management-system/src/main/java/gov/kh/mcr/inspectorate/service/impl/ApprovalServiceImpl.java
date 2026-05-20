package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.ApprovalDecisionRequest;
import gov.kh.mcr.inspectorate.dto.request.ApprovalRequest;
import gov.kh.mcr.inspectorate.dto.response.ApprovalResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.Approval;
import gov.kh.mcr.inspectorate.entity.Document;
import gov.kh.mcr.inspectorate.entity.LookupDocumentStatus;
import gov.kh.mcr.inspectorate.enums.DocumentStatusCode;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.ApprovalMapper;
import gov.kh.mcr.inspectorate.repository.ApprovalRepository;
import gov.kh.mcr.inspectorate.repository.DocumentRepository;
import gov.kh.mcr.inspectorate.repository.LookupDocumentStatusRepository;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.ApprovalService;
import gov.kh.mcr.inspectorate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final DocumentRepository documentRepository;
    private final LookupDocumentStatusRepository lookupStatusRepository;
    private final ApprovalMapper approvalMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApprovalResponse> getAll(
            int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("requestedAt").descending());
        Page<Approval> result = status != null
                ? approvalRepository
                  .findByStatusCode_StatusCode(status, pageable)
                : approvalRepository.findAll(pageable);
        return PageResponse.of(
                result.map(approvalMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalResponse getById(Integer id) {
        return approvalMapper.toResponse(findById(id));
    }

    @Override
    public ApprovalResponse requestApproval(
            ApprovalRequest request) {

        Document document = documentRepository
                .findById(request.getDocumentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ឯកសារ", request.getDocumentId()));

        if (!DocumentStatusCode.isDraft(
                document.getStatusCode() != null
                        ? document.getStatusCode().getStatusCode()
                        : "")) {
            throw new BusinessException(
                    "ឯកសារត្រូវស្ថានភាព DRAFT "
                            + "ទើបអាចស្នើអនុម័តបាន");
        }

        LookupDocumentStatus pending = lookupStatusRepository.findById(
                                DocumentStatusCode.PENDING.getCode())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "ស្ថានភាព",
                                        DocumentStatusCode.PENDING
                                                .getCode()));

        Approval approval = Approval.builder()
                .document(document)
                .requestedAt(LocalDateTime.now())
                .statusCode(pending)
                .build();

        securityUtils.getCurrentUser()
                .ifPresent(approval::setRequestedBy);

        document.setStatusCode(pending);
        documentRepository.save(document);

        Approval saved = approvalRepository.save(approval);

        activityLogService.log(
                "CREATE", "Approval",
                saved.getApprovalId(),
                "ស្នើ: " + document.getDocumentName());

        return approvalMapper.toResponse(saved);
    }

    @Override
    public ApprovalResponse decide(Integer id, ApprovalDecisionRequest request) {
        Approval approval = findById(id);

        LookupDocumentStatus newStatus = lookupStatusRepository
                        .findById(request.getStatusCode())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "ស្ថានភាព", request.getStatusCode()));

        securityUtils.getCurrentUser().ifPresent(approval::setApprovedBy);

        approval.setStatusCode(newStatus);
        approval.setComment(request.getComment());
        approval.setApprovedAt(LocalDateTime.now());

        Document doc = approval.getDocument();
        doc.setStatusCode(newStatus);
        documentRepository.save(doc);

        if (approval.getRequestedBy() != null
                && approval.getRequestedBy()
                .getOfficer() != null) {

            boolean approved = DocumentStatusCode
                    .isApproved(request.getStatusCode());

            notificationService.createByOfficerId(
                    approval.getRequestedBy()
                            .getOfficer()
                            .getOfficerId(),
                    "លទ្ធផលអនុម័ត",
                    (approved
                            ? "ឯកសារបានអនុម័ត: "
                            : "ឯកសារបានបដិសេធ: ")
                            + doc.getDocumentName(),
                    "DOCUMENT",
                    doc.getDocumentId(),
                    "DOCUMENT");}
        activityLogService.log("UPDATE", "Approval",
                id, "សម្រេច: " + request.getStatusCode());

        return approvalMapper.toResponse(
                approvalRepository.save(approval));
    }

    private Approval findById(Integer id) {
        return approvalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ការអនុម័ត", id));
    }
}