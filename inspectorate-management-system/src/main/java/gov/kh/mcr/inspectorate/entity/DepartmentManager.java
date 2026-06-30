package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "department_managers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_dept_manager",
                        columnNames = {
                                "department_id",
                                "user_id"})
        },
        indexes = {
                @Index(
                        name = "idx_dm_department",
                        columnList = "department_id"),
                @Index(
                        name = "idx_dm_user",
                        columnList = "user_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DepartmentManager {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "department_manager_id")
    private Integer departmentManagerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id",
            nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false)
    private User user;

    @Column(name = "is_primary",
            nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;
}