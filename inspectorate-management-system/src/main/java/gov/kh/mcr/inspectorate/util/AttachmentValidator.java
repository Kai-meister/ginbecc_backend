package gov.kh.mcr.inspectorate.util;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;
import java.util.Set;

public final class AttachmentValidator {

    private AttachmentValidator() {}

    public static final long MAX_SIZE_BYTES =
            10L * 1024 * 1024;

    public static final Set<String> ALLOWED_MIME =
            Set.of(
                    "image/jpeg",     "image/png",
                    "image/gif",      "image/webp",
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-"
                            + "officedocument"
                            + ".wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-"
                            + "officedocument"
                            + ".spreadsheetml.sheet",
                    "text/plain",     "text/csv");

    public static final Set<String> ALLOWED_EXT =
            Set.of(
                    "jpg","jpeg","png","gif","webp",
                    "pdf","doc","docx",
                    "xls","xlsx","txt","csv");

    public static final Set<String> IMAGE_MIME =
            Set.of(
                    "image/jpeg","image/png",
                    "image/gif","image/webp");

    public static void validate(MultipartFile file) {
        validateNotEmpty(file);
        validateSize(file);
        validateMimeType(file);
        validateExtension(file);
    }
    public static void validateImage(
            MultipartFile file) {
        validateNotEmpty(file);
        validateSize(file);
        String mime = file.getContentType();
        if (mime == null
                || !IMAGE_MIME.contains(
                mime.toLowerCase())) {
            throw new BusinessException(
                    "រូបភាពប្រភេទ jpg, png, gif, webp"
                            + " ប៉ុណ្ណោះ — បានទទួល: " + mime);
        }
    }

    public static void validateRefType(
            String refTypeCode) {
        if (!AttachmentRefType.isValid(refTypeCode)) {
            throw new BusinessException(
                    "ref_type មិនត្រឹមត្រូវ: ["
                            + refTypeCode + "] — ប្រើ: "
                            + String.join(", ",
                            AttachmentRefType.allCodes()));
        }
    }

    public static void validateNotEmpty(
            MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    "File ទទេ — សូម Upload ឡើងវិញ");
        }
    }

    public static void validateSize(
            MultipartFile file) {
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BusinessException(
                    String.format(
                            "File ធំពេក: %.1f MB"
                                    + " / អតិបរមា 10 MB",
                            file.getSize()
                                    / (1024.0 * 1024)));
        }
    }
    public static void validateMimeType(
            MultipartFile file) {
        String mime = file.getContentType();
        if (mime == null
                || !ALLOWED_MIME.contains(
                mime.toLowerCase())) {
            throw new BusinessException(
                    "ប្រភេទ File មិនអនុញ្ញាត: "
                            + mime
                            + " — ប្រើ: jpg, png, pdf,"
                            + " docx, xlsx");
        }
    }
    public static void validateExtension(
            MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || !name.contains(".")) {
            throw new BusinessException(
                    "ឈ្មោះ File ខ្វះ Extension");
        }
        String ext = name
                .substring(name.lastIndexOf('.') + 1)
                .toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException(
                    "Extension [." + ext
                            + "] មិនអនុញ្ញាត");
        }
    }
}