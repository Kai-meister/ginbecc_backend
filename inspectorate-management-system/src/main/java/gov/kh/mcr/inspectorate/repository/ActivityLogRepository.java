package gov.kh.mcr.inspectorate.repository;
import gov.kh.mcr.inspectorate.entity.ActivityLog;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository
        extends JpaRepository<ActivityLog, Integer> {

@Query("""
SELECT a
FROM ActivityLog a
LEFT JOIN FETCH a.user
WHERE
(:userId IS NULL OR a.user.userId = :userId)
AND (:action IS NULL OR a.action = :action)
AND (:entityType IS NULL OR a.entityType = :entityType)
AND (CAST(:from AS java.time.LocalDateTime) IS NULL
     OR a.createdAt >= :from)
AND (CAST(:to AS java.time.LocalDateTime) IS NULL
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

//    @Query("""
//        SELECT a.entityType, COUNT(a)
//        FROM ActivityLog a
//        WHERE a.createdAt >= :from
//        GROUP BY a.entityType
//        ORDER BY COUNT(a) DESC
//        """)
//    java.util.List<Object[]> countByEntityType(
//            @Param("from") LocalDateTime from);

    @Modifying
    @Query("""
        DELETE FROM ActivityLog a
        WHERE a.createdAt < :before
        """)
    int deleteOlderThan(
            @Param("before") LocalDateTime before);

}

