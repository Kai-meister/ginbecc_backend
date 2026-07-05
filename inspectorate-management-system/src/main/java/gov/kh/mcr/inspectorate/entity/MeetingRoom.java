package gov.kh.mcr.inspectorate.entity;

import gov.kh.mcr.inspectorate.enums
        .MeetingRoomStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_rooms",
        indexes = {
                @Index(name = "idx_room_code",
                        columnList = "room_code"),
                @Index(name = "idx_room_status",
                        columnList = "status")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeetingRoom {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "room_code",
            length = 20,
            nullable = false,
            unique = true)
    private String roomCode;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "location",
            length = 255)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "attachment_id",
            nullable = true)
    private Attachment attachment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            nullable = false)
    @Builder.Default
    private MeetingRoomStatus status =
            MeetingRoomStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "current_meeting_id",
            nullable = true)
    private Meeting currentMeeting;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}