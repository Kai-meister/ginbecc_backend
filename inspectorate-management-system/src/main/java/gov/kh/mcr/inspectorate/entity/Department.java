package gov.kh.mcr.inspectorate.entity;

import gov.kh.mcr.inspectorate.enums
        .ActiveStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments",
        indexes = {
                @Index(name = "idx_dept_code",
                        columnList = "department_code")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "department_code",
            length = 20,
            unique = true, nullable = false)
    private String departmentCode;

    @Column(name = "department_name",
            length = 255, nullable = false)
    private String departmentName;

    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "department",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private List<DepartmentManager>
            managers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            nullable = false)
    @Builder.Default
    private ActiveStatus status =
            ActiveStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}