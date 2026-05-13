package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Announcement;
import gov.kh.mcr.inspectorate.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository
        extends JpaRepository<Announcement, Integer> {

    Page<Announcement> findByStatusCode_StatusCode(
            String status, Pageable pageable);

    Page<Announcement> findByStatusCode_StatusCodeAndPriority(
            String status, Priority priority, Pageable pageable);

    @Query("""
        SELECT COUNT(r)
        FROM AnnouncementRecipient r
        WHERE r.officer.officerId = :officerId
        AND   r.isRead = false
        """)
    long countUnreadByOfficer(@Param("officerId") Integer officerId);
}