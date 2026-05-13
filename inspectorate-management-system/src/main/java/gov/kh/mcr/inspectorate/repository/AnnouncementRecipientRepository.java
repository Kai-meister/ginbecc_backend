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
    findByAnnouncement_AnnouncementId(Integer announcementId);

    Optional<AnnouncementRecipient>
    findByAnnouncement_AnnouncementIdAndOfficer_OfficerId(
            Integer announcementId, Integer officerId);

    @Query("""
        SELECT COUNT(r) FROM AnnouncementRecipient r
        WHERE r.announcement.announcementId = :id
        AND r.isRead = true
        """)
    long countRead(@Param("id") Integer announcementId);

    @Query("""
        SELECT COUNT(r) FROM AnnouncementRecipient r
        WHERE r.announcement.announcementId = :id
        AND r.isRead = false
        """)
    long countUnread(@Param("id") Integer announcementId);
}