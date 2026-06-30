package gov.kh.mcr.inspectorate.entity;

import gov.kh.mcr.inspectorate.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "officers",
        indexes = {
                @Index(name = "idx_officer_code",
                        columnList = "officer_code"),
                @Index(name = "idx_officer_dept",
                        columnList = "department_id"),
                @Index(name = "idx_officer_status",
                        columnList = "status_code"),

        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Officer {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name = "officer_id")
    private Integer officerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id",
            nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_attachment_id",
            nullable = true)
    private Attachment profileAttachment;

    @Column(name = "officer_code",
            length = 50,
            unique = true, nullable = false)
    private String officerCode;

    @Column(name = "full_name_kh",
            length = 255, nullable = false)
    private String fullNameKh;

    @Column(name = "full_name_en",
            length = 255)
    private String fullNameEn;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 1)
    private Gender gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "job_description",
            columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "education_level",
            length = 255)
    private String educationLevel;

    @Column(name = "specialization",
            length = 100)
    private String specialization;

    @Column(name = "salary_grade",
            length = 50)
    private String salaryGrade;

    @Column(name = "current_address",
            columnDefinition = "TEXT")
    private String currentAddress;

    @Column(name = "birthplace", length = 255)
    private String birthplace;

    @Column(name = "living_status",
            length = 100)
    private String livingStatus;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code", referencedColumnName = "status_code", nullable = false)
    private LookupOfficerStatus statusCode;

    @CreationTimestamp
    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}