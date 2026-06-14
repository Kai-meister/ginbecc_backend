package gov.kh.mcr.inspectorate.entity;

import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications",
        indexes = {
                @Index(name = "idx_notif_user",
                        columnList = "user_id"),
                @Index(name = "idx_notif_user_read",
                        columnList = "user_id,is_read"),
                @Index(name = "idx_notif_type",
                        columnList = "type"),
                @Index(name = "idx_notif_created",
                        columnList = "created_at")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false)
    private User user;

    @Column(name = "title",
            length = 255, nullable = false)
    private String title;

    @Column(name = "message",
            columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type",
            nullable = false)
    private NotificationType type;

    @Column(name = "reference_id")
    private Integer referenceId;

    @Column(name = "is_read",
            nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at",
            nullable = false,
            updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}