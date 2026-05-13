package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcement_recipients",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ann_officer",
                columnNames = {"announcement_id","officer_id"}),
        indexes = {
                @Index(name = "idx_ar_announcement",
                        columnList = "announcement_id"),
                @Index(name = "idx_ar_officer",
                        columnList = "officer_id"),
                @Index(name = "idx_ar_read",
                        columnList = "is_read")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnnouncementRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipient_id")
    private Integer recipientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private Officer officer;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}