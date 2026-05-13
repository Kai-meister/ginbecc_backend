package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_minutes",
        indexes = {
                @Index(name = "idx_min_meeting",
                        columnList = "meeting_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeetingMinute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "minute_id")
    private Integer minuteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id",
            unique = true, nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_path_id")
    private Attachment attachmentPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    private User recordedBy;

    @Column(name = "summary",
            columnDefinition = "TEXT")
    private String summary;

    @Column(name = "decisions",
            columnDefinition = "TEXT")
    private String decisions;

    @Column(name = "action_items",
            columnDefinition = "TEXT")
    private String actionItems;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
