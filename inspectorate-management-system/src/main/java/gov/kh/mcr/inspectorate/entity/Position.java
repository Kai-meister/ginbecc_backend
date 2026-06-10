package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions",
        indexes = {
                @Index(name = "idx_pos_code",
                        columnList = "position_code")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Integer positionId;

    @Column(name = "position_code",
            length = 20, unique = true, nullable = false)
    private String positionCode;

    @Column(name = "position_name",
            length = 255, nullable = false)
    private String positionName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}