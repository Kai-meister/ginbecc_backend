package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Attachment;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository
        extends JpaRepository<Attachment, Integer> {

    Optional<Attachment>
    findByReferenceIdAndReferenceTypeAndIsActiveTrue(
            Integer refId,
            AttachmentRefType refType);

    List<Attachment>
    findByReferenceIdAndReferenceType(
            Integer refId,
            AttachmentRefType refType);
    List<Attachment> findByReferenceIdAndReferenceTypeAndIsActive(
            Integer refId,
            AttachmentRefType refType,
            Boolean isActive);

//    Page<Attachment> findByUploadedBy_UserId(
//            Integer userId, Pageable pageable);

//    List<Attachment> findByFileType(
//            String fileType);

//    long countByReferenceIdAndReferenceTypeAndIsActiveTrue(
//            Integer refId,
//            AttachmentRefType refType);
//    @Modifying
//    @Query("""
//        UPDATE Attachment a
//        SET    a.isActive = false
//        WHERE  a.referenceId   = :refId
//        AND    a.referenceType = :refType
//        AND    a.isActive      = true
//        """)
//    int archiveByReference(
//            @Param("refId")   Integer refId,
//            @Param("refType") AttachmentRefType refType);

    @Modifying
    @Query("""
        DELETE FROM Attachment a
        WHERE  a.referenceId   = :refId
        AND    a.referenceType = :refType
        AND    a.isActive      = false
        """)
    int deleteArchivedByReference(
            @Param("refId")   Integer refId,
            @Param("refType") AttachmentRefType refType);

//    boolean existsByFilePath(String filePath);

//    @Query("""
//        SELECT a FROM Attachment a
//        WHERE  a.fileSize > :minSize
//        AND    a.isActive = true
//        ORDER BY a.fileSize DESC
//        """)
//    List<Attachment> findLargeFiles(
//            @Param("minSize") Long minSize);
}