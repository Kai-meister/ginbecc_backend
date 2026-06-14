package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.AnnouncementRecipient;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRecipientRepository
        extends JpaRepository<AnnouncementRecipient, Integer> {

    List<AnnouncementRecipient>
    findByAnnouncement_AnnouncementId(
            Integer announcementId);
    // announcementId + officerId
    Optional<AnnouncementRecipient>
    findByAnnouncement_AnnouncementIdAndOfficer_OfficerId(
            Integer announcementId,
            Integer officerId);

    // Count read
    @Query("""
        SELECT COUNT(r)
        FROM AnnouncementRecipient r
        WHERE r.announcement.announcementId
              = :annId
        AND   r.isRead = true
        """)
    long countRead(
            @Param("annId") Integer annId);

    // Count total recipients
    long countByAnnouncement_AnnouncementId(
            Integer announcementId);

    // Check already read
    boolean
    existsByAnnouncement_AnnouncementIdAndOfficer_OfficerIdAndIsReadTrue(
            Integer announcementId,
            Integer officerId);
}