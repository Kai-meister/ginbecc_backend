package gov.kh.mcr.inspectorate.entity;

import gov.kh.mcr.inspectorate.enums.Priority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements",
        indexes = {
                @Index(name = "idx_ann_status",
                        columnList = "status_code"),
                @Index(name = "idx_ann_priority",
                        columnList = "priority"),
                @Index(name = "idx_ann_publish",
                        columnList = "publish_at"),
                @Index(name = "idx_ann_expire",
                        columnList = "expire_at")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Announcement {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Integer announcementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id",
            nullable = true)
    private Attachment attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by",
            nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id",
            nullable = true)
    private Meeting meeting;

    @Column(name = "title",
            length = 255, nullable = false)
    private String title;

    @Column(name = "content",
            columnDefinition = "TEXT",
            nullable = false)
    private String content;

    @Column(name = "publish_at")
    private LocalDateTime publishAt;

    @Column(name = "expire_at")
    private LocalDate expireAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "status_code",
            referencedColumnName = "status_code",
            nullable = false)
    private LookupAnnouncementStatus statusCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority",
            nullable = false)
    @Builder.Default
    private Priority priority =
            Priority.MEDIUM;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}