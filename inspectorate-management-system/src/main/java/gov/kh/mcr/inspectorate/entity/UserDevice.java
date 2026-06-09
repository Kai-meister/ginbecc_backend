//phireak

package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_devices", uniqueConstraints = @UniqueConstraint(columnNames = "token"), indexes = {
        @Index(name = "idx_user_devices_user_id", columnList = "user_id"),
        @Index(name = "idx_user_devices_token", columnList = "token")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = true)
    private String platform; // WEB / ANDROID / IOS

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastSeenAt;
}