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
        if (permissionRepository.count() > 0) return;

        String[][] perms = {
                {"USER_CREATE","USER"},
                {"USER_UPDATE","USER"},
                {"USER_DELETE","USER"},
                {"USER_SUSPEND","USER"},
                {"ROLE_ASSIGN","USER"},
                {"OFFICER_MANAGE", "OFFICER"},
                {"SELF_PROFILE_VIEW","OFFICER"},
                {"SELF_PROFILE_EDIT", "OFFICER"},
                {"DOC_APPROVE","DOCUMENT"},
                {"DOC_VIEW_OWN","DOCUMENT"},
                {"MEETING_MANAGE","MEETING"},
                {"MEETING_VIEW","MEETING"},
                {"MEETING_ATTEND","MEETING"},
                {"ANNOUNCEMENT_VIEW","ANNOUNCEMENT"},
                {"ANNOUNCEMENT_CREATE","ANNOUNCEMENT"},
                {"ANNOUNCEMENT_UPDATE","ANNOUNCEMENT"},
                {"ANNOUNCEMENT_DELETE", "ANNOUNCEMENT"},
                {"ANNOUNCEMENT_PUBLISH","ANNOUNCEMENT"},
                {"ATTACHMENT_UPLOAD","ATTACHMENT"},
                {"ATTACHMENT_MANAGE","ATTACHMENT"},
                {"AUDIT_VIEW", "AUDIT"},
                {"REPORT_EXPORT", "REPORT"},
                {"DAILY_REPORT","REPORT"},
                {"DASHBOARD_OWN","DASHBOARD"},
                {"NOTIFICATION","NOTIFICATION"},
        };

        for (String[] p : perms) {
            permissionRepository.save(
                    Permission.builder()
                            .permissionName(p[0])
                            .module(p[1])
                            .build());
        }

        roleRepository
                .findByRoleName("SUPER_ADMIN")
                .ifPresent(role ->
                        permissionRepository.findAll()
                                .forEach(perm ->
                                        rolePermissionRepository
                                                .save(
                                                        RolePermission.builder()
                                                                .role(role)
                                                                .permission(perm)
                                                                .build())));

        assignPermsToRole("ADMIN", new String[]{
                "OFFICER_MANAGE","ATTACHMENT_UPLOAD",
                "ATTACHMENT_MANAGE","DOC_APPROVE",
                "MEETING_MANAGE","MEETING_VIEW",
                "ANNOUNCEMENT_VIEW","ANNOUNCEMENT_CREATE",
                "ANNOUNCEMENT_UPDATE",
                "ANNOUNCEMENT_PUBLISH",
                "DAILY_REPORT","DASHBOARD_OWN",
                "NOTIFICATION","USER_CREATE",
                "USER_UPDATE","USER_SUSPEND"});

        assignPermsToRole("MANAGER", new String[]{
                "OFFICER_MANAGE","DOC_APPROVE",
                "MEETING_MANAGE","MEETING_VIEW",
                "ANNOUNCEMENT_VIEW","ANNOUNCEMENT_CREATE",
                "ANNOUNCEMENT_UPDATE",
                "ANNOUNCEMENT_PUBLISH",
                "DAILY_REPORT","DASHBOARD_OWN",
                "NOTIFICATION","ATTACHMENT_UPLOAD",
                "ATTACHMENT_MANAGE"});

        assignPermsToRole("OFFICER", new String[]{
                "SELF_PROFILE_VIEW",
                "SELF_PROFILE_EDIT",
                "MEETING_VIEW",
                "MEETING_ATTEND",
                "DOC_VIEW_OWN",
                "DASHBOARD_OWN",
                "NOTIFICATION",
                "ANNOUNCEMENT_VIEW"});

        assignPermsToRole("AUDITOR", new String[]{
                "AUDIT_VIEW","REPORT_EXPORT",
                "DASHBOARD_OWN","NOTIFICATION"});

        log.info("Permissions (25) + Assignments");
    }

    private void assignPermsToRole(
            String roleName, String[] permNames) {
        roleRepository.findByRoleName(roleName)
                .ifPresent(role -> {
                    for (String permName : permNames) {
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
                                    .uuid(UUID.randomUUID()
                                            .toString())
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