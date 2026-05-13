package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lookup_meeting_status")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LookupMeetingStatus {

    @Id
    @Column(name = "status_code", length = 30)
    private String statusCode;

    @Column(name = "label_kh",
            length = 100, nullable = false)
    private String labelKh;

    @Column(name = "label_en",
            length = 100, nullable = false)
    private String labelEn;

    @Column(name = "sort_order",
            nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active",
            nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;
}