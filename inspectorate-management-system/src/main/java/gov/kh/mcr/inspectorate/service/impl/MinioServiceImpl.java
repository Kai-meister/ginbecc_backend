package gov.kh.mcr.inspectorate.service.impl;

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

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public String upload(
            MultipartFile file,
            String path) {
        try {
            createBucketIfNotExists();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .stream(
                                    file.getInputStream(),
                                    file.getSize(), -1)
                            .contentType(
                                    file.getContentType())
                            .build());

            log.info("Uploaded: {}", path);
            return path;

        } catch (Exception ex) {
            throw new BusinessException(
                    "ការផ្ទុកឡើងឯកសារមានបញ្ហា ឬមិនជោគជ័យ " + ex.getMessage());
        }
    }

    @Override
    public String getPresignedUrl(
            String filePath) {
        return getPresignedUrl(filePath, 60);
    }

    @Override
    public String getPresignedUrl(
            String filePath,
            int expiryMinutes) {

        try {
            return minioClient
                    .getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs
                                    .builder()
                                    .method(Method.GET)
                                    .bucket(bucket)
                                    .object(filePath)
                                    .expiry(
                                            expiryMinutes,
                                            TimeUnit.MINUTES)
                                    .build());

        } catch (Exception e) {
            log.error(
                    "Presigned URL failed"
                            + " for path [{}]: {}",
                    filePath, e.getMessage());
            throw new BusinessException(
                    "មិនអាចបង្កើត URL"
                            + " សម្រាប់ File: "
                            + filePath);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(filePath)
                            .build());
            log.info("MinIO deleted: {}",
                    filePath);
        } catch (Exception ex) {
            log.error("MinIO delete error", ex);
            throw new BusinessException(
                    "ការលុបឯកសារមានបញ្ហា ឬមិនជោគជ័យ "
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
                    "ការពិនិត្យវត្តមានឯកសារក្នុងប្រព័ន្ធផ្ទុកទិន្នន័យមានបញ្ហា "
                            + ex.getMessage());
        } catch (Exception ex) {
            throw new BusinessException(
                    "ការពិនិត្យវត្តមានឯកសារក្នុងប្រព័ន្ធផ្ទុកទិន្នន័យមានបញ្ហា "
                            + ex.getMessage());
        }
    }

    @Override
    public boolean exists(String filePath) {
        return fileExists(filePath);
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
                    "ការពិនិត្យ ឬបង្កើតប្រអប់ផ្ទុកទិន្នន័យមានបញ្ហា "
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
