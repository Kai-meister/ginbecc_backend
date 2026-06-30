package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Approval;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApprovalRepository
        extends JpaRepository<Approval, Integer> {

    Page<Approval>
    findByStatusCode_StatusCode(
            String statusCode,
            Pageable pageable);

    Page<Approval>
    findByDocument_DocumentId(
            Integer documentId,
            Pageable pageable);

    Page<Approval>
    findByStatusCode_StatusCodeAndDepartment_DepartmentIdIn(
            String statusCode,
            List<Integer> departmentIds,
            Pageable pageable);

    // Fix — document.user not user
    Page<Approval>
    findByDocument_User_UserId(
            Integer userId,
            Pageable pageable);

    Page<Approval>
    findByStatusCode_StatusCodeAndDocument_User_UserId(
            String statusCode,
            Integer userId,
            Pageable pageable);

    Page<Approval>
    findByApprovedBy_UserId(
            Integer userId,
            Pageable pageable);

    boolean
    existsByDocument_DocumentIdAndStatusCode_StatusCode(
            Integer documentId,
            String statusCode);

    // Fix — REMOVED:
    // existsByUser_UserId(Integer)
    // (Approval entity មិនមាន field
    //  "user" ដោយផ្ទាល់ — ត្រូវប្រើ
    //  document.user instead, ប្រសិនបើ
    //  ត្រូវការ check)

    // Fix — ប្រសិនបើត្រូវការ check
    // ថា User ណាមួយមាន Approval History
    // ដែរឬទេ ប្រើ method នេះវិញ:
    boolean
    existsByDocument_User_UserId(
            Integer userId);

    @Query("""
        SELECT DISTINCT a FROM Approval a
        LEFT JOIN FETCH a.document       d
        LEFT JOIN FETCH d.user            u
        LEFT JOIN FETCH a.department      dept
        LEFT JOIN FETCH a.approvedBy      ab
        LEFT JOIN FETCH a.statusCode      s
        WHERE u.userId = :userId
           OR (ab IS NOT NULL
               AND ab.userId = :userId)
           OR EXISTS (
               SELECT 1 FROM
                   DepartmentManager dm
               WHERE dm.department
                       = a.department
               AND dm.user.userId
                       = :userId
           )
        ORDER BY a.requestedAt DESC
        """)
    Page<Approval> findVisibleToUser(
            @Param("userId") Integer userId,
            Pageable pageable);

    @Query("""
        SELECT DISTINCT a FROM Approval a
        LEFT JOIN FETCH a.document       d
        LEFT JOIN FETCH d.user            u
        LEFT JOIN FETCH a.department      dept
        LEFT JOIN FETCH a.approvedBy      ab
        LEFT JOIN FETCH a.statusCode      s
        WHERE s.statusCode = :status
        AND (
            u.userId = :userId
            OR (ab IS NOT NULL
                AND ab.userId = :userId)
            OR EXISTS (
                SELECT 1 FROM
                    DepartmentManager dm
                WHERE dm.department
                        = a.department
                AND dm.user.userId
                        = :userId
            )
        )
        ORDER BY a.requestedAt DESC
        """)
    Page<Approval>
    findByStatusCodeAndVisibleToUser(
            @Param("status") String status,
            @Param("userId") Integer userId,
            Pageable pageable);

    // ═══ ApprovalRepository.java — Fix
//      document.user + department ═══
    @Query("""
SELECT a FROM Approval a
LEFT JOIN FETCH a.document       d
LEFT JOIN FETCH d.user            u
LEFT JOIN FETCH a.department      dept
LEFT JOIN FETCH a.approvedBy      ab
LEFT JOIN FETCH a.statusCode      s
WHERE (:status IS NULL
       OR s.statusCode = :status)
AND   (:userId IS NULL
       OR u.userId = :userId)
AND   (CAST(:from AS java.time.LocalDateTime) IS NULL
       OR a.requestedAt >= :from)
AND   (CAST(:to AS java.time.LocalDateTime) IS NULL
       OR a.requestedAt <= :to)
ORDER BY a.requestedAt DESC
""")
    List<Approval> findForReport(
            @Param("status") String status,
            @Param("userId") Integer userId,
            @Param("from")   LocalDateTime from,
            @Param("to")     LocalDateTime to);
}