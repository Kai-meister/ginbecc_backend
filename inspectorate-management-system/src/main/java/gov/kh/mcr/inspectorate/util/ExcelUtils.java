package gov.kh.mcr.inspectorate.util;

import gov.kh.mcr.inspectorate.dto.response.report.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import java.io.ByteArrayOutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public final class ExcelUtils {

    private ExcelUtils() {}

    private static final DateTimeFormatter D =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DT =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm");

    private static final String MINISTRY_NAME =
            "ក្រសួងធម្មការនិងសាសនា";
    private static final String ORG_NAME =
            "អគ្គាធិការដ្ឋានពុទ្ធិកសិក្សាជាតិ";
    private static final String KINGDOM_NAME =
            "ព្រះរាជាណាចក្រកម្ពុជា";
    private static final String KINGDOM_MOTTO =
            "ជាតិ សាសនា ព្រះមហាក្សត្រ";

    private static final byte[] WHITE =
            hex("#FFFFFF");

    private static final byte[] BLACK =
            hex("#000000");
    private static final byte[] LIGHT_GRAY =
            hex("#D9E1F2");

    public static byte[] officers(
            List<OfficerReportResponse> list,
            Integer deptId,
            String status,
            LocalDate from,
            LocalDate to) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("របាយការណ៍មន្ត្រីរាជការ");
            Styles s = new Styles(wb);
            int cols = 10;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);

            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍មន្ត្រីរាជការ");

            r = filterBar(sh, wb, r, cols,
                    null, status, from, to);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ",
                    "លេខសម្គាល់",
                    "គោត្តនាម ឈ្មោះ",
                    "ភេទ",
                    "ថ្ងៃខែឆ្នាំកំណើត",
                    "អាយុ",
                    "នាយកដ្ឋាន",
                    "មុខតំណែង",
                    "ថ្ងៃចូលបម្រើការងារ",
                    "ស្ថានភាព"
            });

            for (var o : list) {
                Row row = sh.createRow(r++);
                row.setHeightInPoints(22);
                CellStyle cs = alt(s, o.getNo());
                c(row, 0,  o.getNo()+"",          cs);
                c(row, 1,  nv(o.getOfficerCode()), cs);
                c(row, 2,  nv(o.getFullNameKh()),  cs);
                c(row, 3,  nv(o.getGenderLabel()), cs);
                c(row, 4,  d(o.getDob()),           cs);
                c(row, 5,  o.getAge() != null
                        ? o.getAge()+" ឆ្នាំ" : "",  cs);
                c(row, 6,  nv(o.getDepartmentName()),cs);
                c(row, 7,  nv(o.getPositionName()), cs);
                c(row, 8,  d(o.getJoinDate()),      cs);
                c(row, 9,  nv(o.getStatusLabel()),  cs);
            }

            summaryBar(sh, wb, r, cols,
                    "ចំនួនមន្ត្រីសរុប: " + list.size() + " នាក់");

            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "មានបញ្ហាក្នុងការបង្កើតរបាយការណ៍មន្ត្រី "
                            + e.getMessage());
        }
    }

    public static byte[] contractOfficers(
            List<ContractOfficerReportResponse> list,
            Integer days) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("របាយការណ៍មន្ត្រីជាប់កិច្ចសន្យា");
            Styles s = new Styles(wb);
            int cols = 11;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍មន្ត្រីជាប់កិច្ចសន្យា");
            r = filterBar(sh, wb, r, cols,
                    "ជិតផុតកំណត់ក្នុងរយៈពេល: "
                            + (days != null ? days : 30)
                            + " ថ្ងៃ",
                    null, null, null);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "លេខសម្គាល់",
                    "គោត្តនាម និងនាម",
                    "ភេទ", "ថ្ងៃខែឆ្នាំកំណើត",
                    "អាយុ", "នាយកដ្ឋាន",
                    "ថ្ងៃចាប់ផ្ដើម", "ថ្ងៃផុតកំណត់",
                    "ចំនួនថ្ងៃនៅសល់",
                    "លេខកូដគណនេយ្យ"
            });

            for (var c2 : list) {
                Row row = sh.createRow(r++);
                row.setHeightInPoints(22);
                long daysLeft =
                        c2.getDaysUntilExpiry() != null
                                ? c2.getDaysUntilExpiry()
                                : 0;
                CellStyle cs = daysLeft <= 7
                        ? s.warn : alt(s, c2.getNo());

                c(row, 0,  c2.getNo()+"",                cs);
                c(row, 1,  nv(c2.getContractOfficerCode()),cs);
                c(row, 2,  nv(c2.getFullNameKh()),        cs);
                c(row, 3,  nv(c2.getGenderLabel()),       cs);
                c(row, 4,  d(c2.getDob()),                cs);
                c(row, 5,  c2.getAge() != null
                        ? c2.getAge()+" ឆ្នាំ" : "",      cs);
                c(row, 6,  nv(c2.getDepartmentName()),    cs);
                c(row, 7,  d(c2.getStartDate()),          cs);
                c(row, 8,  d(c2.getEndDate()),            cs);
                c(row, 9,  nv(c2.getExpiryLabel()),       cs);
                c(row, 10, nv(c2.getAccountingCode()),    cs);
            }

            summaryBar(sh, wb, r, cols,
                    "ចំនួនមន្ត្រីសរុប: " + list.size() + " នាក់");
            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "មានបញ្ហាក្នុងការបង្កើតរបាយការណ៍មន្ត្រីជាប់កិច្ចសន្យា "
                            + e.getMessage());
        }
    }

    public static byte[] documents(
            List<DocumentReportResponse> list) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh = wb.createSheet("ឯកសារ");
            Styles s = new Styles(wb);
            int cols = 9;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);          // ✅ ជំនួស title()
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍ឯកសារ");
            r = filterBar(sh, wb, r, cols,
                    null, null, null, null);
            r = colHeaders(sh, s, r, new String[]{    // ✅ ជំនួស headers()
                    "ល.រ", "លេខឯកសារ",
                    "ឈ្មោះឯកសារ", "ប្រភេទ",
                    "User", "នាយកដ្ឋាន",
                    "ផុតកំណត់", "ស្ថានភាព",
                    "ថ្ងៃបង្កើត"
            });

            for (var d : list) {
                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs = Boolean.TRUE.equals(
                        d.getIsExpired())
                        ? s.warn : alt(s, d.getNo());

                c(row, 0, d.getNo()+"",              cs);
                c(row, 1, nv(d.getDocumentNumber()), cs);
                c(row, 2, nv(d.getDocumentName()),   cs);
                c(row, 3, nv(d.getDocumentTypeName()),cs);
                c(row, 4, nv(d.getUserName()),        cs);
                c(row, 5, nv(d.getDepartmentName()),  cs);
                c(row, 6, d(d.getExpiryDate()),       cs); // ✅ ជំនួស d2()
                c(row, 7, nv(d.getStatusLabel()),     cs);
                c(row, 8, dt(d.getCreatedAt()),       cs);
            }

            long expired = list.stream()
                    .filter(d -> Boolean.TRUE.equals(
                            d.getIsExpired()))
                    .count();

            summaryBar(sh, wb, r, cols,
                    "សរុប: " + list.size()
                            + " | ផុតកំណត់: " + expired);
            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Document report: " + e.getMessage());
        }
    }


    public static byte[] approvals(
            List<ApprovalReportResponse> list) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh = wb.createSheet("Approvals");
            Styles s = new Styles(wb);
            int cols = 10;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍ការអនុម័ត");
            r = filterBar(sh, wb, r, cols,
                    null, null, null, null);
            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ",
                    "ឈ្មោះឯកសារ",
                    "អ្នកស្នើ",
                    "នាយកដ្ឋានស្នើ",
                    "នាយកដ្ឋាន Decide",
                    "អនុម័តដោយ",
                    "ស្ថានភាព",
                    "ហេតុផល",
                    "ថ្ងៃស្នើ",
                    "ថ្ងៃសម្រេច"
            });

            long approved = 0, rejected = 0;

            for (var a : list) {
                if ("APPROVED".equals(a.getStatusCode()))
                    approved++;
                else if ("REJECTED".equals(a.getStatusCode()))
                    rejected++;

                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs = "REJECTED".equals(
                        a.getStatusCode())
                        ? s.warn : alt(s, a.getNo());

                c(row, 0, a.getNo()+"",              cs);
                c(row, 1, nv(a.getDocumentName()),   cs);
                c(row, 2, nv(a.getRequesterName()),  cs);
                c(row, 3, nv(a.getRequesterDept()),  cs);
                c(row, 4, nv(a.getDepartmentName()), cs);
                c(row, 5, nv(a.getApprovedBy()),     cs);
                c(row, 6, nv(a.getStatusLabel()),    cs);
                c(row, 7, a.getComment() != null
                        ? a.getComment() : "",        cs);
                c(row, 8, dt(a.getRequestedAt()),    cs);
                c(row, 9, dt(a.getDecidedAt()),   cs);
            }

            summaryBar(sh, wb, r, cols,
                    "សរុប: " + list.size()
                            + " | អនុម័ត: " + approved
                            + " | បដិសេធ: " + rejected);
            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Approval report: " + e.getMessage());
        }
    }

    public static byte[] meetings(
            List<MeetingReportResponse> list,
            int month, int year,
            String status) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("របាយការណ៍កិច្ចប្រជុំ");
            Styles s = new Styles(wb);
            int cols = 9;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍កិច្ចប្រជុំ - ខែ "
                            + String.format("%02d", month)
                            + "/" + year);
            r = filterBar(sh, wb, r, cols,
                    null, status, null, null);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "ប្រធានបទកិច្ចប្រជុំ",
                    "ប្រភេទ", "កាលបរិច្ឆេទប្រជុំ",
                    "ម៉ោង", "បន្ទប់",
                    "អ្នករៀបចំ",
                    "ចំនួនវត្តមាន/សរុប",
                    "ស្ថានភាព"
            });

            for (var m : list) {
                Row row = sh.createRow(r++);
                row.setHeightInPoints(22);
                CellStyle cs = alt(s, m.getNo());

                c(row, 0, m.getNo()+"",             cs);
                c(row, 1, nv(m.getTitle()),          cs);
                c(row, 2, nv(m.getMeetingType()),    cs);
                c(row, 3, d(m.getMeetingDate()),     cs);
                c(row, 4, (m.getStartTime() != null
                        ? m.getStartTime().toString()
                        : "") + " - "
                        + (m.getEndTime() != null
                        ? m.getEndTime().toString()
                        : ""),                        cs);
                c(row, 5, nv(m.getRoomCode()),       cs);
                c(row, 6, nv(m.getOrganizerName()),  cs);
                c(row, 7, m.getAttendedCount()
                        + "/" + m.getTotalAttendees(), cs);
                c(row, 8, nv(m.getStatusLabel()),    cs);
            }

            summaryBar(sh, wb, r, cols,
                    "ចំនួនកិច្ចប្រជុំសរុប: " + list.size() + " ប្រជុំ");
            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "មានបញ្ហាក្នុងការបង្កើតរបាយការណ៍កិច្ចប្រជុំ "
                            + e.getMessage());
        }
    }

    public static byte[] meetingMinutes(
            List<MeetingMinuteReportResponse> list) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("របាយការណ៍កំណត់ហេតុប្រជុំ");
            Styles s = new Styles(wb);
            int cols = 9;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍កំណត់ហេតុប្រជុំ");
            r = filterBar(sh, wb, r, cols,
                    null, null, null, null);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "ការប្រជុំ",
                    "ថ្ងៃប្រជុំ", "សង្ខេប",
                    "ការសម្រេច",
                    "ចំណុចសម្រេច",
                    "កត់ហេតុដោយ", "ឯកសារភ្ជាប់",
                    "ថ្ងៃបង្កើត"
            });

            for (var m : list) {
                Row row = sh.createRow(r++);
                row.setHeightInPoints(30);
                CellStyle cs = alt(s, m.getNo());

                c(row, 0, m.getNo()+"",               cs);
                c(row, 1, nv(m.getMeetingTitle()),    cs);
                c(row, 2, d(m.getMeetingDate()),      cs);
                c(row, 3, trunc(m.getSummary(), 80),  cs);
                c(row, 4, trunc(m.getDecisions(), 80),cs);
                c(row, 5, trunc(m.getActionItems(), 80), cs);
                c(row, 6, nv(m.getRecordedBy()),      cs);
                c(row, 7, Boolean.TRUE.equals(
                        m.getHasAttachment())
                        ? "." : "",                    cs);
                c(row, 8, dt(m.getCreatedAt()),        cs);
            }

            summaryBar(sh, wb, r, cols,
                    "សរុប: " + list.size()
                            + " ចំនួនកំណត់ហេតុប្រជុំសរុប");
            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "មានបញ្ហាក្នុងការបង្កើតរបាយការណ៍កំណត់ហេតុប្រជុំ "
                            + e.getMessage());
        }
    }

    public static byte[] announcements(
            List<AnnouncementReportResponse> list,
            String status,
            String priority,
            LocalDate from,
            LocalDate to) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("របាយការណ៍សេចក្ដីប្រកាស");
            Styles s = new Styles(wb);
            int cols = 8;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍សេចក្ដីប្រកាស");
            r = filterBar(sh, wb, r, cols,
                    priority != null
                            ? "អាទិភាព: " + priority
                            : null,
                    status, from, to);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "ចំណងជើង",
                    "អ្នកបង្កើត", "អាទិភាព",
                    "ស្ថានភាព", "ចំនួនអ្នកទទួល",
                    "អាន", "មិនទាន់អាន"
            });

            for (var a : list) {
                Row row = sh.createRow(r++);
                row.setHeightInPoints(22);
                CellStyle cs = alt(s, a.getNo());

                c(row, 0, a.getNo()+"",                cs);
                c(row, 1, nv(a.getTitle()),             cs);
                c(row, 2, nv(a.getCreatedBy()),         cs);
                c(row, 3, nv(a.getPriorityLabel()),     cs);
                c(row, 4, nv(a.getStatusLabel()),       cs);
                c(row, 5, a.getTotalRecipients()+"",    cs);
                c(row, 6, a.getReadCount()+"",          cs);
                c(row, 7, a.getUnreadCount()+"",        cs);
            }

            summaryBar(sh, wb, r, cols,
                    "ចំនួនសេចក្ដីប្រកាសសរុប: " + list.size() + " ប្រកាស");
            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "មានបញ្ហាក្នុងការបង្កើតរបាយការណ៍សេចក្ដីប្រកាស: "
                            + e.getMessage());
        }
    }
    public static byte[] announcementRecipients(
            List<AnnouncementRecipientReportResponse> list) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("របាយការណ៍អ្នកទទួលប្រកាស");
            Styles s = new Styles(wb);
            int cols = 7;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍អ្នកទទួលប្រកាស");
            r = filterBar(sh, wb, r, cols,
                    null, null, null, null);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "ប្រកាស",
                    "អ្នកទទួល", "អ៊ីមែល",
                    "នាយកដ្ឋាន", "ស្ថានភាព",
                    "ថ្ងៃអាន",
                    "ថ្ងៃទទួល"
            });

            for (var a : list) {
                Row row = sh.createRow(r++);
                row.setHeightInPoints(22);
                CellStyle cs = Boolean.TRUE.equals(a.getIsRead())
                        ? alt(s, a.getNo()) : s.warn;

                c(row, 0, a.getNo() + "",                cs);
                c(row, 1, nv(a.getAnnouncementTitle()),  cs);
                c(row, 2, nv(a.getReceiverName()),       cs);
                c(row, 3, nv(a.getReceiverEmail()),      cs);
                c(row, 4, nv(a.getDepartmentName()),     cs);
                c(row, 5, nv(a.getReadStatus()),         cs);
                c(row, 6, dt(a.getReadAt()),             cs);
                c(row, 7, dt(a.getCreatedAt()),       cs);
            }

            long read = list.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsRead()))
                    .count();

            summaryBar(sh, wb, r, cols,
                    "សរុប: " + list.size()
                            + "  |  អានរួច: " + read
                            + "  |  មិនទាន់អាន: "
                            + (list.size() - read));
            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "មានបញ្ហាក្នុងការបង្កើតរបាយការណ៍អ្នកទទួលប្រកាស: "
                            + e.getMessage());
        }
    }

    public static byte[] auditLogs(
            List<AuditLogReportResponse> list,
            String action,
            String entityType,
            LocalDate from,
            LocalDate to) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("របាយការណ៍ប្រវត្តិសកម្មភាព");
            Styles s = new Styles(wb);
            int cols = 7;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍ប្រវត្តិសកម្មភាព");
            r = filterBar(sh, wb, r, cols,
                    (action != null
                            ? "សកម្មភាព: " + action
                            : null),
                    entityType, from, to);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "អ្នកប្រើប្រាស់",
                    "អ៊ីមែល", "សកម្មភាព",
                    "ប្រភេទទិន្នន័យ", "អាសយដ្ឋាន IP",
                    "កាលបរិច្ឆេទ"
            });

            for (var l : list) {
                Row row = sh.createRow(r++);
                row.setHeightInPoints(22);
                CellStyle cs = alt(s, l.getNo());

                c(row, 0, l.getNo()+"",          cs);
                c(row, 1, nv(l.getUserNameKh()), cs);
                c(row, 2, nv(l.getUserEmail()),  cs);
                c(row, 3, nv(l.getActionLabel()),cs);
                c(row, 4, (nv(l.getEntityType()))
                        + (l.getEntityId() != null
                        ? " #"+l.getEntityId()
                        : ""),                   cs);
                c(row, 5, nv(l.getIpAddress()), cs);
                c(row, 6, dt(l.getCreatedAt()), cs);
            }

            summaryBar(sh, wb, r, cols,
                    "ចំនួនកំណត់ត្រាសកម្មភាពសរុប: " + list.size() + " Log");
            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "មានបញ្ហាក្នុងការបង្កើតរបាយការណ៍ប្រវត្តិសកម្មភាព: "
                            + e.getMessage());
        }
    }

    public static byte[] notifications(
            List<NotificationReportResponse> list,
            String type,
            LocalDate from,
            LocalDate to) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("របាយការណ៍ការជូនដំណឹង");
            Styles s = new Styles(wb);
            int cols = 7;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍ការជូនដំណឹង");
            r = filterBar(sh, wb, r, cols,
                    type != null
                            ? "ប្រភេទ: " + type
                            : null,
                    null, from, to);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "ទទួលដោយ",
                    "ចំណងជើង", "ប្រភេទ",
                    "ស្ថានភាព", "កាលបរិច្ឆេទបង្កើត",
                    "កាលបរិច្ឆេទអាន"
            });

            long read = list.stream()
                    .filter(n -> Boolean.TRUE.equals(
                            n.getIsRead()))
                    .count();

            for (var n : list) {
                Row row = sh.createRow(r++);
                row.setHeightInPoints(22);
                CellStyle cs = alt(s, n.getNo());

                c(row, 0, n.getNo()+"",          cs);
                c(row, 1, nv(n.getReceiverName()),cs);
                c(row, 2, nv(n.getTitle()),      cs);
                c(row, 3, nv(n.getTypeLabel()),  cs);
                c(row, 4, nv(n.getReadStatus()), cs);
                c(row, 5, dt(n.getCreatedAt()),  cs);
                c(row, 6, dt(n.getReadAt()),     cs);
            }

            summaryBar(sh, wb, r, cols,
                    "សរុប: " + list.size()
                            + "  |  អានរួច: " + read
                            + "  |  មិនទាន់អាន: "
                            + (list.size() - read));
            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "មានបញ្ហាក្នុងការបង្កើតរបាយការណ៍ការជូនដំណឹង: "
                            + e.getMessage());
        }
    }

    public static byte[] users(
            List<UserReportResponse> list,
            String status) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh = wb.createSheet("របាយការណ៍អ្នកប្រើប្រាស់");
            Styles s = new Styles(wb);
            int cols = 8;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍អ្នកប្រើប្រាស់");
            r = filterBar(sh, wb, r, cols,
                    null, status, null, null);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "ឈឈ្មោះជាភាសាខ្មែរ",
                    "អ៊ីមែល", "លេខទូរសព្ទ",
                    "តួនាទី", "មន្ត្រី",
                    "នាយកដ្ឋាន", "ស្ថានភាព"
            });

            for (var u : list) {
                boolean blocked =
                        !"ACTIVE".equals(
                                u.getStatusCode());
                Row row = sh.createRow(r++);
                row.setHeightInPoints(22);
                CellStyle cs = blocked
                        ? s.warn : alt(s, u.getNo());

                c(row, 0, u.getNo()+"",              cs);
                c(row, 1, nv(u.getUserNameKh()),     cs);
                c(row, 2, nv(u.getEmail()),          cs);
                c(row, 3, nv(u.getPhone()),          cs);
                c(row, 4, nv(u.getRoleDisplay()),    cs);
                c(row, 5, nv(u.getOfficerName()),    cs);
                c(row, 6, nv(u.getDepartmentName()), cs);
                c(row, 7, nv(u.getStatusLabel()),    cs);
            }

            summaryBar(sh, wb, r, cols,
                    "ចំនួនអ្នកប្រើប្រាស់សរុប: " + list.size()
                            + " អ្នកប្រើ");
            autoWidth(sh, cols);
            freezeHeader(sh, 5);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "មានបញ្ហាក្នុងការបង្កើតរបាយការណ៍អ្នកប្រើប្រាស់: "
                            + e.getMessage());
        }
    }

    private static int orgHeader(
            XSSFSheet sh, XSSFWorkbook wb,
            int r, int cols) {

        int mid = Math.max(1, cols / 2);

        // Row 1: Ministry (left)  |  Kingdom of Cambodia (right)
        Row r0 = sh.createRow(r++);
        r0.setHeightInPoints(22);

        Cell left0 = r0.createCell(0);
        left0.setCellValue(MINISTRY_NAME);
        left0.setCellStyle(letterhead(
                wb, HorizontalAlignment.LEFT, 12, true));
        sh.addMergedRegion(new CellRangeAddress(
                r-1, r-1, 0, mid-1));

        Cell right0 = r0.createCell(mid);
        right0.setCellValue(KINGDOM_NAME);
        right0.setCellStyle(letterhead(
                wb, HorizontalAlignment.CENTER, 12, true));
        sh.addMergedRegion(new CellRangeAddress(
                r-1, r-1, mid, cols-1));

        // Row 2: Department (left)  |  Nation Religion King (right)
        Row r1 = sh.createRow(r++);
        r1.setHeightInPoints(20);

        Cell left1 = r1.createCell(0);
        left1.setCellValue(ORG_NAME);
        left1.setCellStyle(letterhead(
                wb, HorizontalAlignment.LEFT, 11, true));
        sh.addMergedRegion(new CellRangeAddress(
                r-1, r-1, 0, mid-1));

        Cell right1 = r1.createCell(mid);
        right1.setCellValue(KINGDOM_MOTTO);
        right1.setCellStyle(letterhead(
                wb, HorizontalAlignment.CENTER, 11, true));
        sh.addMergedRegion(new CellRangeAddress(
                r-1, r-1, mid, cols-1));

        // Row 3: decorative divider, under the Kingdom block
        Row r2 = sh.createRow(r++);
        r2.setHeightInPoints(13);
        Cell div = r2.createCell(mid);
//        div.setCellValue(DIVIDER);
        div.setCellStyle(letterhead(
                wb, HorizontalAlignment.CENTER, 9, false));
        sh.addMergedRegion(new CellRangeAddress(
                r-1, r-1, mid, cols-1));

        return r;
    }

    private static CellStyle letterhead(
            XSSFWorkbook wb,
            HorizontalAlignment align,
            int sizePt, boolean bold) {
        CellStyle cs = wb.createCellStyle();
        XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(bold);
        f.setFontHeightInPoints((short) sizePt);
        f.setColor(new XSSFColor(
                hex("#000000"), null));
        cs.setFont(f);
        cs.setAlignment(align);
        cs.setVerticalAlignment(
                VerticalAlignment.CENTER);
        return cs;
    }

    private static int reportTitle(
            XSSFSheet sh, XSSFWorkbook wb,
            int r, int cols, String title) {

        Row row = sh.createRow(r++);
        row.setHeightInPoints(30);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(letterhead(
                wb, HorizontalAlignment.CENTER, 14, true));

        sh.addMergedRegion(
                new CellRangeAddress(
                        r-1, r-1, 0, cols-1));
        return r;
    }

    private static int filterBar(
            XSSFSheet sh, XSSFWorkbook wb,
            int r, int cols,
            String extra,
            String status,
            LocalDate from,
            LocalDate to) {

        StringBuilder sb = new StringBuilder();
        sb.append("ទិន្នន័យ: ");

        if (from != null && to != null) {
            sb.append(from.format(D))
                    .append(" → ")
                    .append(to.format(D))
                    .append("   ");
        } else if (from != null) {
            sb.append("ចាប់ពី ")
                    .append(from.format(D))
                    .append("   ");
        } else if (to != null) {
            sb.append("ដល់ ")
                    .append(to.format(D))
                    .append("   ");
        } else {
            sb.append("ទាំងអស់   ");
        }

        if (status != null && !status.isBlank()) {
            sb.append("ស្ថានភាព: ")
                    .append(status)
                    .append("   ");
        }
        if (extra != null && !extra.isBlank()) {
            sb.append(extra).append("   ");
        }

        sb.append("បោះពុម្ពថ្ងៃទី: ")
                .append(LocalDate.now().format(D));

        Row row = sh.createRow(r++);
        row.setHeightInPoints(20);
        Cell cell = row.createCell(0);
        cell.setCellValue(sb.toString());
        cell.setCellStyle(letterhead(
                wb, HorizontalAlignment.CENTER, 10, false));

        sh.addMergedRegion(
                new CellRangeAddress(
                        r-1, r-1, 0, cols-1));
        return r;
    }

    private static int colHeaders(
            XSSFSheet sh, Styles s,
            int r, String[] cols) {

        Row row = sh.createRow(r++);
        row.setHeightInPoints(28);
        for (int i = 0; i < cols.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(s.hdr);
        }
        return r;
    }

    private static void summaryBar(
            XSSFSheet sh, XSSFWorkbook wb,
            int r, int cols, String text) {

        sh.createRow(r++);

        Row row = sh.createRow(r);
        row.setHeightInPoints(22);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);

        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(
                new XSSFColor(LIGHT_GRAY, null));
        cs.setFillPattern(
                FillPatternType.SOLID_FOREGROUND);
        cs.setAlignment(HorizontalAlignment.RIGHT);
        cs.setVerticalAlignment(
                VerticalAlignment.CENTER);

        XSSFFont f = (XSSFFont) wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 10);
        f.setColor(new XSSFColor(
                hex("#1F3864"), null));
        cs.setFont(f);
        cell.setCellStyle(cs);

        sh.addMergedRegion(
                new CellRangeAddress(
                        r, r, 0, cols-1));
    }

    private static void freezeHeader(
            XSSFSheet sh, int rows) {
        sh.createFreezePane(0, rows);
    }

    private static class Styles {
        final CellStyle hdr, data, alt, warn;

        Styles(XSSFWorkbook wb) {
            hdr  = mkHdr(wb);
            data = mkData(wb);
            alt  = mkAlt(wb);
            warn = mkWarn(wb);
        }

        private CellStyle mkHdr(XSSFWorkbook wb) {
            CellStyle cs = wb.createCellStyle();
            XSSFFont f = (XSSFFont) wb.createFont();
            f.setBold(true);
            f.setColor(new XSSFColor(BLACK, null));
            f.setFontHeightInPoints((short) 10);
            cs.setFont(f);
            cs.setFillForegroundColor(
                    new XSSFColor(WHITE, null));
            cs.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);
            border(cs);
            cs.setAlignment(
                    HorizontalAlignment.CENTER);
            cs.setVerticalAlignment(
                    VerticalAlignment.CENTER);
            cs.setWrapText(true);
            return cs;
        }

        private CellStyle mkData(XSSFWorkbook wb) {
            CellStyle cs = wb.createCellStyle();
            XSSFFont f = (XSSFFont) wb.createFont();
            f.setFontHeightInPoints((short) 10);
            f.setColor(new XSSFColor(
                    hex("#000000"), null));
            cs.setFont(f);
            cs.setFillForegroundColor(
                    new XSSFColor(WHITE, null));
            cs.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);
            border(cs);
            cs.setVerticalAlignment(
                    VerticalAlignment.CENTER);
            return cs;
        }

        private CellStyle mkAlt(XSSFWorkbook wb) {
            CellStyle cs = wb.createCellStyle();
            XSSFFont f = (XSSFFont) wb.createFont();
            f.setFontHeightInPoints((short) 10);
            f.setColor(new XSSFColor(
                    hex("#000000"), null));
            cs.setFont(f);
            cs.setFillForegroundColor(
                    new XSSFColor(WHITE, null));
            cs.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);
            border(cs);
            cs.setVerticalAlignment(
                    VerticalAlignment.CENTER);
            return cs;
        }

        private CellStyle mkWarn(XSSFWorkbook wb) {
            CellStyle cs = wb.createCellStyle();
            XSSFFont f = (XSSFFont) wb.createFont();
            f.setBold(true);
            f.setFontHeightInPoints((short) 10);
            f.setColor(new XSSFColor(
                    hex("#000000"), null));
            cs.setFont(f);
            cs.setFillForegroundColor(
                    new XSSFColor(WHITE, null));
            cs.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);
            border(cs);
            cs.setVerticalAlignment(
                    VerticalAlignment.CENTER);
            return cs;
        }

        private void border(CellStyle cs) {
            cs.setBorderBottom(BorderStyle.THIN);
            cs.setBorderTop(BorderStyle.THIN);
            cs.setBorderLeft(BorderStyle.THIN);
            cs.setBorderRight(BorderStyle.THIN);
            cs.setBottomBorderColor(
                    IndexedColors.GREY_50_PERCENT
                            .getIndex());
            cs.setTopBorderColor(
                    IndexedColors.GREY_50_PERCENT
                            .getIndex());
            cs.setLeftBorderColor(
                    IndexedColors.GREY_50_PERCENT
                            .getIndex());
            cs.setRightBorderColor(
                    IndexedColors.GREY_50_PERCENT
                            .getIndex());
        }
    }

    private static CellStyle alt(
            Styles s, int no) {
        return no % 2 == 0 ? s.alt : s.data;
    }

    private static void c(
            Row row, int col,
            String val, CellStyle cs) {
        Cell cell = row.createCell(col);
        cell.setCellValue(
                val != null ? val : "");
        cell.setCellStyle(cs);
    }

    private static void autoWidth(
            XSSFSheet sh, int cols) {
        for (int i = 0; i < cols; i++) {
            sh.autoSizeColumn(i);
            int w = sh.getColumnWidth(i);
            if (w < 3500)
                sh.setColumnWidth(i, 3500);
            if (w > 18000)
                sh.setColumnWidth(i, 18000);
        }
    }

    private static byte[] bytes(
            XSSFWorkbook wb) throws Exception {
        ByteArrayOutputStream out =
                new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }

//    private static final DateTimeFormatter Date =
//            DateTimeFormatter.ofPattern("dd/MM/yyyy");
//    private static final DateTimeFormatter Date_Time =
//            DateTimeFormatter.ofPattern(
//                    "dd/MM/yyyy HH:mm");

    private static String d(LocalDate date) {
        return date != null
                ? date.format(D) : "";
    }

    private static String dt(LocalDateTime dt) {
        return dt != null
                ? dt.format(DT) : "";
    }

    private static String nv(String s) {
        return s != null ? s : "";
    }

    private static String trunc(
            String text, int max) {
        if (text == null) return "";
        return text.length() > max
                ? text.substring(0, max) + "..."
                : text;
    }

    private static byte[] hex(String hex) {
        hex = hex.replace("#", "");
        return new byte[]{
                (byte) Integer.parseInt(
                        hex.substring(0, 2), 16),
                (byte) Integer.parseInt(
                        hex.substring(2, 4), 16),
                (byte) Integer.parseInt(
                        hex.substring(4, 6), 16)
        };
    }
}