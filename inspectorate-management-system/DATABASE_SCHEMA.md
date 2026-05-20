# Database Schema Documentation

Generated from source code. Schema is auto-created by Hibernate on startup.

---

## Tables Overview (25 tables)

| Table | Description |
|---|---|
| `lookup_user_status` | User status lookup values |
| `lookup_officer_status` | Officer status lookup values |
| `lookup_document_status` | Document/approval status lookup values |
| `lookup_meeting_status` | Meeting status lookup values |
| `lookup_announcement_status` | Announcement status lookup values |
| `roles` | User roles |
| `permissions` | System permissions |
| `role_permissions` | Role-permission assignments |
| `users` | Login accounts |
| `departments` | Organizational departments |
| `positions` | Job positions |
| `officers` | HR profiles (civil servants) |
| `contract_officers` | Contract staff profiles |
| `document_types` | Document category definitions |
| `attachments` | File metadata (stored in MinIO) |
| `documents` | Official documents |
| `approvals` | Document approval records |
| `announcements` | Internal announcements |
| `announcement_recipients` | Per-officer read tracking |
| `notifications` | In-app notifications |
| `meeting_rooms` | Physical/virtual rooms |
| `meetings` | Meeting records |
| `meeting_attendees` | Per-meeting attendance |
| `meeting_minutes` | Meeting minutes (1:1 with meeting) |
| `activity_logs` | Audit trail |

---

## Table Definitions

### lookup_user_status
| Column | Type | Constraints |
|---|---|---|
| status_code | VARCHAR(30) | PK |
| label_kh | VARCHAR(100) | NOT NULL |
| label_en | VARCHAR(100) | NOT NULL |
| block_reason | VARCHAR(255) | nullable |
| sort_order | INT | NOT NULL |
| is_active | BOOLEAN | NOT NULL, default true |
| created_at | DATETIME | NOT NULL, immutable |

**Seed data:** ACTIVE, BLOCKED, SUSPENDED, INACTIVE, LOCKED

---

### lookup_officer_status
| Column | Type | Constraints |
|---|---|---|
| status_code | VARCHAR(30) | PK |
| label_kh | VARCHAR(100) | NOT NULL |
| label_en | VARCHAR(100) | NOT NULL |
| sort_order | INT | NOT NULL |
| is_active | BOOLEAN | NOT NULL, default true |
| created_at | DATETIME | NOT NULL, immutable |

**Seed data:** ACTIVE, INACTIVE, RETIRED, SUSPENDED, ON_LEAVE, PROBATION, RESIGNED, CONTRACT_EXPIRED

---

### lookup_document_status
| Column | Type | Constraints |
|---|---|---|
| status_code | VARCHAR(30) | PK |
| label_kh | VARCHAR(100) | NOT NULL |
| label_en | VARCHAR(100) | NOT NULL |
| applies_to | VARCHAR(50) | NOT NULL, default "BOTH" |
| sort_order | INT | NOT NULL |
| is_active | BOOLEAN | NOT NULL, default true |
| created_at | DATETIME | NOT NULL, immutable |

**Seed data:** DRAFT, PENDING, APPROVED, REJECTED, EXPIRED (DOCUMENT), ARCHIVED (DOCUMENT), CANCELLED (APPROVAL)

---

### lookup_meeting_status
| Column | Type | Constraints |
|---|---|---|
| status_code | VARCHAR(30) | PK |
| label_kh | VARCHAR(100) | NOT NULL |
| label_en | VARCHAR(100) | NOT NULL |
| sort_order | INT | NOT NULL |
| is_active | BOOLEAN | NOT NULL, default true |
| created_at | DATETIME | NOT NULL, immutable |

**Seed data:** DRAFT, SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, POSTPONED, RESCHEDULED

---

### lookup_announcement_status
| Column | Type | Constraints |
|---|---|---|
| status_code | VARCHAR(30) | PK |
| label_kh | VARCHAR(100) | NOT NULL |
| label_en | VARCHAR(100) | NOT NULL |
| sort_order | INT | NOT NULL |
| is_active | BOOLEAN | NOT NULL, default true |
| created_at | DATETIME | NOT NULL, immutable |

**Seed data:** DRAFT, PUBLISHED, SCHEDULED, ARCHIVED, CANCELLED

---

### roles
| Column | Type | Constraints |
|---|---|---|
| role_id | INT | PK, auto-increment |
| role_name | VARCHAR(100) | UNIQUE, NOT NULL |
| display_name | VARCHAR(150) | nullable |
| description | TEXT | nullable |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_role_name  
**Seed data:** SUPER_ADMIN, ADMIN, MANAGER, OFFICER, AUDITOR

---

### permissions
| Column | Type | Constraints |
|---|---|---|
| permission_id | INT | PK, auto-increment |
| permission_name | VARCHAR(150) | UNIQUE, NOT NULL |
| module | VARCHAR(100) | NOT NULL |
| description | TEXT | nullable |

**Indexes:** idx_perm_name, idx_perm_module  
**Seed data (25):** USER_CREATE, USER_UPDATE, USER_DELETE, USER_SUSPEND, ROLE_ASSIGN, OFFICER_MANAGE, SELF_PROFILE_VIEW, SELF_PROFILE_EDIT, DOC_APPROVE, DOC_VIEW_OWN, MEETING_MANAGE, MEETING_VIEW, MEETING_ATTEND, ANNOUNCEMENT_VIEW, ANNOUNCEMENT_CREATE, ANNOUNCEMENT_UPDATE, ANNOUNCEMENT_DELETE, ANNOUNCEMENT_PUBLISH, ATTACHMENT_UPLOAD, ATTACHMENT_MANAGE, AUDIT_VIEW, REPORT_EXPORT, DAILY_REPORT, DASHBOARD_OWN, NOTIFICATION

---

### role_permissions
| Column | Type | Constraints |
|---|---|---|
| id | INT | PK, auto-increment |
| role_id | INT | FK → roles, NOT NULL |
| permission_id | INT | FK → permissions, NOT NULL |

**Unique:** (role_id, permission_id)  
**Indexes:** idx_rp_role  
**Seed data:**
- SUPER_ADMIN → all 25 permissions
- ADMIN → 16 permissions (excludes USER_DELETE, ROLE_ASSIGN, SELF_PROFILE_*, DOC_VIEW_OWN, MEETING_ATTEND, ANNOUNCEMENT_DELETE, AUDIT_VIEW, REPORT_EXPORT)
- MANAGER → 11 permissions (excludes all USER_*, ROLE_ASSIGN, SELF_PROFILE_*, DOC_VIEW_OWN, MEETING_ATTEND, ANNOUNCEMENT_DELETE, AUDIT_VIEW, REPORT_EXPORT)
- OFFICER → 8 permissions (SELF_PROFILE_VIEW, SELF_PROFILE_EDIT, DOC_VIEW_OWN, MEETING_VIEW, MEETING_ATTEND, ANNOUNCEMENT_VIEW, DASHBOARD_OWN, NOTIFICATION)
- AUDITOR → 4 permissions (AUDIT_VIEW, REPORT_EXPORT, DASHBOARD_OWN, NOTIFICATION)

---

### users
| Column | Type | Constraints |
|---|---|---|
| user_id | INT | PK, auto-increment |
| role_id | INT | FK → roles, NOT NULL |
| officer_id | INT | FK → officers (OneToOne), nullable |
| created_by | INT | FK → users (self), nullable |
| updated_by | INT | FK → users (self), nullable |
| status_code | VARCHAR(30) | FK → lookup_user_status, nullable |
| uuid | VARCHAR(36) | UNIQUE, NOT NULL |
| user_name_kh | VARCHAR(150) | nullable |
| user_name_en | VARCHAR(150) | nullable |
| email | VARCHAR(150) | UNIQUE, NOT NULL |
| phone | VARCHAR(20) | nullable |
| password_hash | VARCHAR(255) | NOT NULL |
| must_change_password | BOOLEAN | default false |
| last_login_at | DATETIME | nullable |
| failed_login_count | INT | default 0 |
| locked_until | DATETIME | nullable |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_user_email, idx_user_uuid, idx_user_role, idx_user_status  
**Seed data (1 row):** email=admin@system.kh, password=Admin@1234, role=SUPER_ADMIN, status=ACTIVE

---

### departments
| Column | Type | Constraints |
|---|---|---|
| department_id | INT | PK, auto-increment |
| department_code | VARCHAR(20) | UNIQUE, NOT NULL |
| department_name | VARCHAR(255) | NOT NULL |
| description | TEXT | nullable |
| status | ENUM(ACTIVE, INACTIVE) | default ACTIVE |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_dept_code

---

### positions
| Column | Type | Constraints |
|---|---|---|
| position_id | INT | PK, auto-increment |
| position_code | VARCHAR(20) | UNIQUE, NOT NULL |
| position_name | VARCHAR(255) | NOT NULL |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_pos_code

---

### officers
| Column | Type | Constraints |
|---|---|---|
| officer_id | INT | PK, auto-increment |
| position_id | INT | FK → positions, nullable |
| department_id | INT | FK → departments, nullable |
| profile_attachment_id | INT | FK → attachments, nullable |
| status_code | VARCHAR(30) | FK → lookup_officer_status, nullable |
| officer_code | VARCHAR(50) | UNIQUE, NOT NULL |
| full_name_kh | VARCHAR(255) | NOT NULL |
| full_name_en | VARCHAR(255) | nullable |
| gender | ENUM(M, F) | nullable |
| dob | DATE | nullable |
| join_date | DATE | nullable |
| job_description | TEXT | nullable |
| education_level | VARCHAR(255) | nullable |
| specialization | VARCHAR(100) | nullable |
| salary_grade | VARCHAR(50) | nullable |
| current_address | TEXT | nullable |
| birthplace | VARCHAR(255) | nullable |
| living_status | VARCHAR(100) | nullable |
| phone | VARCHAR(20) | nullable |
| email | VARCHAR(150) | nullable |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_officer_code, idx_officer_dept, idx_officer_status

---

### contract_officers
| Column | Type | Constraints |
|---|---|---|
| contract_officer_id | INT | PK, auto-increment |
| department_id | INT | FK → departments, nullable |
| status_code | VARCHAR(30) | FK → lookup_officer_status, nullable |
| contract_officer_code | VARCHAR(50) | UNIQUE, NOT NULL |
| full_name_kh | VARCHAR(255) | NOT NULL |
| full_name_en | VARCHAR(255) | nullable |
| gender | ENUM(M, F) | nullable |
| job_level | VARCHAR(150) | nullable |
| job_description | TEXT | nullable |
| start_date | DATE | NOT NULL |
| end_date | DATE | NOT NULL |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_co_code, idx_co_end_date, idx_co_status

---

### document_types
| Column | Type | Constraints |
|---|---|---|
| document_type_id | INT | PK, auto-increment |
| document_type_code | VARCHAR(20) | UNIQUE, NOT NULL |
| document_type_name | VARCHAR(255) | NOT NULL |
| description | TEXT | nullable |
| status | ENUM(ACTIVE, INACTIVE) | default ACTIVE |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_dt_code

---

### attachments
| Column | Type | Constraints |
|---|---|---|
| attachment_id | INT | PK, auto-increment |
| file_path | VARCHAR(500) | NOT NULL |
| reference_id | INT | nullable (polymorphic) |
| reference_type | VARCHAR(50) | nullable |
| uploaded_by | INT | FK → users, nullable |
| original_name | VARCHAR(255) | nullable |
| is_active | BOOLEAN | default true |
| file_size | BIGINT | nullable |
| file_type | VARCHAR(50) | nullable |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_attach_ref (reference_id, reference_type), idx_attach_active, idx_attach_type

---

### documents
| Column | Type | Constraints |
|---|---|---|
| document_id | INT | PK, auto-increment |
| officer_id | INT | FK → officers, NOT NULL |
| document_type_id | INT | FK → document_types, NOT NULL |
| attachment_id | INT | FK → attachments, nullable |
| uploaded_by | INT | FK → users, nullable |
| status_code | VARCHAR(30) | FK → lookup_document_status, nullable |
| document_name | VARCHAR(255) | NOT NULL |
| document_number | VARCHAR(100) | nullable |
| note | TEXT | nullable |
| expiry_date | DATE | nullable |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_doc_officer, idx_doc_status, idx_doc_expiry

---

### approvals
| Column | Type | Constraints |
|---|---|---|
| approval_id | INT | PK, auto-increment |
| document_id | INT | FK → documents, NOT NULL |
| requested_by | INT | FK → users, nullable |
| requested_at | DATETIME | nullable |
| approved_by | INT | FK → users, nullable |
| approved_at | DATETIME | nullable |
| status_code | VARCHAR(30) | FK → lookup_document_status, nullable |
| comment | TEXT | nullable |

**Indexes:** idx_appr_document, idx_appr_status, idx_appr_requested

---

### announcements
| Column | Type | Constraints |
|---|---|---|
| announcement_id | INT | PK, auto-increment |
| attachment_path_id | INT | FK → attachments, nullable |
| created_by | INT | FK → users, NOT NULL |
| meeting_id | INT | FK → meetings, nullable |
| status_code | VARCHAR(30) | FK → lookup_announcement_status, nullable |
| title | VARCHAR(255) | NOT NULL |
| content | TEXT | NOT NULL |
| publish_at | DATETIME | nullable |
| priority | ENUM(LOW, MEDIUM, HIGH) | default MEDIUM |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_ann_status, idx_ann_priority, idx_ann_publish

---

### announcement_recipients
| Column | Type | Constraints |
|---|---|---|
| recipient_id | INT | PK, auto-increment |
| announcement_id | INT | FK → announcements, NOT NULL |
| officer_id | INT | FK → officers, NOT NULL |
| is_read | BOOLEAN | default false |
| read_at | DATETIME | nullable |

**Unique:** (announcement_id, officer_id)  
**Indexes:** idx_ar_announcement, idx_ar_officer, idx_ar_read

---

### notifications
| Column | Type | Constraints |
|---|---|---|
| notification_id | INT | PK, auto-increment |
| user_id | INT | FK → users, NOT NULL |
| title | VARCHAR(255) | NOT NULL |
| message | TEXT | nullable |
| type | ENUM(NotificationType) | NOT NULL |
| reference_id | INT | nullable |
| is_read | BOOLEAN | default false |
| created_at | DATETIME | NOT NULL, immutable |

**Indexes:** idx_notif_user, idx_notif_read

---

### meeting_rooms
| Column | Type | Constraints |
|---|---|---|
| room_id | INT | PK, auto-increment |
| image_path | INT | FK → attachments, nullable |
| room_code | VARCHAR(20) | UNIQUE, NOT NULL |
| location | VARCHAR(255) | nullable |
| capacity | INT | nullable |
| status | ENUM(AVAILABLE, UNAVAILABLE) | default AVAILABLE |
| facilities | TEXT | nullable |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_room_code, idx_room_status

---

### meetings
| Column | Type | Constraints |
|---|---|---|
| meeting_id | INT | PK, auto-increment |
| room_id | INT | FK → meeting_rooms, nullable |
| organizer_id | INT | FK → users, nullable |
| status_code | VARCHAR(30) | FK → lookup_meeting_status, nullable |
| title | VARCHAR(255) | NOT NULL |
| meeting_date | DATE | NOT NULL |
| start_time | TIME | NOT NULL |
| end_time | TIME | NOT NULL |
| meeting_type | ENUM(MeetingType) | NOT NULL |
| meeting_link | VARCHAR(255) | nullable |
| agenda | TEXT | nullable |
| note | TEXT | nullable |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_meeting_date, idx_meeting_room, idx_meeting_status

---

### meeting_attendees
| Column | Type | Constraints |
|---|---|---|
| attendee_id | INT | PK, auto-increment |
| meeting_id | INT | FK → meetings, NOT NULL |
| officer_id | INT | FK → officers, NOT NULL |
| role | ENUM(AttendeeRole) | NOT NULL |
| attendance_status | ENUM(AttendanceStatus) | nullable |
| check_in_time | DATETIME | nullable |
| note | TEXT | nullable |

**Unique:** (meeting_id, officer_id)  
**Indexes:** idx_att_meeting, idx_att_officer

---

### meeting_minutes
| Column | Type | Constraints |
|---|---|---|
| minute_id | INT | PK, auto-increment |
| meeting_id | INT | FK → meetings (OneToOne), UNIQUE, NOT NULL |
| attachment_path_id | INT | FK → attachments, nullable |
| recorded_by | INT | FK → users, nullable |
| summary | TEXT | nullable |
| decisions | TEXT | nullable |
| action_items | TEXT | nullable |
| created_at | DATETIME | NOT NULL, immutable |
| updated_at | DATETIME | auto-update |

**Indexes:** idx_min_meeting

---

### activity_logs
| Column | Type | Constraints |
|---|---|---|
| log_id | INT | PK, auto-increment |
| user_id | INT | FK → users, nullable |
| action | VARCHAR(100) | NOT NULL |
| entity_type | VARCHAR(100) | nullable |
| entity_id | INT | nullable |
| details | TEXT | nullable |
| created_at | DATETIME | NOT NULL, immutable |

**Indexes:** idx_log_user, idx_log_action, idx_log_created

---

## Relationship Diagram (simplified)

```
roles ──< role_permissions >── permissions
roles ──< users >── lookup_user_status
users >── officers >── positions
                  >── departments
                  >── lookup_officer_status
officers ──< documents >── document_types
                       >── lookup_document_status
documents ──< approvals >── lookup_document_status
departments ──< contract_officers
users ──< announcements >── lookup_announcement_status
officers ──< announcement_recipients >── announcements
users ──< notifications
meeting_rooms ──< meetings >── lookup_meeting_status
officers ──< meeting_attendees >── meetings
meetings ── meeting_minutes
users ──< activity_logs
attachments ── (polymorphic: officers, documents, meetings, announcements, meeting_rooms, meeting_minutes)
```

---

## Notes

- Schema is auto-generated by Hibernate (`ddl-auto`) — no manual SQL migrations
- All seed data is inserted by `DataInitializer` on startup (skips if rows already exist)
- File storage is in MinIO; `attachments` table holds only metadata
- Khmer labels (`label_kh`) are used in the UI; English labels (`label_en`) for API consumers
