package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Approval;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository
        extends JpaRepository<Approval, Integer> {

    Page<Approval>
    findByStatusCode_StatusCode(
            String statusCode, Pageable pageable);

    Page<Approval>
    findByRequestedBy_OfficerId(
            Integer officerId, Pageable pageable);

    Page<Approval>
    findByDocument_DocumentId(
            Integer documentId, Pageable pageable);

    Page<Approval>
    findByStatusCode_StatusCodeAndRequestedBy_OfficerId(
            String statusCode,
            Integer officerId,
            Pageable pageable);

    boolean
    existsByDocument_DocumentIdAndStatusCode_StatusCode(
            Integer documentId,
            String statusCode);


    Optional<Approval>
    findFirstByDocument_DocumentIdOrderByCreatedAtDesc(
            Integer documentId);

    @Query("""

    SELECT a FROM Approval a
    LEFT JOIN FETCH a.document   d
    LEFT JOIN FETCH a.requestedBy o
    LEFT JOIN FETCH a.approvedBy  u
    LEFT JOIN FETCH a.statusCode  s
    WHERE (:status IS NULL OR s.statusCode = :status)
      AND (CAST(:from AS java.time.LocalDateTime) IS NULL OR a.requestedAt >= :from)
      AND (CAST(:to   AS java.time.LocalDateTime) IS NULL OR a.requestedAt <= :to)
    ORDER BY a.requestedAt DESC
    """)
    List<Approval> findForReport(
            @Param("status") String status,
            @Param("from")   LocalDateTime from,
            @Param("to")     LocalDateTime to);
    }