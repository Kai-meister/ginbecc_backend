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

    private static final String ORG_NAME =
            "អគ្គាធិការដ្ឋានពុទ្ធិកសិក្សាជាតិ";
    private static final String ORG_NAME_EN =
            "National Inspectorate of Buddhist Education";

    private static final String FONT_NAME =
            "Khmer OS Siemreap";


    private static final byte[] HDR_BG =
            hex("#401E12");
    private static final byte[] SUB_BG =
            hex("#895129");
    private static final byte[] ALT_BG =
            hex("#E7C6A2");
    private static final byte[] WARN_BG =
            hex("#FCE4D6");
    private static final byte[] SUM_BG =
            hex("#E2EFDA");
    private static final byte[] WHITE =
            hex("#FFFFFF");

    public static byte[] officers(
            List<OfficerReportResponse> list,
            Integer deptId,
            String status,
            LocalDate from,
            LocalDate to) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("មន្ត្រីរាជការ");
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
                    "តំណែង",
                    "ថ្ងៃចូលបម្រើ",
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
                    "សរុប: " + list.size() + " នាក់");

            autoWidth(sh, cols);
            freezeHeader(sh, 4); // freeze row 1-4
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Officer report: "
                            + e.getMessage());
        }
    }

    public static byte[] contractOfficers(
            List<ContractOfficerReportResponse> list,
            Integer days) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("Contract Officers");
            Styles s = new Styles(wb);
            int cols = 11;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍មន្ត្រីជាប់កិច្ចសន្យា");
            r = filterBar(sh, wb, r, cols,
                    "ជិតផុតក្នុង: "
                            + (days != null ? days : 30)
                            + " ថ្ងៃ",
                    null, null, null);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "លេខសម្គាល់",
                    "គោត្តនាម ឈ្មោះ",
                    "ភេទ", "ថ្ងៃខែឆ្នាំ",
                    "អាយុ", "នាយកដ្ឋាន",
                    "ចាប់ផ្ដើម", "ផុតកំណត់",
                    "ថ្ងៃនៅសល់",
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
                    "សរុប: " + list.size() + " នាក់");
            autoWidth(sh, cols);
            freezeHeader(sh, 4);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Contract report: "
                            + e.getMessage());
        }
    }

    public static byte[] documents(
            List<DocumentReportResponse> list,
            Integer officerId,
            String status,
            LocalDate from,
            LocalDate to) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh = wb.createSheet("ឯកសារ");
            Styles s = new Styles(wb);
            int cols = 9;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍ឯកសារ");
            r = filterBar(sh, wb, r, cols,
                    null, status, from, to);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "លេខឯកសារ",
                    "ឈ្មោះឯកសារ", "ប្រភេទ",
                    "មន្ត្រី", "នាយកដ្ឋាន",
                    "ផុតកំណត់", "ស្ថានភាព",
                    "ថ្ងៃបង្កើត"
            });

            long expired = 0;
            for (var d2 : list) {
                boolean exp = Boolean.TRUE.equals(
                        d2.getIsExpired());
                if (exp) expired++;
                Row row = sh.createRow(r++);
                row.setHeightInPoints(22);
                CellStyle cs = exp
                        ? s.warn : alt(s, d2.getNo());

                c(row, 0, d2.getNo()+"",              cs);
                c(row, 1, nv(d2.getDocumentNumber()), cs);
                c(row, 2, nv(d2.getDocumentName()),   cs);
                c(row, 3, nv(d2.getDocumentTypeName()),cs);
                c(row, 4, nv(d2.getOfficerName()),    cs);
                c(row, 5, nv(d2.getDepartmentName()), cs);
                c(row, 6, d(d2.getExpiryDate()),      cs);
                c(row, 7, nv(d2.getStatusLabel()),    cs);
                c(row, 8, dt(d2.getCreatedAt()),      cs);
            }

            summaryBar(sh, wb, r, cols,
                    "សរុប: " + list.size()
                            + "  |  ផុតកំណត់: "
                            + expired);
            autoWidth(sh, cols);
            freezeHeader(sh, 4);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Document report: "
                            + e.getMessage());
        }
    }

    public static byte[] approvals(
            List<ApprovalReportResponse> list,
            String status,
            LocalDate from,
            LocalDate to) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("Approvals");
            Styles s = new Styles(wb);
            int cols = 8;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍ការអនុម័ត");
            r = filterBar(sh, wb, r, cols,
                    null, status, from, to);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "ឈ្មោះឯកសារ",
                    "ស្នើដោយ", "នាយកដ្ឋាន",
                    "អនុម័តដោយ", "ស្ថានភាព",
                    "ហេតុផល", "ថ្ងៃស្នើ"
            });

            long approved = 0, rejected = 0;
            for (var a : list) {
                if ("APPROVED".equals(
                        a.getStatusCode())) approved++;
                else if ("REJECTED".equals(
                        a.getStatusCode())) rejected++;

                Row row = sh.createRow(r++);
                row.setHeightInPoints(22);
                CellStyle cs =
                        "REJECTED".equals(
                                a.getStatusCode())
                                ? s.warn : alt(s, a.getNo());

                c(row, 0, a.getNo()+"",             cs);
                c(row, 1, nv(a.getDocumentName()),  cs);
                c(row, 2, nv(a.getRequestedBy()),   cs);
                c(row, 3, nv(a.getRequestedByDept()),cs);
                c(row, 4, nv(a.getApprovedBy()),    cs);
                c(row, 5, nv(a.getStatusLabel()),   cs);
                c(row, 6, nv(a.getComment()),       cs);
                c(row, 7, dt(a.getRequestedAt()),   cs);
            }

            summaryBar(sh, wb, r, cols,
                    "សរុប: " + list.size()
                            + "  |  អនុម័ត: " + approved
                            + "  |  បដិសេធ: " + rejected);
            autoWidth(sh, cols);
            freezeHeader(sh, 4);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Approval report: "
                            + e.getMessage());
        }
    }

    public static byte[] meetings(
            List<MeetingReportResponse> list,
            int month, int year,
            String status) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("ការប្រជុំ");
            Styles s = new Styles(wb);
            int cols = 9;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍ប្រជុំ — "
                            + String.format("%02d", month)
                            + "/" + year);
            r = filterBar(sh, wb, r, cols,
                    null, status, null, null);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "ចំណងជើង",
                    "ប្រភេទ", "ថ្ងៃប្រជុំ",
                    "ម៉ោង", "បន្ទប់",
                    "អ្នករៀបចំ",
                    "វត្តមាន/សរុប",
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
                    "សរុប: " + list.size() + " ប្រជុំ");
            autoWidth(sh, cols);
            freezeHeader(sh, 4);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Meeting report: "
                            + e.getMessage());
        }
    }

    public static byte[] meetingMinutes(
            List<MeetingMinuteReportResponse> list) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("Minutes");
            Styles s = new Styles(wb);
            int cols = 7;
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
                    "កត់ហេតុដោយ", "File"
            });

            for (var m : list) {
                Row row = sh.createRow(r++);
                row.setHeightInPoints(30);
                CellStyle cs = alt(s, m.getNo());

                c(row, 0, m.getNo()+"",              cs);
                c(row, 1, nv(m.getMeetingTitle()),   cs);
                c(row, 2, d(m.getMeetingDate()),     cs);
                c(row, 3, trunc(m.getSummary(), 80), cs);
                c(row, 4, trunc(m.getDecisions(), 80),cs);
                c(row, 5, nv(m.getRecordedBy()),     cs);
                c(row, 6, Boolean.TRUE.equals(
                        m.getHasAttachment())
                        ? "✓" : "",                  cs);
            }

            summaryBar(sh, wb, r, cols,
                    "សរុប: " + list.size()
                            + " កំណត់ហេតុ");
            autoWidth(sh, cols);
            freezeHeader(sh, 4);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Minute report: "
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
                    wb.createSheet("Announcements");
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
                    "ស្ថានភាព", "អ្នកទទួល",
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
                    "សរុប: " + list.size() + " ប្រកាស");
            autoWidth(sh, cols);
            freezeHeader(sh, 4);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Announcement report: "
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
                    wb.createSheet("Audit Logs");
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
                    "ល.រ", "អ្នកប្រើ",
                    "Email", "សកម្មភាព",
                    "Entity", "IP",
                    "ពេលវេលា"
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
                    "សរុប: " + list.size() + " Log");
            autoWidth(sh, cols);
            freezeHeader(sh, 4);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "AuditLog report: "
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
                    wb.createSheet("Notifications");
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
                    "ស្ថានភាព", "ពេលបង្កើត",
                    "ពេលអាន"
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
                            + "  |  អាន: " + read
                            + "  |  មិនទាន់: "
                            + (list.size() - read));
            autoWidth(sh, cols);
            freezeHeader(sh, 4);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Notification report: "
                            + e.getMessage());
        }
    }

    public static byte[] users(
            List<UserReportResponse> list,
            String status) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sh = wb.createSheet("Users");
            Styles s = new Styles(wb);
            int cols = 8;
            int r = 0;

            r = orgHeader(sh, wb, r, cols);
            r = reportTitle(sh, wb, r, cols,
                    "របាយការណ៍អ្នកប្រើប្រាស់");
            r = filterBar(sh, wb, r, cols,
                    null, status, null, null);

            r = colHeaders(sh, s, r, new String[]{
                    "ល.រ", "ឈ្មោះ KH",
                    "Email", "Phone",
                    "Role", "មន្ត្រី",
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
                    "សរុប: " + list.size()
                            + " អ្នកប្រើ");
            autoWidth(sh, cols);
            freezeHeader(sh, 4);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "User report: "
                            + e.getMessage());
        }
    }

    private static int orgHeader(
            XSSFSheet sh, XSSFWorkbook wb,
            int r, int cols) {

        Row r0 = sh.createRow(r++);
        r0.setHeightInPoints(28);
        Cell c0 = r0.createCell(0);
        c0.setCellValue(ORG_NAME);
        CellStyle cs0 = wb.createCellStyle();
        XSSFFont f0 = (XSSFFont) wb.createFont();
        f0.setFontName(FONT_NAME);
        f0.setBold(true);
        f0.setFontHeightInPoints((short) 12);
        f0.setColor(new XSSFColor(HDR_BG, null));
        cs0.setFont(f0);
        cs0.setAlignment(HorizontalAlignment.CENTER);
        cs0.setVerticalAlignment(
                VerticalAlignment.CENTER);
        c0.setCellStyle(cs0);
        sh.addMergedRegion(
                new CellRangeAddress(
                        r-1, r-1, 0, cols-1));

        Row r1 = sh.createRow(r++);
        r1.setHeightInPoints(18);
        Cell c1 = r1.createCell(0);
        c1.setCellValue(ORG_NAME_EN);
        CellStyle cs1 = wb.createCellStyle();
        XSSFFont f1 = (XSSFFont) wb.createFont();
        f1.setFontName(FONT_NAME);
        f1.setFontHeightInPoints((short) 9);
        f1.setItalic(true);
        f1.setColor(new XSSFColor(
                hex("#555555"), null));
        cs1.setFont(f1);
        cs1.setAlignment(HorizontalAlignment.CENTER);
        c1.setCellStyle(cs1);
        sh.addMergedRegion(
                new CellRangeAddress(
                        r-1, r-1, 0, cols-1));

        return r;
    }

    private static int reportTitle(
            XSSFSheet sh, XSSFWorkbook wb,
            int r, int cols, String title) {

        Row row = sh.createRow(r++);
        row.setHeightInPoints(36);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);

        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(
                new XSSFColor(HDR_BG, null));
        cs.setFillPattern(
                FillPatternType.SOLID_FOREGROUND);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(
                VerticalAlignment.CENTER);

        XSSFFont f = (XSSFFont) wb.createFont();
        f.setFontName(FONT_NAME);
        f.setBold(true);
        f.setFontHeightInPoints((short) 14);
        f.setColor(new XSSFColor(WHITE, null));
        cs.setFont(f);
        cell.setCellStyle(cs);

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

        sb.append("បោះពុម្ពថ្ងៃ: ")
                .append(LocalDate.now().format(D));

        Row row = sh.createRow(r++);
        row.setHeightInPoints(20);
        Cell cell = row.createCell(0);
        cell.setCellValue(sb.toString());

        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(
                new XSSFColor(SUB_BG, null));
        cs.setFillPattern(
                FillPatternType.SOLID_FOREGROUND);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(
                VerticalAlignment.CENTER);

        XSSFFont f = (XSSFFont) wb.createFont();
        f.setFontName(FONT_NAME);
        f.setFontHeightInPoints((short) 9);
        f.setColor(new XSSFColor(WHITE, null));
        cs.setFont(f);
        cell.setCellStyle(cs);

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

        // blank separator
        sh.createRow(r++);

        Row row = sh.createRow(r);
        row.setHeightInPoints(22);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);

        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(
                new XSSFColor(SUM_BG, null));
        cs.setFillPattern(
                FillPatternType.SOLID_FOREGROUND);
        cs.setAlignment(HorizontalAlignment.RIGHT);
        cs.setVerticalAlignment(
                VerticalAlignment.CENTER);

        XSSFFont f = (XSSFFont) wb.createFont();
        f.setFontName(FONT_NAME);
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
            f.setFontName(FONT_NAME);
            f.setBold(true);
            f.setColor(new XSSFColor(WHITE, null));
            f.setFontHeightInPoints((short) 10);
            cs.setFont(f);
            cs.setFillForegroundColor(
                    new XSSFColor(HDR_BG, null));
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
            f.setFontName(FONT_NAME);
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
            f.setFontName(FONT_NAME);
            f.setFontHeightInPoints((short) 10);
            f.setColor(new XSSFColor(
                    hex("#000000"), null));
            cs.setFont(f);
            cs.setFillForegroundColor(
                    new XSSFColor(ALT_BG, null));
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
            f.setFontName(FONT_NAME);
            f.setBold(true);
            f.setFontHeightInPoints((short) 10);
            f.setColor(new XSSFColor(
                    hex("#000000"), null));
            cs.setFont(f);
            cs.setFillForegroundColor(
                    new XSSFColor(WARN_BG, null));
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

    private static final DateTimeFormatter Date =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter Date_Time =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm");

    private static String d(LocalDate date) {
        return date != null
                ? date.format(Date) : "";
    }

    private static String dt(LocalDateTime dt) {
        return dt != null
                ? dt.format(Date_Time) : "";
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