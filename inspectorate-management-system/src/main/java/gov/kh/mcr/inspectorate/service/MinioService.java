package gov.kh.mcr.inspectorate.service;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

    String upload(MultipartFile file, String path);

    String getPresignedUrl(String filePath);
    String getPresignedUrl(String filePath, int expiryMinutes);
    void delete(String filePath);
    boolean fileExists(String objectName);

    long getFileSize(String objectName);
    boolean exists(String filePath);
    void createBucketIfNotExists();
}
