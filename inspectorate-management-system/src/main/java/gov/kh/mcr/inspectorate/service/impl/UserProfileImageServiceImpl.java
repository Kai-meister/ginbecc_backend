package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .AttachmentRefType;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;
import org.springframework.web.multipart
        .MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileImageServiceImpl
        implements UserProfileImageService {

    private final OfficerRepository
            officerRepo;
    private final ContractOfficerRepository
            contractRepo;
    private final UserRepository
            userRepo;
    private final AttachmentRepository
            attachmentRepo;
    private final AttachmentService
            attachmentService;
    private final MinioService
            minioService;

    @Override
    public String uploadOfficerProfileImage(
            Integer officerId,
            MultipartFile file) {

        Officer officer =
                officerRepo.findById(officerId)
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "មន្ត្រី", officerId));

        validateImageFile(file);

        // Delete old image first
        if (officer.getProfileAttachment()
                != null) {
            deleteOldAttachment(
                    officer
                            .getProfileAttachment()
                            .getAttachmentId());
        }

        var uploaded =
                attachmentService.upload(
                        file,
                        AttachmentRefType
                                .OFFICER,
                        officerId);

        Attachment attachment =
                attachmentRepo.findById(
                                uploaded
                                        .getAttachmentId())
                        .orElseThrow();

        officer.setProfileAttachment(
                attachment);
        officerRepo.save(officer);

        log.info(
                "Officer {} profile image"
                        + " uploaded: {}",
                officerId,
                attachment.getFilePath());

        return minioService
                .getPresignedUrl(
                        attachment.getFilePath());
    }

    @Override
    public String
    uploadContractOfficerProfileImage(
            Integer contractOfficerId,
            MultipartFile file) {

        ContractOfficer co =
                contractRepo.findById(
                                contractOfficerId)
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "មន្ត្រីកិច្ចសន្យា",
                                        contractOfficerId));

        validateImageFile(file);

        // Delete old image first
        if (co.getProfileAttachment()
                != null) {
            deleteOldAttachment(
                    co.getProfileAttachment()
                            .getAttachmentId());
        }

        var uploaded =
                attachmentService.upload(
                        file,
                        AttachmentRefType
                                .CONTRACT_OFFICER_PROFILE,
                        contractOfficerId);

        Attachment attachment =
                attachmentRepo.findById(
                                uploaded
                                        .getAttachmentId())
                        .orElseThrow();

        co.setProfileAttachment(attachment);
        contractRepo.save(co);

        log.info(
                "ContractOfficer {} profile"
                        + " image uploaded: {}",
                contractOfficerId,
                attachment.getFilePath());

        return minioService
                .getPresignedUrl(
                        attachment.getFilePath());
    }

    @Override
    @Transactional(readOnly = true)
    public String getProfileImageUrl(
            Integer userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User", userId));

        if (user.getOfficer() != null
                && user.getOfficer()
                .getProfileAttachment()
                != null) {

            String path = user.getOfficer()
                    .getProfileAttachment()
                    .getFilePath();

            log.debug(
                    "User {} profile: "
                            + "from Officer {}",
                    userId,
                    user.getOfficer()
                            .getOfficerId());

            return minioService
                    .getPresignedUrl(path);
        }

        if (user.getContractOfficer()
                != null
                && user.getContractOfficer()
                .getProfileAttachment()
                != null) {

            String path =
                    user.getContractOfficer()
                            .getProfileAttachment()
                            .getFilePath();

            log.debug(
                    "User {} profile: from"
                            + " ContractOfficer {}",
                    userId,
                    user.getContractOfficer()
                            .getContractOfficerId());

            return minioService
                    .getPresignedUrl(path);
        }


        return resolveDefaultProfileUrl(
                user);
    }

    @Override
    public void deleteOfficerProfileImage(
            Integer officerId) {

        Officer officer =
                officerRepo.findById(officerId)
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "មន្ត្រី", officerId));

        if (officer.getProfileAttachment()
                == null) {
            throw new BusinessException(
                    "មន្ត្រីរូបនេះមិនមានរូបភាពប្រវត្តិរូបឡើយ។");
        }

        Integer attId = officer
                .getProfileAttachment()
                .getAttachmentId();

        officer.setProfileAttachment(null);
        officerRepo.save(officer);

        deleteOldAttachment(attId);

        log.info(
                "Officer {} profile image"
                        + " deleted", officerId);
    }

    @Override
    public void
    deleteContractOfficerProfileImage(
            Integer contractOfficerId) {

        ContractOfficer co =
                contractRepo.findById(
                                contractOfficerId)
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "មន្ត្រីកិច្ចសន្យា",
                                        contractOfficerId));

        if (co.getProfileAttachment()
                == null) {
            throw new BusinessException(
                    "មន្ត្រីកិច្ចសន្យារូបនេះមិនមានរូបភាពប្រវត្តិរូបឡើយ។");
        }

        Integer attId = co
                .getProfileAttachment()
                .getAttachmentId();

        co.setProfileAttachment(null);
        contractRepo.save(co);

        deleteOldAttachment(attId);

        log.info(
                "ContractOfficer {} profile"
                        + " image deleted",
                contractOfficerId);
    }

    private void validateImageFile(
            MultipartFile file) {

        if (file == null
                || file.isEmpty()) {
            throw new BusinessException(
                    "ឯកសារចាំបាច់ត្រូវតែមាន — សូមជ្រើសរើស និងបង្ហោះរូបភាពឡើងវិញ។");
        }

        String contentType =
                file.getContentType();

        if (contentType == null
                || (!contentType.startsWith(
                "image/jpeg")
                && !contentType.startsWith(
                "image/png")
                && !contentType.startsWith(
                "image/jpg")
                && !contentType.startsWith(
                "image/webp"))) {
            throw new BusinessException(
                    "ប្រភេទឯកសាររូបភាពមិនត្រឹមត្រូវឡើយ។ ប្រព័ន្ធអនុញ្ញាតឱ្យបង្ហោះតែឯកសារដែលមានទម្រង់ជា .jpg, .jpeg, .png ឬ .webp ប៉ុណ្ណោះ (ទម្រង់បច្ចុប្បន្ន៖ " + contentType + ")។)");
        }

        // Max 5MB
        long maxSize = 5L * 1024 * 1024;
        if (file.getSize() > maxSize) {
            long currentMb = file.getSize() / 1024 / 1024;
            throw new BusinessException(
                    "ទំហំឯកសារធំលើសការកំណត់។ ប្រព័ន្ធអនុញ្ញាតឱ្យបង្ហោះឯកសារដែលមានទំហំអតិបរមា 5MB ប៉ុណ្ណោះ (ទំហំបច្ចុប្បន្ន៖ " + currentMb + "MB)។"
            );
        }
    }

    private void deleteOldAttachment(
            Integer attachmentId) {
        try {
            attachmentService.delete(
                    attachmentId);
        } catch (Exception e) {
            log.warn(
                    "Failed to delete old"
                            + " attachment {}: {}",
                    attachmentId,
                    e.getMessage());
        }
    }

    private String resolveDefaultProfileUrl(
            User user) {

        String roleName =
                user.getRole() != null
                        ? user.getRole().getRoleName()
                        : "UNKNOWN";

        // or Static resources)
        return switch (roleName) {
            case "SUPER_ADMIN" ->
                    "/api/v1/static/avatars"
                            + "/super_admin.png";
            case "ADMIN" ->
                    "/api/v1/static/avatars"
                            + "/admin.png";
            case "MANAGER" ->
                    "/api/v1/static/avatars"
                            + "/manager.png";
            case "OFFICER" ->
                    "/api/v1/static/avatars"
                            + "/officer.png";
            case "CONTRACT_OFFICER" ->
                    "/api/v1/static/avatars"
                            + "/contract_officer.png";
            default ->
                    "/api/v1/static/avatars"
                            + "/default.png";
        };
    }
}