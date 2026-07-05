package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcement_recipients",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ann_user",
                        // Fix — officer_id → user_id
                        columnNames = {
                                "announcement_id",
                                "user_id"})
        },
        indexes = {
                @Index(
                        name = "idx_recipient_ann",
                        columnList = "announcement_id"),
                // Fix — officer_id → user_id
                @Index(
                        name = "idx_recipient_user",
                        columnList = "user_id"),
                @Index(
                        name = "idx_recipient_read",
                        columnList =
                                "announcement_id,is_read")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnnouncementRecipient {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "recipient_id")
    private Integer recipientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id",
            nullable = false)
    private Announcement announcement;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false)
    private User user;

    @Column(name = "is_read",
            nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;
}