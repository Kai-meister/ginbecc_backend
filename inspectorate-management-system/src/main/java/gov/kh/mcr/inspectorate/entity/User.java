package gov.kh.mcr.inspectorate.entity;

import gov.kh.mcr.inspectorate.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_email",
                        columnList = "email"),
                @Index(name = "idx_user_status",
                        columnList = "status_code"),
                @Index(name = "idx_user_role",
                        columnList = "role_id"),
                @Index(name = "idx_user_officer",
                        columnList = "officer_id"),
                @Index(name = "idx_user_contract",
                        columnList = "contract_officer_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id",
            unique = true, nullable = true)
    private Officer officer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_officer_id",
            unique = true, nullable = true)
    private ContractOfficer contractOfficer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code",
            referencedColumnName = "status_code",
            nullable = false)
    private LookupUserStatus statusCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type",
            nullable = false, length = 30)
    @Builder.Default
    private UserType userType = UserType.OFFICER;

    @Column(name = "user_name_kh",
            length = 255, nullable = false)
    private String userNameKh;

    @Column(name = "user_name_en", length = 255)
    private String userNameEn;

    @Column(name = "email",
            length = 150, unique = true,
            nullable = false)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "password_hash",
            nullable = false)
    private String passwordHash;

    @Builder.Default
    @Column(name = "failed_login_count",
            nullable = false)
    private Integer failedLoginCount = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}