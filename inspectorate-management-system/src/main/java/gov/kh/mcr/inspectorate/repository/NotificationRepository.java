package gov.kh.mcr.inspectorate.repository;
import gov.kh.mcr.inspectorate.entity.Notification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification,
        Integer> {

    Page<Notification> findByUser_UserId(
            Integer userId, Pageable pageable);

    Page<Notification> findByUser_UserIdAndIsRead(
            Integer userId,
            Boolean isRead,
            Pageable pageable);

    long countByUser_UserIdAndIsRead(
            Integer userId, Boolean isRead);

    @Modifying
    @Query("""
        UPDATE Notification n
        SET    n.isRead = true
        WHERE  n.user.userId = :userId
        AND    n.isRead = false
        """)
    int markAllAsRead(
            @Param("userId") Integer userId);
}