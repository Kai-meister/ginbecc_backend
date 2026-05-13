package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.service.MinioService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public String uploadFile(
            MultipartFile file,
            String refTypeCodeOrPrefix,
            Integer refId) {
        try {
            createBucketIfNotExists();

            String prefix;
            try {
                prefix = AttachmentRefType
                        .fromCode(refTypeCodeOrPrefix)
                        .toPathPrefix();
            } catch (IllegalArgumentException ex) {
                // Already lowercase prefix
                prefix = refTypeCodeOrPrefix
                        .toLowerCase()
                        .replace("_", "-");
            }

            String ext = getExt(
                    file.getOriginalFilename());
            String objectName =
                    prefix
                            + "/" + refId
                            + "/" + UUID.randomUUID()
                            + "." + ext;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(
                                    file.getInputStream(),
                                    file.getSize(), -1)
                            .contentType(
                                    file.getContentType())
                            .build());

            log.info("MinIO uploaded: {}",
                    objectName);
            return objectName;

        } catch (Exception ex) {
            log.error("MinIO upload error", ex);
            throw new BusinessException(
                    "Upload File មានបញ្ហា: "
                            + ex.getMessage());
        }
    }

    @Override
    public String getPresignedUrl(String objectName) {
        return getPresignedUrl(objectName, 60);
    }

    @Override
    public String getPresignedUrl(
            String objectName, int expiryMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectName)
                            .expiry(expiryMinutes,
                                    TimeUnit.MINUTES)
                            .build());
        } catch (Exception ex) {
            log.error("Presigned URL error", ex);
            throw new BusinessException(
                    "ទាញ URL Download មានបញ្ហា: "
                            + ex.getMessage());
        }
    }

    @Override
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
            log.info("MinIO deleted: {}",
                    objectName);
        } catch (Exception ex) {
            log.error("MinIO delete error", ex);
            throw new BusinessException(
                    "លុប File មានបញ្ហា: "
                            + ex.getMessage());
        }
    }

    @Override
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
            return true;
        } catch (ErrorResponseException ex) {
            if ("NoSuchKey".equals(
                    ex.errorResponse().code())) {
                return false;
            }
            throw new BusinessException(
                    "MinIO check error: "
                            + ex.getMessage());
        } catch (Exception ex) {
            throw new BusinessException(
                    "MinIO check error: "
                            + ex.getMessage());
        }
    }

    @Override
    public long getFileSize(String objectName) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()).size();
        } catch (Exception ex) {
            log.error("StatObject error", ex);
            return 0L;
        }
    }

    @Override
    public void createBucketIfNotExists() {
        try {
            boolean exists =
                    minioClient.bucketExists(
                            BucketExistsArgs.builder()
                                    .bucket(bucket)
                                    .build());
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucket)
                                .build());
                log.info(
                        "Created MinIO bucket: {}",
                        bucket);
            }
        } catch (Exception ex) {
            log.error("Bucket create error", ex);
            throw new BusinessException(
                    "MinIO bucket error: "
                            + ex.getMessage());
        }
    }

    private String getExt(String filename) {
        if (filename != null
                && filename.contains(".")) {
            return filename.substring(
                            filename.lastIndexOf('.') + 1)
                    .toLowerCase();
        }
        return "bin";
    }
}