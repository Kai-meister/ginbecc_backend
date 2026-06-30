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
            10L * 1024 * 1024; // 10MB

    private final AttachmentRepository attachmentRepo;
    private final MinioService minioService;
    private final AttachmentMapper attachmentMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;

    @Override
    public AttachmentResponse upload(
            MultipartFile file,
            AttachmentRefType refType,
            Integer refId) {

        // ១. Validate file
        validateFile(file);

        // ២. Archive existing active
        archiveExisting(refId, refType);

        // ៣. Upload to MinIO
         String filePath = minioService.uploadFile(
                file,
                refType.toPathPrefix(),
                refId);

        // ៤. Build + Save entity
        Attachment attachment = Attachment.builder()
                .filePath(filePath)
                .referenceId(refId)
                .referenceType(refType)
                .originalName(
                        file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileType(getExtension(
                        Objects.requireNonNull(
                                file.getOriginalFilename())))
                .isActive(true)
                .build();

        securityUtils.getCurrentUser()
                .ifPresent(attachment::setUploadedBy);

        Attachment saved =
                attachmentRepo.save(attachment);

        activityLogService.log(
                "CREATE", "Attachment",
                saved.getAttachmentId(),
                "Upload: " + saved.getOriginalName()
                        + " [" + refType.getLabelKh()
                        + "/" + refId + "]");

        log.info("Uploaded: {} → [{}]",
                saved.getOriginalName(),
                refType.getCode());

        return attachmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getByReference(
            Integer refId,
            AttachmentRefType refType) {

        return attachmentRepo
                .findByReferenceIdAndReferenceType(
                        refId, refType)
                .stream()
                .map(attachmentMapper::toResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse getActiveByReference(
            Integer refId,
            AttachmentRefType refType) {

        return attachmentRepo
                .findByReferenceIdAndReferenceTypeAndIsActiveTrue(
                        refId, refType)
                .map(attachmentMapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active File ["
                                        + refType.getLabelKh()
                                        + "/" + refId + "]", ""));
    }

    @Override
    @Transactional(readOnly = true)
    public String getDownloadUrl(Integer id) {
        return minioService.getPresignedUrl(
                findById(id).getFilePath());
    }

    @Override
    public AttachmentResponse setActive(Integer id) {
        Attachment target = findById(id);

        archiveExisting(
                target.getReferenceId(),
                target.getReferenceType());

        target.setIsActive(true);
        Attachment saved =
                attachmentRepo.save(target);

        activityLogService.log(
                "UPDATE", "Attachment", id,
                "Set Active: "
                        + saved.getOriginalName()
                        + " ["
                        + saved.getReferenceType()
                        .getLabelKh() + "]");

        return attachmentMapper.toResponse(saved);
    }

    @Override
    public void delete(Integer id) {
        Attachment a = findById(id);

        try {
            minioService.deleteFile(a.getFilePath());
        } catch (Exception ex) {
            log.warn("MinIO delete failed: {}",
                    ex.getMessage());
        }

        attachmentRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "Attachment", id,
                "លុប: " + a.getOriginalName()
                        + " ["
                        + a.getReferenceType().getLabelKh()
                        + "]");
    }

    @Override
    public int cleanupArchived(
            Integer refId,
            AttachmentRefType refType) {

        List<Attachment> archived =
                attachmentRepo
                        .findByReferenceIdAndReferenceTypeAndIsActive(
                                refId, refType, false);

        archived.forEach(a -> {
            try {
                minioService.deleteFile(
                        a.getFilePath());
            } catch (Exception ex) {
                log.warn(
                        "MinIO delete failed: {}",
                        ex.getMessage());
            }
        });

        int count = attachmentRepo
                .deleteArchivedByReference(
                        refId, refType);

        log.info(
                "Cleaned {} archived [{}={}]",
                count,
                refType.getLabelKh(), refId);

        return count;
    }

    @Override
    public String getFileSizeDisplay(Long bytes) {
        return attachmentMapper.formatFileSize(bytes);
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
                            + " / អតិបរមា 10MB");
        }
        String mime = file.getContentType();
        if (mime == null
                || !ALLOWED_MIME.contains(
                mime.toLowerCase())) {
            throw new BusinessException(
                    "ប្រភេទ File មិនអនុញ្ញាត: " + mime);
        }
        if (!StringUtils.hasText(
                file.getOriginalFilename())) {
            throw new BusinessException(
                    "ឈ្មោះ File ខ្វះ");
        }
    }
    private void archiveExisting(
            Integer refId,
            AttachmentRefType refType) {
        attachmentRepo
                .findByReferenceIdAndReferenceTypeAndIsActiveTrue(
                        refId, refType)
                .ifPresent(old -> {
                    old.setIsActive(false);
                    attachmentRepo.save(old);
                    log.debug("Archived: {}",
                            old.getOriginalName());
                });
    }

    private Attachment findById(Integer id) {
        return attachmentRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ឯកសារ", id));
    }

    private String getExtension(String filename) {
        int i = filename.lastIndexOf('.');
        return i >= 0
                ? filename.substring(i + 1)
                  .toLowerCase()
                : "unknown";
    }
}