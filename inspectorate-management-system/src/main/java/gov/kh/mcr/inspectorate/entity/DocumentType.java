package gov.kh.mcr.inspectorate.entity;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_types",
        indexes = {
                @Index(name = "idx_dt_code",
                        columnList = "document_type_code")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_type_id")
    private Integer documentTypeId;

    @Column(name = "document_type_code",
            length = 20, unique = true, nullable = false)
    private String documentTypeCode;

    @Column(name = "document_type_name",
            length = 255, nullable = false)
    private String documentTypeName;

    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ActiveStatus status = ActiveStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}