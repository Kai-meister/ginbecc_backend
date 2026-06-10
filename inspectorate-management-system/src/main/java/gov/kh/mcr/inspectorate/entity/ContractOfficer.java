package gov.kh.mcr.inspectorate.entity;

import gov.kh.mcr.inspectorate.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "contract_officers",
        indexes = {
                @Index(name = "idx_co_dept",
                        columnList = "department_id"),
                @Index(name = "idx_co_status",
                        columnList = "status_code"),
                @Index(name = "idx_co_code",
                        columnList =
                                "contract_officer_code"),
                @Index(name = "idx_co_end_date",
                        columnList = "end_date")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ContractOfficer {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "contract_officer_id")
    private Integer contractOfficerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id",
            nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "status_code",
            referencedColumnName = "status_code")
    private LookupOfficerStatus statusCode;

    @Column(name = "contract_officer_code",
            length = 50,
            unique = true, nullable = false)
    private String contractOfficerCode;

    @Column(name = "full_name_kh",
            length = 255, nullable = false)
    private String fullNameKh;

    @Column(name = "full_name_en",
            length = 255)
    private String fullNameEn;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "job_level", length = 50)
    private String jobLevel;

    @Column(name = "start_date",
            nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date",
            nullable = false)
    private LocalDate endDate;

    // Fix — កំណត់ចំណាំ
    @Column(name = "note",
            columnDefinition = "TEXT")
    private String note;

    // Fix — លេខកូដគណនេយ្យ
    @Column(name = "accounting_code",
            length = 50)
    private String accountingCode;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}