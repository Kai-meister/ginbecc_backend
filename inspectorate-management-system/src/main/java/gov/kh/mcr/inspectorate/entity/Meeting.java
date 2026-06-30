package gov.kh.mcr.inspectorate.entity;

import gov.kh.mcr.inspectorate.enums.MeetingType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meetings",
        indexes = {
                @Index(name = "idx_meeting_date",
                        columnList = "meeting_date"),
                @Index(name = "idx_meeting_room",
                        columnList = "room_id"),
                @Index(name = "idx_meeting_status",
                        columnList = "status_code"),
                @Index(name = "idx_meeting_organizer",
                        columnList = "organizer_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Meeting {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Integer meetingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id",
            nullable = true)
    private MeetingRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id",
            nullable = false)
    private User organizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code",
            referencedColumnName = "status_code",
            nullable = false)
    private LookupMeetingStatus statusCode;

    @Column(name = "title",
            length = 255, nullable = false)
    private String title;

    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type",
            nullable = false)
    private MeetingType meetingType;

    @Column(name = "meeting_date",
            nullable = false)
    private LocalDate meetingDate;

    @Column(name = "start_time",
            nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time",
            nullable = false)
    private LocalTime endTime;

    @Column(name = "meeting_link",
            length = 500)
    private String meetingLink;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MeetingAttendee> attendees = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}