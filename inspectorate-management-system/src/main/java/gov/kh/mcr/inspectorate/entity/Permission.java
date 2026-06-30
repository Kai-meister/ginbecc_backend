package gov.kh.mcr.inspectorate.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions",
        indexes = {
                @Index(name = "idx_perm_name",
                        columnList = "permission_name"),
                @Index(name = "idx_perm_module",
                        columnList = "module")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Integer permissionId;

    @Column(name = "permission_name",
            length = 150, unique = true, nullable = false)
    private String permissionName;

    @Column(name = "module",
            length = 100, nullable = false)
    private String module;

    @Column(name = "display_name_kh",
            length = 255)
    private String displayNameKh;

    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;
}