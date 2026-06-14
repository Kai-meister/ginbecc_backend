package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcement_recipients",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ann_officer",
                        columnNames = {
                                "announcement_id",
                                "officer_id"})
        },
        indexes = {
                @Index(
                        name = "idx_recipient_ann",
                        columnList = "announcement_id"),
                @Index(
                        name = "idx_recipient_officer",
                        columnList = "officer_id"),
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
    @JoinColumn(name = "officer_id",
            nullable = false)
    private Officer officer;

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