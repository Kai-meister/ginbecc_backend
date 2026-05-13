package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentRepository
        extends JpaRepository<Document, Integer> {

    Page<Document> findByOfficer_OfficerId(
            Integer officerId, Pageable pageable);

    Page<Document> findByOfficer_OfficerIdAndStatusCode_StatusCode(
            Integer officerId, String status, Pageable pageable);

    Page<Document> findByOfficer_OfficerIdAndDocumentType_DocumentTypeId(
            Integer officerId, Integer typeId, Pageable pageable);

    @Query("""
        SELECT d FROM Document d
        LEFT JOIN FETCH d.officer
        LEFT JOIN FETCH d.documentType
        LEFT JOIN FETCH d.statusCode
        LEFT JOIN FETCH d.attachment
        WHERE d.expiryDate <= :expiryDate
        AND d.statusCode.statusCode = 'APPROVED'
        ORDER BY d.expiryDate ASC
        """)
    List<Document> findExpiring(
            @Param("expiryDate") LocalDate expiryDate);

    @Query("""
        SELECT d.documentType.documentTypeName, COUNT(d)
        FROM Document d
        GROUP BY d.documentType.documentTypeName
        ORDER BY COUNT(d) DESC
        """)
    List<Object[]> countByDocumentType();
}