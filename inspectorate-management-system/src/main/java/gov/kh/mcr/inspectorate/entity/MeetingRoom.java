package gov.kh.mcr.inspectorate.entity;
import gov.kh.mcr.inspectorate.enums.RoomStatus;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_path")
    private Attachment imagePath;

    @Column(name = "room_code",
            length = 20, unique = true, nullable = false)
    private String roomCode;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "capacity")
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column(name = "facilities",
            columnDefinition = "TEXT")
    private String facilities;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}