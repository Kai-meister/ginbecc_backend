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
                        "សកម្ម","Active","abc", 1},
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
//        if (permissionRepository.count() > 0) return;
        String[][] perms = {

                // User Management
                {"USER_VIEW",           "USER",   "មើលគណនីអ្នកប្រើ",           "អនុញ្ញាតឱ្យមើលព័ត៌មានគណនីអ្នកប្រើប្រាស់"},
                {"USER_CREATE",         "USER",   "បង្កើតគណនីអ្នកប្រើ",         "អនុញ្ញាតឱ្យបង្កើតគណនីអ្នកប្រើប្រាស់ថ្មី"},
                {"USER_UPDATE",         "USER",   "កែប្រែគណនីអ្នកប្រើ",         "អនុញ្ញាតឱ្យកែប្រែព័ត៌មានគណនីអ្នកប្រើប្រាស់"},
                {"USER_DELETE",         "USER",   "លុបគណនីអ្នកប្រើ",           "អនុញ្ញាតឱ្យលុបគណនីអ្នកប្រើប្រាស់"},
                {"USER_RESET_PASSWORD", "USER",   "កំណត់ពាក្យសម្ងាត់ឡើងវិញ",   "អនុញ្ញាតឱ្យកំណត់ពាក្យសម្ងាត់អ្នកប្រើប្រាស់ឡើងវិញ"},
                {"ROLE_VIEW",           "USER",   "មើលតួនាទី",                 "អនុញ្ញាតឱ្យមើលបញ្ជីតួនាទីទាំងអស់"},
                {"ROLE_ASSIGN",         "USER",   "ចាត់តាំងតួនាទី",             "អនុញ្ញាតឱ្យចាត់តាំងតួនាទីដល់អ្នកប្រើប្រាស់"},
                {"PERMISSION_VIEW",     "USER",   "មើលសិទ្ធិ",                 "អនុញ្ញាតឱ្យមើលបញ្ជីសិទ្ធិទាំងអស់"},
                {"PERMISSION_MANAGE",   "USER",   "គ្រប់គ្រងសិទ្ធិ",           "អនុញ្ញាតឱ្យគ្រប់គ្រង និងកំណត់សិទ្ធិប្រើប្រាស់"},

                // Officer Management
                {"OFFICER_VIEW",            "OFFICER", "មើលព័ត៌មានមន្ត្រី",         "អនុញ្ញាតឱ្យមើលព័ត៌មានមន្ត្រីរាជការ"},
                {"OFFICER_CREATE",          "OFFICER", "បន្ថែមមន្ត្រី",             "អនុញ្ញាតឱ្យបន្ថែមមន្ត្រីរាជការថ្មី"},
                {"OFFICER_UPDATE",          "OFFICER", "កែប្រែព័ត៌មានមន្ត្រី",       "អនុញ្ញាតឱ្យកែប្រែព័ត៌មានមន្ត្រីរាជការ"},
                {"OFFICER_DELETE",          "OFFICER", "លុបមន្ត្រី",               "អនុញ្ញាតឱ្យលុបព័ត៌មានមន្ត្រីរាជការ"},
                {"OFFICER_VIEW_SENSITIVE",  "OFFICER", "មើលព័ត៌មានសម្ងាត់មន្ត្រី",   "អនុញ្ញាតឱ្យមើលព័ត៌មានសម្ងាត់របស់មន្ត្រីរាជការ"},
                {"CONTRACT_OFFICER_VIEW",   "OFFICER", "មើលមន្ត្រីកិច្ចសន្យា",       "អនុញ្ញាតឱ្យមើលព័ត៌មានមន្ត្រីកិច្ចសន្យា"},
                {"CONTRACT_OFFICER_CREATE", "OFFICER", "បង្កើតមន្ត្រីកិច្ចសន្យា",     "អនុញ្ញាតឱ្យបង្កើតកំណត់ត្រាមន្ត្រីកិច្ចសន្យាថ្មី"},
                {"CONTRACT_OFFICER_UPDATE", "OFFICER", "កែប្រែកមន្ត្រីកិច្ចសន្យា",   "អនុញ្ញាតឱ្យកែប្រែព័ត៌មានមន្ត្រីកិច្ចសន្យា"},
                {"CONTRACT_OFFICER_DELETE", "OFFICER", "លុបមន្ត្រីកិច្ចសន្យា",       "អនុញ្ញាតឱ្យលុបកំណត់ត្រាមន្ត្រីកិច្ចសន្យា"},
                {"DEPARTMENT_VIEW",         "OFFICER", "មើលផ្នែក",               "អនុញ្ញាតឱ្យមើលបញ្ជីផ្នែកទាំងអស់"},
                {"DEPARTMENT_MANAGE",       "OFFICER", "គ្រប់គ្រងផ្នែក",         "អនុញ្ញាតឱ្យគ្រប់គ្រង បន្ថែម និងកែប្រែផ្នែក"},
                {"OFFICE_VIEW",            "OFFICER", "មើលផ្នែកកិរិយាល័យ",            "អនុញ្ញាតឱ្យមើលបញ្ជីផ្នែកកិរិយាល័យទាំងអស់"},
                {"OFFICE_MANAGE",          "OFFICER", "គ្រប់គ្រងផ្នែកកិរិយាល័យ",         "អនុញ្ញាតឱ្យគ្រប់គ្រង បន្ថែម និងកែប្រែផ្នែកកិរិយាល័យ"},
                {"POSITION_VIEW",           "OFFICER", "មើលតំណែង",              "អនុញ្ញាតឱ្យមើលបញ្ជីតំណែងទាំងអស់"},
                {"POSITION_MANAGE",         "OFFICER", "គ្រប់គ្រងតំណែង",          "អនុញ្ញាតឱ្យគ្រប់គ្រង បន្ថែម និងកែប្រែតំណែង"},

                // Document Management
                {"DOCUMENT_VIEW",        "DOCUMENT", "មើលឯកសារ",             "អនុញ្ញាតឱ្យមើលឯកសាររបស់ខ្លួន"},
                {"DOCUMENT_VIEW_ALL",    "DOCUMENT", "មើលឯកសារទាំងអស់",       "អនុញ្ញាតឱ្យមើលឯកសាររបស់អ្នកប្រើប្រាស់ទាំងអស់"},
                {"DOCUMENT_CREATE",      "DOCUMENT", "បង្កើតឯកសារ",           "អនុញ្ញាតឱ្យបង្កើតឯកសារថ្មី"},
                {"DOCUMENT_UPDATE",      "DOCUMENT", "កែប្រែឯកសារ",           "អនុញ្ញាតឱ្យកែប្រែមាតិកាឯកសារ"},
                {"DOCUMENT_DELETE",      "DOCUMENT", "លុបឯកសារ",             "អនុញ្ញាតឱ្យលុបឯកសារ"},
                {"DOCUMENT_TYPE_MANAGE", "DOCUMENT", "គ្រប់គ្រងប្រភេទឯកសារ",   "អនុញ្ញាតឱ្យគ្រប់គ្រងប្រភេទ និងប្រភេទរងឯកសារ"},
                {"APPROVAL_REQUEST",     "DOCUMENT", "ស្នើសុំការអនុម័ត",       "អនុញ្ញាតឱ្យស្នើសុំការអនុម័តឯកសារ"},
                {"APPROVAL_REVIEW",      "DOCUMENT", "ពិនិត្យការអនុម័ត",       "អនុញ្ញាតឱ្យពិនិត្យ និងសម្រេចលើការអនុម័តឯកសារ"},
                {"APPROVAL_VIEW",        "DOCUMENT", "មើលស្ថានភាពការអនុម័ត",   "អនុញ្ញាតឱ្យតាមដានស្ថានភាពការអនុម័ត"},

                // Meeting Management
                {"MEETING_VIEW",             "MEETING", "មើលកិច្ចប្រជុំ",         "អនុញ្ញាតឱ្យមើលបញ្ជីកិច្ចប្រជុំ"},
                {"MEETING_CREATE",           "MEETING", "បង្កើតកិច្ចប្រជុំ",       "អនុញ្ញាតឱ្យបង្កើតកិច្ចប្រជុំថ្មី"},
                {"MEETING_UPDATE",           "MEETING", "កែប្រែកិច្ចប្រជុំ",       "អនុញ្ញាតឱ្យកែប្រែព័ត៌មានកិច្ចប្រជុំ"},
                {"MEETING_DELETE",           "MEETING", "លុបកិច្ចប្រជុំ",         "អនុញ្ញាតឱ្យលុបកិច្ចប្រជុំ"},
                {"MEETING_MANAGE_ATTENDEES", "MEETING", "គ្រប់គ្រងអ្នកចូលរួម",     "អនុញ្ញាតឱ្យបន្ថែម និងលុបអ្នកចូលរួមកិច្ចប្រជុំ"},
                {"MEETING_MARK_ATTENDANCE",  "MEETING", "កត់ត្រាការចូលរួម",       "អនុញ្ញាតឱ្យកត់ត្រាវត្តមានអ្នកចូលរួមកិច្ចប្រជុំ"},
                {"MEETING_MINUTE_CREATE",    "MEETING", "បង្កើតកំណត់ហេតុប្រជុំ",   "អនុញ្ញាតឱ្យបង្កើត និងកែប្រែកំណត់ហេតុប្រជុំ"},
                {"MEETING_MINUTE_VIEW",      "MEETING", "មើលកំណត់ហេតុប្រជុំ",     "អនុញ្ញាតឱ្យមើលកំណត់ហេតុប្រជុំ"},
                {"ROOM_VIEW",                "MEETING", "មើលបន្ទប់ប្រជុំ",         "អនុញ្ញាតឱ្យមើលបញ្ជី និងព័ត៌មានបន្ទប់ប្រជុំ"},
                {"ROOM_MANAGE",              "MEETING", "គ្រប់គ្រងបន្ទប់ប្រជុំ",   "អនុញ្ញាតឱ្យគ្រប់គ្រង បន្ថែម និងកែប្រែបន្ទប់ប្រជុំ"},

                // Announcement
                {"ANNOUNCEMENT_VIEW",    "ANNOUNCEMENT", "មើលសេចក្តីប្រកាស",         "អនុញ្ញាតឱ្យមើលសេចក្តីប្រកាសដែលបានផ្សព្វផ្សាយ"},
                {"ANNOUNCEMENT_CREATE",  "ANNOUNCEMENT", "បង្កើតសេចក្តីប្រកាស",       "អនុញ្ញាតឱ្យបង្កើតសេចក្តីប្រកាសថ្មី"},
                {"ANNOUNCEMENT_UPDATE",  "ANNOUNCEMENT", "កែប្រែសេចក្តីប្រកាស",       "អនុញ្ញាតឱ្យកែប្រែមាតិកាសេចក្តីប្រកាស"},
                {"ANNOUNCEMENT_DELETE",  "ANNOUNCEMENT", "លុបសេចក្តីប្រកាស",         "អនុញ្ញាតឱ្យលុបសេចក្តីប្រកាស"},
                {"ANNOUNCEMENT_PUBLISH", "ANNOUNCEMENT", "ផ្សព្វផ្សាយសេចក្តីប្រកាស", "អនុញ្ញាតឱ្យផ្សព្វផ្សាយសេចក្តីប្រកាសដល់អ្នកប្រើប្រាស់"},

                // Report
                {"REPORT_VIEW",     "REPORT", "មើលរបាយការណ៍",         "អនុញ្ញាតឱ្យមើលរបាយការណ៍ទូទៅ"},
                {"REPORT_EXPORT",   "REPORT", "នាំចេញរបាយការណ៍",       "អនុញ្ញាតឱ្យនាំចេញរបាយការណ៍ជា Excel "},
                {"REPORT_ADVANCED", "REPORT", "របាយការណ៍កម្រិតខ្ពស់",   "អនុញ្ញាតឱ្យចូលប្រើរបាយការណ៍វិភាគកម្រិតខ្ពស់"},

                // System
                {"ATTACHMENT_UPLOAD", "SYSTEM", "បញ្ចូលឯកសារភ្ជាប់", "អនុញ្ញាតឱ្យបញ្ចូលឯកសារភ្ជាប់ក្នុងប្រព័ន្ធ"},
                {"ATTACHMENT_DELETE", "SYSTEM", "លុបឯកសារភ្ជាប់",   "អនុញ្ញាតឱ្យលុបឯកសារភ្ជាប់ចេញពីប្រព័ន្ធ"},
                {"LOG_VIEW",          "SYSTEM", "មើលកំណត់ហេតុប្រព័ន្ធ", "អនុញ្ញាតឱ្យមើលកំណត់ហេតុសកម្មភាពប្រព័ន្ធ"},
                {"LOOKUP_MANAGE",     "SYSTEM", "គ្រប់គ្រងទិន្នន័យយោង", "អនុញ្ញាតឱ្យគ្រប់គ្រងទិន្នន័យយោងប្រព័ន្ធ"},
                {"NOTIFICATION_SEND", "SYSTEM", "ផ្ញើការជូនដំណឹង",     "អនុញ្ញាតឱ្យផ្ញើការជូនដំណឹងដល់អ្នកប្រើប្រាស់"},
        };

        for (String[] p : perms) {
            if (permissionRepository.findByPermissionName(p[0]).isEmpty()) {
            permissionRepository.save(
                    Permission.builder()
                            .permissionName(p[0])
                            .module(p[1])
                            .displayNameKh(p[2])
                            .description(p[3])
                            .build());
        }
            }

        //ROLE ASSIGNMENTS
        // SUPER_ADMIN Full control
        roleRepository.findByRoleName("SUPER_ADMIN")
                .ifPresent(role ->
                        permissionRepository.findAll().forEach(perm -> {
                            if (!rolePermissionRepository.existsByRoleAndPermission(role, perm)) {
                                rolePermissionRepository.save(
                                        RolePermission.builder()
                                                .role(role)
                                                .permission(perm)
                                                .build());
                            }
                        }));


        // ADMIN
        assignPermsToRole("ADMIN", new String[]{
                // User
                "USER_VIEW",
                "USER_CREATE",
                "USER_UPDATE",
                "USER_SUSPEND",
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
                "DEPARTMENT_VIEW",
                "DEPARTMENT_MANAGE",
                "OFFICE_VIEW",
                "OFFICE_MANAGE",

                "POSITION_VIEW", "POSITION_MANAGE",
                // Document
                "DOCUMENT_VIEW",
                "DOCUMENT_VIEW_ALL",
                "DOCUMENT_CREATE",
                "DOCUMENT_UPDATE",
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
                "REPORT_VIEW",
                "REPORT_EXPORT",

                // System
                "ATTACHMENT_UPLOAD",
                "ATTACHMENT_DELETE",
                "LOG_VIEW",
                "NOTIFICATION_SEND",
                "LOOKUP_MANAGE",
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
                "OFFICE_VIEW",
                "POSITION_VIEW",
                // Document
                "DOCUMENT_VIEW",
                "DOCUMENT_VIEW_ALL",
                "DOCUMENT_CREATE",
                "DOCUMENT_UPDATE",
                "APPROVAL_REVIEW",
                "APPROVAL_VIEW",
                // Meeting
                "MEETING_VIEW",
                "MEETING_CREATE",
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
                "REPORT_VIEW",
                "REPORT_EXPORT",
                // System
                "ATTACHMENT_UPLOAD",
                "NOTIFICATION_SEND",
                "LOOKUP_MANAGE",
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

        log.info(" Permissions ({}) + Assignments",
                perms.length);
    }

    private void assignPermsToRole(
            String roleName, String[] permNames) {
        roleRepository.findByRoleName(roleName)
                .ifPresent(role -> {
                    for (String permName : permNames) {
                        permissionRepository
                                .findByPermissionName(permName)
                                .ifPresent(perm -> {
                                    if (!rolePermissionRepository.existsByRoleAndPermission(role, perm)) {
                                        rolePermissionRepository.save(
                                                RolePermission.builder()
                                                        .role(role)
                                                        .permission(perm)
                                                        .build());
                                    }
                                });
                    }
                });
    }

    private void initSuperAdmin() {
        if (userRepository.existsByEmail(
                "system.supperadmin@inspectorate.gov.kh")) return;

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
                                    .userNameKh(
                                            "អ្នកគ្រប់គ្រងប្រព័ន្ធ")
                                    .userNameEn(
                                            "System Administrator")
                                    .email("system.supperadmin@inspectorate.gov.kh")
                                    .phone(null)
                                    .passwordHash(
                                            passwordEncoder.encode(
                                                    "Inspectorate@936396$"))
                                    .role(role)
                                    .statusCode(active)
                                    .failedLoginCount(0)
                                    .build());

                    log.info(
                            "Super Admin: "
                                    + "system.supperadmin@inspectorate.gov.kh");
                });
    }
}
