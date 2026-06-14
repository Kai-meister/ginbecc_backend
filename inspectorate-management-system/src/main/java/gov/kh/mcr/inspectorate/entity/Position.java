package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions",
        indexes = {
                @Index(name = "idx_pos_dept",
                        columnList = "department_id"),
                @Index(name = "idx_pos_code",
                        columnList = "position_code")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Position {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Integer positionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id",
            nullable = false)
    private Department department;

    @Column(name = "position_code",
            length = 50,
            unique = true, nullable = false)
    private String positionCode;

    @Column(name = "position_name",
            length = 255, nullable = false)
    private String positionName;

    @Column(name = "position_name_en",
            length = 255)
    private String positionNameEn;

    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}