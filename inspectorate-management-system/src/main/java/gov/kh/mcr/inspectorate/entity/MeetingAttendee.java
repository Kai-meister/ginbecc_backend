package gov.kh.mcr.inspectorate.entity;

import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
import gov.kh.mcr.inspectorate.enums.AttendeeRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_attendees",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_meeting_officer",
                columnNames = {"meeting_id","officer_id"}),
        indexes = {
                @Index(name = "idx_att_meeting",
                        columnList = "meeting_id"),
                @Index(name = "idx_att_officer",
                        columnList = "officer_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeetingAttendee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendee_id")
    private Integer attendeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private Officer officer;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AttendeeRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status")
    private AttendanceStatus attendanceStatus;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}