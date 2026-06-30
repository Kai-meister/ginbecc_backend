package gov.kh.mcr.inspectorate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "approvals",
        indexes = {
                @Index(name = "idx_approval_doc",
                        columnList = "document_id"),
                @Index(name = "idx_approval_status",
                        columnList = "status_code"),
                @Index(name = "idx_approval_approver",
                        columnList = "approved_by_user_id"),
                // Fix — index department (not
                // single manager, since multiple
                // managers can decide)
                @Index(name = "idx_approval_dept",
                        columnList = "department_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Approval {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Integer approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id",
            nullable = false)
    private Document document;

    // Fix — field name "department"
    // (NOT "assignedManager")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id",
            nullable = true)
    private Department department;
    //              ↑
    //  ត្រូវឲ្យត្រូវនឹង
    //  source = "department.departmentId"
    //  ក្នុង ApprovalMapper

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "status_code",
            referencedColumnName = "status_code",
            nullable = false)
    private LookupDocumentStatus statusCode;

    @Column(name = "comment",
            columnDefinition = "TEXT")
    private String comment;

    @Column(name = "requested_at",
            nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}