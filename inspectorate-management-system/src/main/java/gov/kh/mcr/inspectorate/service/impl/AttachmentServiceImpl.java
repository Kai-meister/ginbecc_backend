package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.entity.Attachment;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.AttachmentMapper;
import gov.kh.mcr.inspectorate.repository.AttachmentRepository;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import gov.kh.mcr.inspectorate.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentServiceImpl
        implements AttachmentService {

    private static final Set<String> ALLOWED_MIME =
            Set.of(
                    "image/jpeg", "image/png",
                    "image/gif",  "image/webp",
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-"
                            + "officedocument"
                            + ".wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-"
                            + "officedocument"
                            + ".spreadsheetml.sheet",
                    "text/plain", "text/csv");

    private static final long MAX_SIZE =
            10L * 1024 * 1024;

    private final AttachmentRepository attachmentRepository;
    private final MinioService minioService;
    private final AttachmentMapper attachmentMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;

    @Override
    public AttachmentResponse upload(
            MultipartFile file,
            String refTypeCode,
            Integer refId) {

        AttachmentRefType refType =
                parseRefType(refTypeCode);

        validateFile(file);

        archiveExisting(refId, refType.getCode());

        String filePath = minioService.uploadFile(
                file,
                refType.toPathPrefix(),
                refId);

        Attachment attachment = Attachment.builder()
                .filePath(filePath)
                .referenceId(refId)
                .referenceType(refType.getCode())
                .originalName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileType(getExtension(Objects.requireNonNull(
                        file.getOriginalFilename())))
                .isActive(true)
                .build();

        securityUtils.getCurrentUser()
                .ifPresent(attachment::setUploadedBy);

        Attachment saved =
                attachmentRepository.save(attachment);

        activityLogService.log(
                "CREATE", "Attachment",
                saved.getAttachmentId(),
                "Upload: " + saved.getOriginalName()
                        + " [" + refType.getLabelKh()
                        + "/" + refId + "]");

        log.info("Uploaded: {} → {}",
                saved.getOriginalName(), filePath);

        return attachmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getByReference(
            Integer refId, String refTypeCode) {

        AttachmentRefType refType =
                parseRefType(refTypeCode);

        return attachmentRepository
                .findByReferenceIdAndReferenceType(
                        refId, refType.getCode())
                .stream()
                .map(attachmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse getActiveByReference(
            Integer refId, String refTypeCode) {

        AttachmentRefType refType =
                parseRefType(refTypeCode);

        return attachmentRepository
                .findByReferenceIdAndReferenceTypeAndIsActiveTrue(
                        refId, refType.getCode())
                .map(attachmentMapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ឯកសារ Active ["
                                        + refType.getLabelKh()
                                        + "/" + refId + "]", ""));
    }

    @Override
    @Transactional(readOnly = true)
    public String getDownloadUrl(Integer id) {
        Attachment a = findById(id);
        return minioService.getPresignedUrl(
                a.getFilePath());
    }

    @Override
    public AttachmentResponse setActive(Integer id) {
        Attachment target = findById(id);

        AttachmentRefType refType =
                parseRefType(target.getReferenceType());

        archiveExisting(
                target.getReferenceId(),
                refType.getCode());

        target.setIsActive(true);
        Attachment saved =
                attachmentRepository.save(target);

        activityLogService.log(
                "UPDATE", "Attachment", id,
                "Set Active: "
                        + saved.getOriginalName()
                        + " [" + refType.getLabelKh() + "]");

        return attachmentMapper.toResponse(saved);
    }

    @Override
    public void delete(Integer id) {
        Attachment a = findById(id);

        AttachmentRefType refType =
                parseRefType(a.getReferenceType());

        try {
            minioService.deleteFile(a.getFilePath());
        } catch (Exception ex) {
            log.warn("MinIO delete failed: {}",
                    ex.getMessage());
        }

        attachmentRepository.deleteById(id);

        activityLogService.log(
                "DELETE", "Attachment", id,
                "លុប: " + a.getOriginalName()
                        + " [" + refType.getLabelKh() + "]");
    }

    @Override
    public int cleanupArchived(
            Integer refId, String refTypeCode) {

        AttachmentRefType refType =
                parseRefType(refTypeCode);

        List<Attachment> archived =
                attachmentRepository
                        .findByReferenceIdAndReferenceTypeAndIsActive(
                                refId, refType.getCode(), false);

        archived.forEach(a -> {
            try {
                minioService.deleteFile(
                        a.getFilePath());
            } catch (Exception ex) {
                log.warn("MinIO delete failed: {}",
                        ex.getMessage());
            }
        });

        int count =
                attachmentRepository
                        .deleteArchivedByReference(
                                refId, refType.getCode());

        log.info(
                "Cleaned {} archived [{}={}]",
                count,
                refType.getLabelKh(),
                refId);

        return count;
    }

    @Override
    public String getFileSizeDisplay(Long bytes) {
        return attachmentMapper.formatFileSize(bytes);
    }

    // ── Private Helpers ───────────────────────────
    private AttachmentRefType parseRefType(
            String code) {
        try {
            return AttachmentRefType.fromCode(code);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(
                    "ref_type មិនត្រឹមត្រូវ: ["
                            + code + "]");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    "File ទទេ");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(
                    "File ធំពេក: "
                            + attachmentMapper.formatFileSize(
                            file.getSize())
                            + " / អតិបរមា 10 MB");
        }
        String mime = file.getContentType();
        if (mime == null
                || !ALLOWED_MIME.contains(
                mime.toLowerCase())) {
            throw new BusinessException(
                    "ប្រភេទ File មិនអនុញ្ញាត: "
                            + mime);
        }
        if (!StringUtils.hasText(
                file.getOriginalFilename())) {
            throw new BusinessException(
                    "ឈ្មោះ File ខ្វះ");
        }
    }

    private void archiveExisting(
            Integer refId, String refTypeCode) {
        attachmentRepository
                .findByReferenceIdAndReferenceTypeAndIsActiveTrue(
                        refId, refTypeCode)
                .ifPresent(old -> {
                    old.setIsActive(false);
                    attachmentRepository.save(old);
                    log.debug("Archived: {}",
                            old.getOriginalName());
                });
    }

    private Attachment findById(Integer id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ឯកសារ", id));
    }

    private String getExtension(String filename) {
        int i = filename.lastIndexOf('.');
        return i >= 0
                ? filename.substring(i + 1).toLowerCase()
                : "unknown";
    }
}