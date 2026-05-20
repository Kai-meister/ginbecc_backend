package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs",
        indexes = {
                @Index(name = "idx_log_user",
                        columnList = "user_id"),
                @Index(name = "idx_log_action",
                        columnList = "action"),
                @Index(name = "idx_log_entity",
                        columnList = "entity_type,entity_id"),
                @Index(name = "idx_log_created",
                        columnList = "created_at"),
                // Composite — filter + sort
                @Index(name = "idx_log_user_created",
                        columnList = "user_id,created_at")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "action",
            length = 100, nullable = false)
    private String action;

    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "entity_id")
    private Integer entityId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}