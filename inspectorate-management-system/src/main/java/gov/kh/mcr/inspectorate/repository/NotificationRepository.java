package gov.kh.mcr.inspectorate.repository;
import gov.kh.mcr.inspectorate.entity.Notification;
import gov.kh.mcr.inspectorate.enums.NotificationType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification,
        Integer> {

    // GET list
    Page<Notification> findByUser_UserId(
            Integer userId, Pageable pageable);

    Page<Notification>
    findByUser_UserIdAndIsRead(
            Integer userId,
            Boolean isRead,
            Pageable pageable);

    // find with owner check
    Optional<Notification> findByNotificationIdAndUser_UserId(
            Integer notificationId,
            Integer userId);

    // Count
    long countByUser_UserIdAndIsRead(
            Integer userId, Boolean isRead);

    @Modifying
    @Query("""
        UPDATE Notification n
        SET    n.isRead = true,
               n.readAt = :now
        WHERE  n.user.userId = :userId
        AND    n.isRead = false
        """)
    int markAllAsRead(
            @Param("userId") Integer userId,
            @Param("now")    LocalDateTime now);


    @Modifying
    @Query("""
        DELETE FROM Notification n
        WHERE n.isRead = true
        AND   n.createdAt < :before
        """)
    int deleteOldRead(
            @Param("before") LocalDateTime before);


    @Modifying
    @Query("""
        DELETE FROM Notification n
        WHERE n.user.userId = :userId
        AND   n.isRead = true
        AND   n.createdAt < :before
        """)
    int deleteOldByUser(
            @Param("userId") Integer userId,
            @Param("before") LocalDateTime before);


    @Query("""
        SELECT n FROM Notification n
        LEFT JOIN FETCH n.user u
        WHERE (:userId IS NULL
               OR u.userId = :userId)
        AND   (:type IS NULL
               OR n.type = :type)
        AND   (:isRead IS NULL
               OR n.isRead = :isRead)
        AND   (:from IS NULL
               OR CAST(n.createdAt AS date)
                  >= :from)
        AND   (:to IS NULL
               OR CAST(n.createdAt AS date)
                  <= :to)
        ORDER BY n.createdAt DESC
        """)
    List<Notification> findForReport(
            @Param("userId") Integer userId,
            @Param("type")   String type,
            @Param("isRead") Boolean isRead,
            @Param("from")   LocalDate from,
            @Param("to")     LocalDate to);
}
