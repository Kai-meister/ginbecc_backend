package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Announcement;
import gov.kh.mcr.inspectorate.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository
        extends JpaRepository<Announcement, Integer> {

    @Query("""
        SELECT DISTINCT a FROM Announcement a
        LEFT JOIN FETCH a.createdBy u
        LEFT JOIN FETCH a.statusCode s
        LEFT JOIN AnnouncementRecipient r
            ON r.announcement = a
        WHERE (
            r.user.userId = :userId
            OR a.createdBy.userId = :userId
        )
        AND (:status IS NULL
             OR s.statusCode = :status)
        AND (:priority IS NULL
             OR a.priority = :priority)
        AND (a.expireAt IS NULL
             OR a.expireAt >= :today
             OR a.createdBy.userId
                = :userId)
        ORDER BY a.createdAt DESC
        """)
    Page<Announcement>
    findVisibleToUser(
            @Param("userId")
            Integer userId,
            @Param("status")
            String status,
            @Param("priority")
            Priority priority,
            @Param("today")
            LocalDate today,
            Pageable pageable);

    @Query("""
        SELECT a FROM Announcement a
        LEFT JOIN a.createdBy u
        LEFT JOIN u.officer o
        LEFT JOIN u.contractOfficer co
        WHERE (
            (o IS NOT NULL
             AND o.department.departmentId
                 = :deptId)
            OR
            (co IS NOT NULL
             AND co.department.departmentId
                 = :deptId)
        )
        ORDER BY a.createdAt DESC
        """)
    Page<Announcement>
    findByCreatorDepartmentId(
            @Param("deptId")
            Integer departmentId,
            Pageable pageable);


    // Fix — Expiring soon (for scheduler)
    @Query("""
        SELECT a FROM Announcement a
        WHERE a.expireAt IS NOT NULL
        AND   a.expireAt <= :expireDate
        AND   a.statusCode.statusCode
              = 'PUBLISHED'
        ORDER BY a.expireAt ASC
        """)
    List<Announcement> findExpiringSoon(
            @Param("expireDate")
            LocalDate expireDate);



//    Page<Announcement> findByPriority(
//            Priority priority, Pageable pageable);
//
//    Page<Announcement>
//    findByStatusCode_StatusCodeAndPriority(
//            String status,
//            Priority priority,
//            Pageable pageable);

    Page<Announcement>
    findByStatusCode_StatusCode(
            String status, Pageable pageable);

    Page<Announcement> findByPriority(
            Priority priority, Pageable pageable);

    Page<Announcement>
    findByStatusCode_StatusCodeAndPriority(
            String status,
            Priority priority,
            Pageable pageable);


    @Query("""
        SELECT a FROM Announcement a
        LEFT JOIN FETCH a.createdBy u
        LEFT JOIN FETCH a.statusCode s
        WHERE (:status IS NULL
               OR s.statusCode = :status)
        AND   (:priority IS NULL
               OR a.priority = :priority)
        AND   (a.expireAt IS NULL
               OR a.expireAt >= :today)
        ORDER BY a.createdAt DESC
        """)
    Page<Announcement> findActive(
            @Param("status")   String status,
            @Param("priority") Priority priority,
            @Param("today")    LocalDate today,
            Pageable pageable);

    @Query("""
        SELECT a FROM Announcement a
        LEFT JOIN FETCH a.createdBy u
        LEFT JOIN FETCH a.statusCode s
        WHERE (:status IS NULL
               OR s.statusCode = :status)
        AND   (:priority IS NULL
               OR a.priority = :priority)
        ORDER BY a.createdAt DESC
        """)
    Page<Announcement> findAll(
            @Param("status")   String status,
            @Param("priority") Priority priority,
            Pageable pageable);

    // ═══ AnnouncementRepository.java ═══
    @Query("""
    SELECT a FROM Announcement a
    LEFT JOIN FETCH a.createdBy u
    LEFT JOIN FETCH a.statusCode s
    WHERE (:status IS NULL
           OR s.statusCode = :status)
    AND   (:priority IS NULL
           OR a.priority = :priority)
    AND   (:from IS NULL
           OR CAST(a.createdAt AS date)
              >= :from)
    AND   (:to IS NULL
           OR CAST(a.createdAt AS date)
              <= :to)
    ORDER BY a.createdAt DESC
    """)
    List<Announcement> findForReport(
            @Param("status")   String status,
            @Param("priority") Priority priority,
            @Param("from")     LocalDate from,
            @Param("to")       LocalDate to);
}