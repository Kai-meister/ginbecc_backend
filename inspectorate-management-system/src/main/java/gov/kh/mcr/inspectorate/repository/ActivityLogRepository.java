package gov.kh.mcr.inspectorate.repository;
import gov.kh.mcr.inspectorate.entity.ActivityLog;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface ActivityLogRepository
        extends JpaRepository<ActivityLog, Integer> {

    @Query("""
        SELECT a FROM ActivityLog a
        WHERE (:userId IS NULL
               OR a.user.userId = :userId)
        AND   (:action IS NULL
               OR a.action = :action)
        AND   (:entityType IS NULL
               OR a.entityType = :entityType)
        AND   (:from IS NULL
               OR a.createdAt >= :from)
        AND   (:to IS NULL
               OR a.createdAt <= :to)
        ORDER BY a.createdAt DESC
        """)
    Page<ActivityLog> findWithFilters(
            @Param("userId")     Integer userId,
            @Param("action")     String action,
            @Param("entityType") String entityType,
            @Param("from")       LocalDateTime from,
            @Param("to")         LocalDateTime to,
            Pageable pageable);
}