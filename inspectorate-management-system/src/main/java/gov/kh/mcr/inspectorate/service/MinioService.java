package gov.kh.mcr.inspectorate.service;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

    String uploadFile(MultipartFile file,
                      String refType,
                      Integer refId);

    String getPresignedUrl(String objectName);

    String getPresignedUrl(String objectName,
                           int expiryMinutes);

    void deleteFile(String objectName);

    boolean fileExists(String objectName);

    long getFileSize(String objectName);

    void createBucketIfNotExists();
}