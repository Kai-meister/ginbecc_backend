package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Announcement;
import gov.kh.mcr.inspectorate.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnnouncementRepository
        extends JpaRepository<Announcement, Integer> {

    Page<Announcement> findByStatusCode_StatusCode(
            String status, Pageable pageable);

    Page<Announcement> findByStatusCode_StatusCodeAndPriority(
            String status, Priority priority, Pageable pageable);

//    @Query("""
//        SELECT COUNT(r)
//        FROM AnnouncementRecipient r
//        WHERE r.officer.officerId = :officerId
//        AND   r.isRead = false
//        """)
//    long countUnreadByOfficer(@Param("officerId") Integer officerId);

    // AnnouncementRepository
    @Query("""
    SELECT a FROM Announcement a
    LEFT JOIN FETCH a.createdBy u
    LEFT JOIN FETCH a.statusCode s
    WHERE (:status IS NULL
           OR s.statusCode = :status)
    AND   (:priority IS NULL
           OR a.priority = :priority)
    AND   (:from IS NULL
           OR CAST(a.createdAt AS date) >= :from)
    AND   (:to IS NULL
           OR CAST(a.createdAt AS date) <= :to)
    ORDER BY a.createdAt DESC
    """)
    List<Announcement> findForReport(
            @Param("status")   String status,
            @Param("priority") String priority,
            @Param("from") LocalDate from,
            @Param("to")       LocalDate to);
}