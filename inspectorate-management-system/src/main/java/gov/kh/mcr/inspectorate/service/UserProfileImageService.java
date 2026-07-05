package gov.kh.mcr.inspectorate.service;

import org.springframework.web.multipart
        .MultipartFile;

public interface UserProfileImageService {

    String uploadOfficerProfileImage(
            Integer officerId,
            MultipartFile file);

    String uploadContractOfficerProfileImage(
            Integer contractOfficerId,
            MultipartFile file);

    String getProfileImageUrl(
            Integer userId);

    void deleteOfficerProfileImage(
            Integer officerId);

    void deleteContractOfficerProfileImage(
            Integer contractOfficerId);
}