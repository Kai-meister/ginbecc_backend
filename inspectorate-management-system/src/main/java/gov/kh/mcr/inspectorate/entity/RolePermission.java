package gov.kh.mcr.inspectorate.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_permissions",
        uniqueConstraints = @UniqueConstraint(
                name  = "uq_role_permission",
                columnNames = {"role_id","permission_id"}),
        indexes = {
                @Index(name = "idx_rp_role",
                        columnList = "role_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
}