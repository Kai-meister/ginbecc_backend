package gov.kh.mcr.inspectorate.entity;

import gov.kh.mcr.inspectorate.enums
        .AttendanceStatus;
import gov.kh.mcr.inspectorate.enums
        .AttendeeRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_attendees",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_meeting_user",
                        columnNames = {"meeting_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_attendee_meeting",
                        columnList = "meeting_id"),
                @Index(name = "idx_attendee_user",
                        columnList = "user_id"),
                @Index(name = "idx_attendee_status",
                        columnList = "attendance_status")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeetingAttendee {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "attendee_id")
    private Integer attendeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id",
            nullable = false)
    private Meeting meeting;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",
            nullable = false)
    @Builder.Default
    private AttendeeRole role =
            AttendeeRole.ATTENDEE;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status",
            nullable = false)
    @Builder.Default
    private AttendanceStatus attendanceStatus = AttendanceStatus.INVITED;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "note",
            columnDefinition = "TEXT")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}