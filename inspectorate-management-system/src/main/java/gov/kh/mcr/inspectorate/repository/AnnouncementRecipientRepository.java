package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.AnnouncementRecipient;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRecipientRepository
        extends JpaRepository<AnnouncementRecipient, Integer> {


    List<AnnouncementRecipient>
    findByAnnouncement_AnnouncementId(
            Integer announcementId);

    Optional<AnnouncementRecipient>
    findByAnnouncement_AnnouncementIdAndUser_UserId(
            Integer announcementId,
            Integer userId);

    // Fix — used in getById() check
    boolean
    existsByAnnouncement_AnnouncementIdAndUser_UserId(
            Integer announcementId,
            Integer userId);

    long countByAnnouncement_AnnouncementId(
            Integer announcementId);

    @Query("""
        SELECT COUNT(r)
        FROM AnnouncementRecipient r
        WHERE r.announcement.announcementId
              = :annId
        AND   r.isRead = true
        """)
    long countRead(
            @Param("annId") Integer annId);

    void deleteByAnnouncement_AnnouncementId(
            Integer announcementId);

    // ═══ AnnouncementRecipientRepository.java
//      — Fix user (not officer) ═══
    @Query("""
    SELECT r FROM AnnouncementRecipient r
    LEFT JOIN FETCH r.announcement a
    LEFT JOIN FETCH r.user         u
    LEFT JOIN FETCH u.officer       o
    LEFT JOIN FETCH o.department    od
    LEFT JOIN FETCH u.contractOfficer co
    LEFT JOIN FETCH co.department   cod
    WHERE (:annId IS NULL
           OR a.announcementId
              = :annId)
    AND   (:isRead IS NULL
           OR r.isRead = :isRead)
    AND   (:from IS NULL
           OR CAST(r.createdAt AS date)
              >= :from)
    AND   (:to IS NULL
           OR CAST(r.createdAt AS date)
              <= :to)
    ORDER BY a.announcementId DESC,
             r.isRead ASC
    """)
    List<AnnouncementRecipient>
    findRecipientsForReport(
            @Param("annId")  Integer annId,
            @Param("isRead") Boolean isRead,
            @Param("from") LocalDate from,
            @Param("to")     LocalDate to);


}