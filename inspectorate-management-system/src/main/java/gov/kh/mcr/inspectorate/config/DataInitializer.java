package gov.kh.mcr.inspectorate.config;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.*;
import gov.kh.mcr.inspectorate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer
        implements CommandLineRunner {

    private final LookupOfficerStatusRepository
            officerStatusRepo;
    private final LookupDocumentStatusRepository
            documentStatusRepo;
    private final LookupMeetingStatusRepository
            meetingStatusRepo;
    private final LookupAnnouncementStatusRepository
            announcementStatusRepo;
    private final LookupUserStatusRepository
            userStatusRepo;
    private final RoleRepository
            roleRepository;
    private final PermissionRepository
            permissionRepository;
    private final RolePermissionRepository
            rolePermissionRepository;
    private final UserRepository
            userRepository;
    private final PasswordEncoder
            passwordEncoder;

    @Override
    public void run(String... args) {
        initOfficerStatuses();
        initDocumentStatuses();
        initMeetingStatuses();
        initAnnouncementStatuses();
        initUserStatuses();
        initRoles();
        initPermissions();
        initSuperAdmin();
        log.info(" DataInitializer បានបញ្ចប់");
    }

    private void initOfficerStatuses() {
        if (officerStatusRepo.count() > 0) return;

        LocalDateTime now = LocalDateTime.now();

        // code, labelKh, labelEn, sortOrder
        Object[][] data = {
                {OfficerStatusCode.ACTIVE.getCode(),
                        "កំពុងបម្រើការ","Active",1},
                {OfficerStatusCode.INACTIVE.getCode(),
                        "មិនសកម្ម","Inactive",2},
                {OfficerStatusCode.RETIRED.getCode(),
                        "ចូលនិវត្តន៍","Retired",3},
                {OfficerStatusCode.SUSPENDED.getCode(),
                        "ផ្អាកការងារ","Suspended",4},
                {OfficerStatusCode.ON_LEAVE.getCode(),
                        "ឈប់សម្រាក","On Leave",5},
                {OfficerStatusCode.PROBATION.getCode(),
                        "សាកល្បង","Probation",6},
                {OfficerStatusCode.RESIGNED.getCode(),
                        "បានលាឈប់", "Resigned",7},
                {OfficerStatusCode.CONTRACT_EXPIRED
                        .getCode(),
                        "កិច្ចសន្យាផុត", "Contract Expired",8},
        };

        for (Object[] d : data) {
            officerStatusRepo.save(
                    LookupOfficerStatus.builder()
                            .statusCode((String) d[0])
                            .labelKh((String) d[1])
                            .labelEn((String) d[2])
                            .sortOrder((Integer) d[3])
                            .isActive(true)
                            .createdAt(now)
                            .build());
        }
        log.info("Officer Statuses (8)");
    }

    private void initDocumentStatuses() {
        if (documentStatusRepo.count() > 0) return;

        LocalDateTime now = LocalDateTime.now();

        // code, labelKh, labelEn, appliesTo, sort
        Object[][] data = {
                {DocumentStatusCode.DRAFT.getCode(),
                        "សេចក្តីព្រាង","Draft",
                        "BOTH", 1},
                {DocumentStatusCode.PENDING.getCode(),
                        "រង់ចាំអនុម័ត", "Pending",
                        "BOTH",2},
                {DocumentStatusCode.APPROVED.getCode(),
                        "បានអនុម័ត","Approved",
                        "BOTH",3},
                {DocumentStatusCode.REJECTED.getCode(),
                        "បានបដិសេធ","Rejected",
                        "BOTH",4},
                {DocumentStatusCode.EXPIRED.getCode(),
                        "ផុតកំណត់","Expired",
                        "DOCUMENT", 5},
                {DocumentStatusCode.ARCHIVED.getCode(),
                        "ប័ណ្ណសារ","Archived",
                        "DOCUMENT", 6},
                {DocumentStatusCode.CANCELLED.getCode(),
                        "បានលុបចោល",  "Cancelled",
                        "APPROVAL", 7},
        };

        for (Object[] d : data) {
            documentStatusRepo.save(
                    LookupDocumentStatus.builder()
                            .statusCode((String) d[0])
                            .labelKh((String) d[1])
                            .labelEn((String) d[2])
                            .appliesTo((String) d[3])
                            .sortOrder((Integer) d[4])
                            .isActive(true)
                            .createdAt(now)
                            .build());
        }
        log.info(" Document Statuses (7)");
    }

    private void initMeetingStatuses() {
        if (meetingStatusRepo.count() > 0) return;

        LocalDateTime now = LocalDateTime.now();

        Object[][] data = {
                {MeetingStatusCode.DRAFT.getCode(),
                        "សេចក្តីព្រាង","Draft",       1},
                {MeetingStatusCode.SCHEDULED.getCode(),
                        "កំណត់ពេល","Scheduled",   2},
                {MeetingStatusCode.CONFIRMED.getCode(),
                        "បានបញ្ជាក់","Confirmed",   3},
                {MeetingStatusCode.IN_PROGRESS.getCode(),
                        "កំពុងប្រជុំ", "In Progress", 4},
                {MeetingStatusCode.COMPLETED.getCode(),
                        "បានបញ្ចប់","Completed",   5},
                {MeetingStatusCode.CANCELLED.getCode(),
                        "បានលុបចោល","Cancelled",   6},
                {MeetingStatusCode.POSTPONED.getCode(),
                        "បានពន្យារ","Postponed",   7},
                {MeetingStatusCode.RESCHEDULED.getCode(),
                        "កំណត់ពេលឡើងវិញ","Rescheduled", 8},
        };

        for (Object[] d : data) {
            meetingStatusRepo.save(
                    LookupMeetingStatus.builder()
                            .statusCode((String) d[0])
                            .labelKh((String) d[1])
                            .labelEn((String) d[2])
                            .sortOrder((Integer) d[3])
                            .isActive(true)
                            .createdAt(now)
                            .build());
        }
        log.info(" Meeting Statuses (8)");
    }

    private void initAnnouncementStatuses() {
        if (announcementStatusRepo.count() > 0)
            return;

        LocalDateTime now = LocalDateTime.now();

        Object[][] data = {
                {AnnouncementStatusCode.DRAFT.getCode(),
                        "សេចក្តីព្រាង","Draft",     1},
                {AnnouncementStatusCode.PUBLISHED
                        .getCode(),
                        "បានផ្សព្វផ្សាយ","Published", 2},
                {AnnouncementStatusCode.SCHEDULED
                        .getCode(),
                        "កំណត់ពេល","Scheduled", 3},
                {AnnouncementStatusCode.ARCHIVED
                        .getCode(),
                        "ប័ណ្ណសារ","Archived",  4},
                {AnnouncementStatusCode.CANCELLED
                        .getCode(),
                        "បានលុបចោល","Cancelled", 5},
        };

        for (Object[] d : data) {
            announcementStatusRepo.save(
                    LookupAnnouncementStatus.builder()
                            .statusCode((String) d[0])
                            .labelKh((String) d[1])
                            .labelEn((String) d[2])
                            .sortOrder((Integer) d[3])
                            .isActive(true)
                            .createdAt(now)
                            .build());
        }
        log.info("Announcement Statuses (5)");
    }

    private void initUserStatuses() {
        if (userStatusRepo.count() > 0) return;

        LocalDateTime now = LocalDateTime.now();

        Object[][] data = {
                {UserStatusCode.ACTIVE.getCode(),
                        "សកម្ម","Active",null, 1},
                {UserStatusCode.BLOCKED.getCode(),
                        "ត្រូវបានបិទ","Blocked",null, 2},
                {UserStatusCode.SUSPENDED.getCode(),
                        "ផ្អាកបណ្ដោះអាសន្ន", "Suspended", null, 3},
                {UserStatusCode.INACTIVE.getCode(),
                        "មិនសកម្ម","Inactive",  null, 4},
                {UserStatusCode.LOCKED.getCode(),
                        "បានចាក់សោ","Locked",
                        "Failed login 5 times",5},
        };

        for (Object[] d : data) {
            userStatusRepo.save(
                    LookupUserStatus.builder()
                            .statusCode((String) d[0])
                            .labelKh((String) d[1])
                            .labelEn((String) d[2])
                            .blockReason((String) d[3])
                            .sortOrder((Integer) d[4])
                            .isActive(true)
                            .createdAt(now)
                            .build());
        }
        log.info("User Statuses (5)");
    }

    private void initRoles() {
        if (roleRepository.count() > 0) return;
        String[][] data = {
                {"SUPER_ADMIN",
                        "អ្នកគ្រប់គ្រងជាន់ខ្ពស់",
                        "Full control"},
                {"ADMIN",
                        "មន្ត្រីរដ្ឋបាល",
                        "Daily operations"},
                {"MANAGER",
                        "ប្រធានផ្នែក",
                        "Department head"},
                {"OFFICER",
                        "មន្ត្រីទូទៅ",
                        "Basic access"},
                {"AUDITOR",
                        "អ្នកសវនករ",
                        "Audit access"},
        };
        for (String[] d : data) {
            roleRepository.save(
                    Role.builder()
                            .roleName(d[0])
                            .displayName(d[1])
                            .description(d[2])
                            .build());
        }
        log.info("Roles (5)");
    }


    private void initPermissions() {
        // Idempotent: runs every startup. Missing permissions are inserted
        // and role grants are reconciled, so adding/renaming permissions in
        // code no longer requires a clean DB. (Old behaviour early-returned
        // when any permission existed, causing permanent seed drift.)
        String[][] perms = {

                // User Management
                {"USER_VIEW",           "USER"},
                {"USER_CREATE",         "USER"},
                {"USER_UPDATE",         "USER"},
                {"USER_DELETE",         "USER"},
                {"USER_RESET_PASSWORD", "USER"},
                {"ROLE_VIEW",           "USER"},
                {"ROLE_ASSIGN",         "USER"},
                {"PERMISSION_VIEW",     "USER"},
                {"PERMISSION_MANAGE",   "USER"},

                // Officer Management
                {"OFFICER_VIEW",             "OFFICER"},
                {"OFFICER_CREATE",           "OFFICER"},
                {"OFFICER_UPDATE",           "OFFICER"},
                {"OFFICER_DELETE",           "OFFICER"},
                {"OFFICER_VIEW_SENSITIVE",   "OFFICER"},
                {"CONTRACT_OFFICER_VIEW",    "OFFICER"},
                {"CONTRACT_OFFICER_CREATE",  "OFFICER"},
                {"CONTRACT_OFFICER_UPDATE",  "OFFICER"},
                {"CONTRACT_OFFICER_DELETE",  "OFFICER"},
                {"DEPARTMENT_VIEW",          "OFFICER"},
                {"DEPARTMENT_MANAGE",        "OFFICER"},
                {"POSITION_VIEW",            "OFFICER"},
                {"POSITION_MANAGE",          "OFFICER"},

                // Document Management
                {"DOCUMENT_VIEW",         "DOCUMENT"},
                {"DOCUMENT_VIEW_ALL",     "DOCUMENT"},
                {"DOCUMENT_CREATE",       "DOCUMENT"},
                {"DOCUMENT_UPDATE",       "DOCUMENT"},
                {"DOCUMENT_DELETE",       "DOCUMENT"},
                {"DOCUMENT_TYPE_MANAGE",  "DOCUMENT"},
                {"APPROVAL_REQUEST",      "DOCUMENT"},
                {"APPROVAL_REVIEW",       "DOCUMENT"},
                {"APPROVAL_VIEW",         "DOCUMENT"},

                // Meeting Management
                {"MEETING_VIEW",             "MEETING"},
                {"MEETING_CREATE",           "MEETING"},
                {"MEETING_UPDATE",           "MEETING"},
                {"MEETING_DELETE",           "MEETING"},
                {"MEETING_MANAGE_ATTENDEES", "MEETING"},
                {"MEETING_MARK_ATTENDANCE",  "MEETING"},
                {"MEETING_MINUTE_CREATE",    "MEETING"},
                {"MEETING_MINUTE_VIEW",      "MEETING"},
                {"ROOM_VIEW",                "MEETING"},
                {"ROOM_MANAGE",              "MEETING"},

                // Announcement
                {"ANNOUNCEMENT_VIEW",    "ANNOUNCEMENT"},
                {"ANNOUNCEMENT_CREATE",  "ANNOUNCEMENT"},
                {"ANNOUNCEMENT_UPDATE",  "ANNOUNCEMENT"},
                {"ANNOUNCEMENT_DELETE",  "ANNOUNCEMENT"},
                {"ANNOUNCEMENT_PUBLISH", "ANNOUNCEMENT"},

                // Report
                {"REPORT_VIEW",     "REPORT"},
                {"REPORT_EXPORT",   "REPORT"},
                {"REPORT_ADVANCED", "REPORT"},

                // System
                {"ATTACHMENT_UPLOAD",   "SYSTEM"},
                {"ATTACHMENT_DELETE",   "SYSTEM"},
                {"LOG_VIEW",            "SYSTEM"},
                {"LOOKUP_MANAGE",       "SYSTEM"},
                {"NOTIFICATION_SEND",   "SYSTEM"},
        };

        // Upsert: only insert permissions that don't already exist.
        for (String[] p : perms) {
            if (permissionRepository
                    .findByPermissionName(p[0])
                    .isEmpty()) {
                permissionRepository.save(
                        Permission.builder()
                                .permissionName(p[0])
                                .module(p[1])
                                .build());
            }
        }

        //ROLE ASSIGNMENTS
        // SUPER_ADMIN Full control
        roleRepository
                .findByRoleName("SUPER_ADMIN")
                .ifPresent(role -> {
                    var existing = new java.util.HashSet<>(
                            rolePermissionRepository
                                    .findPermissionNamesByRoleId(
                                            role.getRoleId()));
                    permissionRepository.findAll()
                            .forEach(perm -> {
                                if (!existing.contains(
                                        perm.getPermissionName())) {
                                    rolePermissionRepository.save(
                                            RolePermission.builder()
                                                    .role(role)
                                                    .permission(perm)
                                                    .build());
                                }
                            });
                });

        // ADMIN
        assignPermsToRole("ADMIN", new String[]{
                // User
                "USER_VIEW", "USER_CREATE",
                "USER_UPDATE", "USER_SUSPEND",
                "USER_RESET_PASSWORD",
                "ROLE_VIEW", "ROLE_ASSIGN",
                "PERMISSION_VIEW",
                // Officer
                "OFFICER_VIEW", "OFFICER_CREATE",
                "OFFICER_UPDATE", "OFFICER_DELETE",
                "OFFICER_VIEW_SENSITIVE",
                "CONTRACT_OFFICER_VIEW",
                "CONTRACT_OFFICER_CREATE",
                "CONTRACT_OFFICER_UPDATE",
                "CONTRACT_OFFICER_DELETE",
                "DEPARTMENT_VIEW", "DEPARTMENT_MANAGE",
                "POSITION_VIEW", "POSITION_MANAGE",
                // Document
                "DOCUMENT_VIEW", "DOCUMENT_VIEW_ALL",
                "DOCUMENT_CREATE", "DOCUMENT_UPDATE",
                "DOCUMENT_DELETE",
                "DOCUMENT_TYPE_MANAGE",
                "APPROVAL_REVIEW", "APPROVAL_VIEW",
                // Meeting
                "MEETING_VIEW", "MEETING_CREATE",
                "MEETING_UPDATE", "MEETING_DELETE",
                "MEETING_MANAGE_ATTENDEES",
                "MEETING_MARK_ATTENDANCE",
                "MEETING_MINUTE_CREATE",
                "MEETING_MINUTE_VIEW",
                "ROOM_VIEW", "ROOM_MANAGE",
                // Announcement
                "ANNOUNCEMENT_VIEW",
                "ANNOUNCEMENT_CREATE",
                "ANNOUNCEMENT_UPDATE",
                "ANNOUNCEMENT_DELETE",
                "ANNOUNCEMENT_PUBLISH",
                // Report
                "REPORT_VIEW", "REPORT_EXPORT",
                // System
                "ATTACHMENT_UPLOAD", "ATTACHMENT_DELETE",
                "LOG_VIEW", "NOTIFICATION_SEND",
        });

        // MANAGER
        assignPermsToRole("MANAGER", new String[]{
                // Officer
                "OFFICER_VIEW", "OFFICER_CREATE",
                "OFFICER_UPDATE",
                "OFFICER_VIEW_SENSITIVE",
                "CONTRACT_OFFICER_VIEW",
                "CONTRACT_OFFICER_CREATE",
                "CONTRACT_OFFICER_UPDATE",
                "DEPARTMENT_VIEW",
                "POSITION_VIEW",
                // Document
                "DOCUMENT_VIEW", "DOCUMENT_VIEW_ALL",
                "DOCUMENT_CREATE", "DOCUMENT_UPDATE",
                "APPROVAL_REVIEW", "APPROVAL_VIEW",
                // Meeting
                "MEETING_VIEW", "MEETING_CREATE",
                "MEETING_UPDATE",
                "MEETING_MANAGE_ATTENDEES",
                "MEETING_MARK_ATTENDANCE",
                "MEETING_MINUTE_CREATE",
                "MEETING_MINUTE_VIEW",
                "ROOM_VIEW",
                // Announcement
                "ANNOUNCEMENT_VIEW",
                "ANNOUNCEMENT_CREATE",
                "ANNOUNCEMENT_UPDATE",
                "ANNOUNCEMENT_PUBLISH",
                // Report
                "REPORT_VIEW", "REPORT_EXPORT",
                // System
                "ATTACHMENT_UPLOAD",
                "NOTIFICATION_SEND",
        });

        // OFFICER
        assignPermsToRole("OFFICER", new String[]{
                // Officer (self)
                "OFFICER_VIEW",
                // Document (own)
                "DOCUMENT_VIEW",
                "DOCUMENT_CREATE",
                "DOCUMENT_UPDATE",
                "DOCUMENT_DELETE",
                "APPROVAL_REQUEST",  // submit approval
                "APPROVAL_VIEW",
                // Meeting
                "MEETING_VIEW",
                "MEETING_MARK_ATTENDANCE",
                "MEETING_MINUTE_VIEW",
                "ROOM_VIEW",
                // Announcement
                "ANNOUNCEMENT_VIEW",
                // System
                "ATTACHMENT_UPLOAD",
        });

        // AUDITOR
        assignPermsToRole("AUDITOR", new String[]{
                "OFFICER_VIEW",
                "DOCUMENT_VIEW_ALL",
                "APPROVAL_VIEW",
                "MEETING_VIEW",
                "MEETING_MINUTE_VIEW",
                "ANNOUNCEMENT_VIEW",
                "REPORT_VIEW",
                "REPORT_EXPORT",
                "REPORT_ADVANCED",
                "LOG_VIEW",
        });

        log.info("✅ Permissions ({}) + Assignments",
                perms.length);
    }

    private void assignPermsToRole(
            String roleName, String[] permNames) {
        roleRepository.findByRoleName(roleName)
                .ifPresent(role -> {
                    var existing = new java.util.HashSet<>(
                            rolePermissionRepository
                                    .findPermissionNamesByRoleId(
                                            role.getRoleId()));
                    for (String permName : permNames) {
                        if (existing.contains(permName)) continue;
                        permissionRepository
                                .findByPermissionName(permName)
                                .ifPresent(perm ->
                                        rolePermissionRepository
                                                .save(
                                                        RolePermission.builder()
                                                                .role(role)
                                                                .permission(perm)
                                                                .build()));
                    }
                });
    }

    private void initSuperAdmin() {
        if (userRepository.existsByEmail(
                "admin@system.kh")) return;

        roleRepository
                .findByRoleName("SUPER_ADMIN")
                .ifPresent(role -> {
                    LookupUserStatus active =
                            userStatusRepo
                                    .findById(
                                            UserStatusCode.ACTIVE
                                                    .getCode())
                                    .orElse(null);

                    userRepository.save(
                            User.builder()
//                                    .uuid(UUID.randomUUID()
//                                            .toString())
                                    .userNameKh(
                                            "អ្នកគ្រប់គ្រងប្រព័ន្ធ")
                                    .userNameEn(
                                            "System Administrator")
                                    .email("admin@system.kh")
                                    .phone("012000000")
                                    .passwordHash(
                                            passwordEncoder.encode(
                                                    "Admin@1234"))
                                    .role(role)
                                    .statusCode(active)
                                    .mustChangePassword(false)
                                    .failedLoginCount(0)
                                    .build());

                    log.info(
                            "Super Admin: "
                                    + "admin@system.kh"
                                    + " | Pass: Admin@1234");
                });
    }
}