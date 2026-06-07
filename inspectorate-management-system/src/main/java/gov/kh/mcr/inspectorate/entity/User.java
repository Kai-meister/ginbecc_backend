package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

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
                        columnList = "officer_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id",
            unique = true,
            nullable = true)
    private Officer officer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id",
            nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "status_code",
            referencedColumnName = "status_code")
    private LookupUserStatus statusCode;

    @Column(name = "user_name_kh",
            length = 255, nullable = false)
    private String userNameKh;

    @Column(name = "user_name_en",
            length = 255)
    private String userNameEn;

    @Column(name = "email",
            length = 150,
            unique = true, nullable = false)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "password_hash",
            nullable = false)
    private String passwordHash;

    @Column(name = "must_change_password",
            nullable = false)
    @Builder.Default
    private Boolean mustChangePassword = false;

    @Column(name = "failed_login_count",
            nullable = false)
    @Builder.Default
    private Integer failedLoginCount = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}