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
    Page<Document> findByUser_UserId(
            Integer userId, Pageable pageable);

    Page<Document>
    findByStatusCode_StatusCode(
            String statusCode,
            Pageable pageable);

    Page<Document>
    findByDocumentType_DocumentTypeId(
            Integer typeId,
            Pageable pageable);

    Page<Document>
    findByUser_UserIdAndStatusCode_StatusCode(
            Integer userId,
            String statusCode,
            Pageable pageable);

    Page<Document>
    findByStatusCode_StatusCodeAndDocumentType_DocumentTypeId(
            String statusCode,
            Integer typeId,
            Pageable pageable);

    // Fix — STRICT department-scoped query
    // via user.officer OR
    // user.contractOfficer
    @Query("""
    SELECT d FROM Document d
    LEFT JOIN d.user u
    LEFT JOIN u.officer o
    LEFT JOIN o.department od
    LEFT JOIN u.contractOfficer co
    LEFT JOIN co.department cod
    WHERE (
        od.departmentId = :deptId
        OR
        cod.departmentId = :deptId
    )
    """)
    Page<Document> findByUserDepartmentId(
            @Param("deptId") Integer departmentId,
            Pageable pageable);

    @Query("""
        SELECT d FROM Document d
        WHERE d.expiryDate <= :expiryDate
        AND   d.user.userId = :userId
        AND   d.statusCode.statusCode
              NOT IN ('EXPIRED','ARCHIVED')
        ORDER BY d.expiryDate ASC
        """)
    List<Document> findExpiringByUser(
            @Param("expiryDate")
            LocalDate expiryDate,
            @Param("userId") Integer userId);

    // Fix — expiring by department
    @Query("""
        SELECT d FROM Document d
        LEFT JOIN d.user u
        WHERE d.expiryDate <= :expiryDate
        AND   (
            u.officer.department
                .departmentId = :deptId
            OR
            u.contractOfficer.department
                .departmentId = :deptId
        )
        AND   d.statusCode.statusCode
              NOT IN ('EXPIRED','ARCHIVED')
        ORDER BY d.expiryDate ASC
        """)
    List<Document>
    findExpiringByDepartment(
            @Param("expiryDate")
            LocalDate expiryDate,
            @Param("deptId")
            Integer departmentId);

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

    long countByDocumentType_DocumentTypeId(
            Integer documentTypeId);

    // ═══ DocumentRepository.java — Fix
//      user (not officer) ═══
    @Query("""
    SELECT d FROM Document d
    LEFT JOIN FETCH d.user        u
    LEFT JOIN FETCH d.documentType t
    LEFT JOIN FETCH d.statusCode   s
    LEFT JOIN FETCH d.uploadedBy   ub
    WHERE (:userId IS NULL
           OR u.userId = :userId)
    AND   (:status IS NULL
           OR s.statusCode = :status)
    AND   (:typeId IS NULL
           OR t.documentTypeId
              = :typeId)
    AND   (:from IS NULL
           OR CAST(d.createdAt AS date)
              >= :from)
    AND   (:to IS NULL
           OR CAST(d.createdAt AS date)
              <= :to)
    ORDER BY d.createdAt DESC
    """)
    List<Document> findForReport(
            @Param("userId") Integer userId,
            @Param("status") String status,
            @Param("typeId") Integer typeId,
            @Param("from")   LocalDate from,
            @Param("to")     LocalDate to);
}