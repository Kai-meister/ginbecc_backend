package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileImageServiceImpl
        implements UserProfileImageService {

    private final UserRepository
            userRepo;
    private final OfficerRepository
            officerRepo;
    private final ContractOfficerRepository
            contractRepo;
    private final MinioService
            minioService;

    @Override
    public String getProfileImageUrl(
            Integer userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User", userId));

        return resolveProfileUrl(user);
    }

    @Override
    public String
    uploadOfficerProfileImage(
            Integer officerId,
            org.springframework.web
                    .multipart.MultipartFile
                    file) {
        throw new UnsupportedOperationException(
                "ប្រតិបត្តិការនេះមិនត្រូវបានគាំទ្រឡើយ។ សូមមេត្តាប្រើប្រាស់ Endpoint «POST /officers/{id}/profile-image» ជំនួសវិញ។"
        );
    }

    @Override
    public String
    uploadContractOfficerProfileImage(
            Integer contractOfficerId,
            org.springframework.web
                    .multipart.MultipartFile
                    file) {
        throw new UnsupportedOperationException(
                "ប្រតិបត្តិការនេះមិនត្រូវបានគាំទ្រឡើយ។ សូមមេត្តាប្រើប្រាស់ Endpoint «POST /contract-officers/{id}/profile-image» ជំនួសវិញ។"
        );
    }

    @Override
    @Transactional
    public void deleteOfficerProfileImage(
            Integer officerId) {
        throw new UnsupportedOperationException(
                "ប្រតិបត្តិការនេះមិនត្រូវបានគាំទ្រឡើយ។ សូមមេត្តាប្រើប្រាស់ Endpoint «DELETE /officers/{id}/profile-image» ជំនួសវិញ។"
        );
    }

    @Override
    @Transactional
    public void
    deleteContractOfficerProfileImage(
            Integer contractOfficerId) {
        throw new UnsupportedOperationException(
                "ប្រតិបត្តិការនេះមិនត្រូវបានគាំទ្រឡើយ។ សូមមេត្តាប្រើប្រាស់ Endpoint «DELETE /contract-officers/{id}/profile-image» ជំនួសវិញ។"
        );
    }

    public String resolveProfileUrl(
            User user) {

        if (user == null) return null;
        if (user.getOfficer() != null) {
            Officer officer = officerRepo
                    .findByIdWithAll(
                            user.getOfficer()
                                    .getOfficerId())
                    .orElse(null);

            if (officer != null
                    && officer
                    .getProfileAttachment()
                    != null
                    && officer
                    .getProfileAttachment()
                    .getFilePath()
                    != null) {
                try {
                    return minioService
                            .getPresignedUrl(
                                    officer
                                            .getProfileAttachment()
                                            .getFilePath());
                } catch (Exception e) {
                    log.warn(
                            "Presigned URL"
                                    + " failed for"
                                    + " Officer {}: {}",
                            officer
                                    .getOfficerId(),
                            e.getMessage());
                }
            }
        }


        if (user.getContractOfficer()
                != null) {
            ContractOfficer co =
                    contractRepo.findByIdWithAll(
                                    user.getContractOfficer()
                                            .getContractOfficerId())
                            .orElse(null);

            if (co != null
                    && co.getProfileAttachment()
                    != null
                    && co.getProfileAttachment()
                    .getFilePath()
                    != null) {
                try {
                    return minioService
                            .getPresignedUrl(
                                    co.getProfileAttachment()
                                            .getFilePath());
                } catch (Exception e) {
                    log.warn(
                            "Presigned URL"
                                    + " failed for CO"
                                    + " {}: {}",
                            co.getContractOfficerId(),
                            e.getMessage());
                }
            }
        }


        return null;
    }
}
