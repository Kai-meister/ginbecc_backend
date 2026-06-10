package gov.kh.mcr.inspectorate.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents",
        indexes = {
                @Index(name = "idx_doc_officer",
                        columnList = "officer_id"),
                @Index(name = "idx_doc_status",
                        columnList = "status_code"),
                @Index(name = "idx_doc_expiry",
                        columnList = "expiry_date")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Integer documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id",
            nullable = false)
    private Officer officer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id",
            nullable = false)
    private DocumentType documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id",
            nullable = true)
    private Attachment attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "document_name",
            length = 255, nullable = false)
    private String documentName;

    @Column(name = "document_number",
            length = 100)
    private String documentNumber;

    @Column(name = "note",
            columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code",
            referencedColumnName = "status_code")
    private LookupDocumentStatus statusCode;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
