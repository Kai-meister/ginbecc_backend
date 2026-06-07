package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Document;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentRepository
        extends JpaRepository<Document, Integer> {

    // Filter by officer
    Page<Document> findByOfficer_OfficerId(
            Integer officerId, Pageable pageable);

    // Filter by status
    Page<Document>
    findByStatusCode_StatusCode(
            String statusCode, Pageable pageable);

    // Filter by type
    Page<Document>
    findByDocumentType_DocumentTypeId(
            Integer typeId, Pageable pageable);

    // Filter by officer + status
    Page<Document>
    findByOfficer_OfficerIdAndStatusCode_StatusCode(
            Integer officerId,
            String statusCode,
            Pageable pageable);

    // Expiring documents
    @Query("""
        SELECT d FROM Document d
        WHERE d.expiryDate <= :expiryDate
        AND   d.statusCode.statusCode
              NOT IN ('EXPIRED','ARCHIVED')
        ORDER BY d.expiryDate ASC
        """)
    List<Document> findExpiring(
            @Param("expiryDate")
            LocalDate expiryDate);

    // Count by document type
    long countByDocumentType_DocumentTypeId(
            Integer documentTypeId);

    // Expiring by officer
    @Query("""
    SELECT d FROM Document d
    WHERE d.expiryDate <= :expiryDate
    AND   d.officer.officerId = :officerId
    AND   d.statusCode.statusCode
          NOT IN ('EXPIRED','ARCHIVED')
    ORDER BY d.expiryDate ASC
    """)
    List<Document> findExpiringByOfficer(
            @Param("expiryDate") LocalDate expiryDate,
            @Param("officerId")  Integer officerId);

    //  Filter by status + type
    Page<Document>
    findByStatusCode_StatusCodeAndDocumentType_DocumentTypeId(
            String statusCode,
            Integer typeId,
            Pageable pageable);

    @Query("""
        SELECT d FROM Document d
        LEFT JOIN FETCH d.officer       o
        LEFT JOIN FETCH d.documentType  t
        LEFT JOIN FETCH d.statusCode    s
        LEFT JOIN FETCH d.uploadedBy    u
        WHERE (:officerId IS NULL
               OR o.officerId = :officerId)
        AND   (:status IS NULL
               OR s.statusCode = :status)
        AND   (:typeId IS NULL
               OR t.documentTypeId = :typeId)
        AND   (:from IS NULL
               OR CAST(d.createdAt AS date)
                  >= :from)
        AND   (:to IS NULL
               OR CAST(d.createdAt AS date)
                  <= :to)
        ORDER BY d.createdAt DESC
        """)
    List<Document> findForReport(
            @Param("officerId") Integer officerId,
            @Param("status")    String status,
            @Param("typeId")    Integer typeId,
            @Param("from")      LocalDate from,
            @Param("to")        LocalDate to);
}
