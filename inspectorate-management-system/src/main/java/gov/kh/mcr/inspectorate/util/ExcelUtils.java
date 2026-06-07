package gov.kh.mcr.inspectorate.util;

import gov.kh.mcr.inspectorate.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import java.io.ByteArrayOutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
public final class ExcelUtils {

    private ExcelUtils() {}

    private static final DateTimeFormatter D =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DT =
            DateTimeFormatter
                    .ofPattern("dd/MM/yyyy HH:mm");


    public static byte[] officers(
            List<Officer> list) {

        try (XSSFWorkbook wb =
                     new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("មន្ត្រីរាជការ");
            Styles s = new Styles(wb);

            title(sh, wb,
                    "របាយការណ៍មន្ត្រីរាជការ", 9);
            headers(sh, s.hdr, new String[]{
                    "ល.រ", "លេខសម្គាល់",
                    "គោត្តនាម ឈ្មោះ",
                    "ភេទ",
                    "ថ្ងៃខែឆ្នាំកំណើត",
                    "នាយកដ្ឋាន", "តំណែង",
                    "ថ្ងៃចូលបម្រើ", "ស្ថានភាព"
            });

            int r = 3;
            for (int i = 0;
                 i < list.size(); i++) {
                Officer o = list.get(i);
                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs = alt(s, i);

                c(row, 0, (i+1)+"", cs);
                c(row, 1,
                        o.getOfficerCode(), cs);
                c(row, 2,
                        o.getFullNameKh(), cs);
                c(row, 3,
                        o.getGender() != null
                                ? o.getGender().name()
                                : "", cs);
                c(row, 4,
                        d(o.getDob()), cs);
                c(row, 5,
                        o.getDepartment() != null
                                ? o.getDepartment()
                                  .getDepartmentName()
                                : "", cs);
                c(row, 6,
                        o.getPosition() != null
                                ? o.getPosition()
                                  .getPositionName()
                                : "", cs);
                c(row, 7,
                        d(o.getJoinDate()), cs);
                c(row, 8,
                        lbl(o.getStatusCode()), cs);
            }

            summary(sh, wb, r,
                    "សរុប: " + list.size()
                            + " នាក់", 9);
            auto(sh, 9);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Officer report: "
                            + e.getMessage());
        }
    }


    public static byte[] contractOfficers(
            List<ContractOfficer> list,
            int withinDays) {

        try (XSSFWorkbook wb =
                     new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("Contract Officers");
            Styles s = new Styles(wb);

            title(sh, wb,
                    "មន្ត្រីកិច្ចសន្យា ផុតក្នុង "
                            + withinDays + " ថ្ងៃ", 8);

            headers(sh, s.hdr, new String[]{
                    "ល.រ", "លេខសម្គាល់",
                    "គោត្តនាម ឈ្មោះ",
                    "នាយកដ្ឋាន",
                    "ចាប់ផ្ដើម", "ផុតកំណត់",
                    "ថ្ងៃនៅសល់", "ស្ថានភាព"
            });

            int r = 3;
            for (int i = 0;
                 i < list.size(); i++) {
                ContractOfficer o = list.get(i);
                long days =
                        o.getEndDate() != null
                                ? ChronoUnit.DAYS.between(
                                LocalDate.now(),
                                o.getEndDate())
                                : 0;

                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs = days <= 7
                        ? s.warn : alt(s, i);

                c(row, 0, (i+1)+"", cs);
                c(row, 1,
                        o.getContractOfficerCode(),
                        cs);
                c(row, 2,
                        o.getFullNameKh(), cs);
                c(row, 3,
                        o.getDepartment() != null
                                ? o.getDepartment()
                                  .getDepartmentName()
                                : "", cs);
                c(row, 4,
                        d(o.getStartDate()), cs);
                c(row, 5,
                        d(o.getEndDate()), cs);
                c(row, 6, days+"ថ្ងៃ", cs);
                c(row, 7,
                        lbl(o.getStatusCode()), cs);
            }

            summary(sh, wb, r,
                    "សរុប: " + list.size()
                            + " នាក់", 8);
            auto(sh, 8);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Contract report: "
                            + e.getMessage());
        }
    }

    public static byte[] documents(
            List<Document> list) {

        try (XSSFWorkbook wb =
                     new XSSFWorkbook()) {

            XSSFSheet sh = wb.createSheet("ឯកសារ");
            Styles s = new Styles(wb);

            title(sh, wb,
                    "របាយការណ៍ឯកសារ", 8);
            headers(sh, s.hdr, new String[]{
                    "ល.រ", "លេខឯកសារ",
                    "ឈ្មោះឯកសារ", "ប្រភេទ",
                    "មន្ត្រី", "ផុតកំណត់",
                    "ស្ថានភាព", "ថ្ងៃបង្កើត"
            });

            int r = 3;
            for (int i = 0;
                 i < list.size(); i++) {
                Document doc = list.get(i);
                boolean exp =
                        doc.getExpiryDate() != null
                                && doc.getExpiryDate()
                                .isBefore(LocalDate.now());

                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs = exp
                        ? s.warn : alt(s, i);

                c(row, 0, (i+1)+"", cs);
                c(row, 1,
                        doc.getDocumentNumber(), cs);
                c(row, 2,
                        doc.getDocumentName(), cs);
                c(row, 3,
                        doc.getDocumentType() != null
                                ? doc.getDocumentType()
                                  .getDocumentTypeName()
                                : "", cs);
                c(row, 4,
                        doc.getOfficer() != null
                                ? doc.getOfficer()
                                  .getFullNameKh()
                                : "", cs);
                c(row, 5,
                        d(doc.getExpiryDate()), cs);
                c(row, 6,
                        lbl(doc.getStatusCode()), cs);
                c(row, 7,
                        dt(doc.getCreatedAt()), cs);
            }

            summary(sh, wb, r,
                    "សរុប: " + list.size()
                            + " ឯកសារ", 8);
            auto(sh, 8);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Document report: "
                            + e.getMessage());
        }
    }

    public static byte[] approvals(
            List<Approval> list) {

        try (XSSFWorkbook wb =
                     new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("Approvals");
            Styles s = new Styles(wb);

            title(sh, wb,
                    "របាយការណ៍ការអនុម័ត", 7);
            headers(sh, s.hdr, new String[]{
                    "ល.រ", "ឈ្មោះឯកសារ",
                    "ស្នើដោយ", "អនុម័តដោយ",
                    "ស្ថានភាព", "ហេតុផល",
                    "ថ្ងៃស្នើ"
            });

            int r = 3;
            long approved = 0, rejected = 0;

            for (int i = 0;
                 i < list.size(); i++) {
                Approval a = list.get(i);
                String code =
                        a.getStatusCode() != null
                                ? a.getStatusCode()
                                  .getStatusCode() : "";

                if ("APPROVED".equals(code))
                    approved++;
                else if ("REJECTED".equals(code))
                    rejected++;

                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs =
                        "REJECTED".equals(code)
                                ? s.warn : alt(s, i);

                c(row, 0, (i+1)+"", cs);
                c(row, 1,
                        a.getDocument() != null
                                ? a.getDocument()
                                  .getDocumentName()
                                : "", cs);
                c(row, 2,
                        a.getRequestedBy() != null
                                ? a.getRequestedBy()
                                  .getFullNameKh()
                                : "", cs);
                c(row, 3,
                        a.getApprovedBy() != null
                                ? a.getApprovedBy()
                                  .getUserNameKh()
                                : "", cs);
                c(row, 4, lbl(a.getStatusCode()),
                        cs);
                c(row, 5,
                        a.getComment() != null
                                ? a.getComment() : "", cs);
                c(row, 6,
                        dt(a.getRequestedAt()), cs);
            }

            summary(sh, wb, r,
                    "សរុប: " + list.size()
                            + " | អនុម័ត: " + approved
                            + " | បដិសេធ: " + rejected, 7);
            auto(sh, 7);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Approval report: "
                            + e.getMessage());
        }
    }

    public static byte[] meetings(
            List<Meeting> list,
            int month, int year) {

        try (XSSFWorkbook wb =
                     new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("ការប្រជុំ");
            Styles s = new Styles(wb);

            title(sh, wb,
                    "របាយការណ៍ប្រជុំ — "
                            + String.format("%02d", month)
                            + "/" + year, 7);

            headers(sh, s.hdr, new String[]{
                    "ល.រ", "ចំណងជើង",
                    "ថ្ងៃប្រជុំ", "ម៉ោង",
                    "បន្ទប់", "ប្រភេទ",
                    "ស្ថានភាព"
            });

            int r = 3;
            for (int i = 0;
                 i < list.size(); i++) {
                Meeting m = list.get(i);
                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs = alt(s, i);

                c(row, 0, (i+1)+"", cs);
                c(row, 1, m.getTitle(), cs);
                c(row, 2,
                        d(m.getMeetingDate()), cs);
                c(row, 3,
                        (m.getStartTime() != null
                                ? m.getStartTime()
                                  .toString() : "")
                                + " - "
                                + (m.getEndTime() != null
                                ? m.getEndTime()
                                  .toString() : ""),
                        cs);
                c(row, 4,
                        m.getRoom() != null
                                ? m.getRoom()
                                  .getRoomCode()
                                : "Online", cs);
                c(row, 5,
                        m.getMeetingType() != null
                                ? m.getMeetingType()
                                  .name() : "", cs);
                c(row, 6,
                        lbl(m.getStatusCode()), cs);
            }

            summary(sh, wb, r,
                    "សរុប: " + list.size()
                            + " ប្រជុំ", 7);
            auto(sh, 7);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Meeting report: "
                            + e.getMessage());
        }
    }

    public static byte[] meetingMinutes(
            List<MeetingMinute> list) {

        try (XSSFWorkbook wb =
                     new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("Meeting Minutes");
            Styles s = new Styles(wb);

            title(sh, wb,
                    "របាយការណ៍កំណត់ហេតុប្រជុំ",
                    6);
            headers(sh, s.hdr, new String[]{
                    "ល.រ", "ការប្រជុំ",
                    "ថ្ងៃប្រជុំ", "សង្ខេប",
                    "ការសម្រេច", "កត់ហេតុដោយ"
            });

            int r = 3;
            for (int i = 0;
                 i < list.size(); i++) {
                MeetingMinute m = list.get(i);
                Row row = sh.createRow(r++);
                row.setHeightInPoints(30);
                CellStyle cs = alt(s, i);

                c(row, 0, (i+1)+"", cs);
                c(row, 1,
                        m.getMeeting() != null
                                ? m.getMeeting()
                                  .getTitle() : "", cs);
                c(row, 2,
                        m.getMeeting() != null
                                ? d(m.getMeeting()
                                    .getMeetingDate())
                                : "", cs);
                c(row, 3,
                        trunc(m.getSummary(), 100),
                        cs);
                c(row, 4,
                        trunc(m.getDecisions(), 100),
                        cs);
                c(row, 5,
                        m.getRecordedBy() != null
                                ? m.getRecordedBy()
                                  .getUserNameKh()
                                : "", cs);
            }

            summary(sh, wb, r,
                    "សរុប: " + list.size()
                            + " កំណត់ហេតុ", 6);
            auto(sh, 6);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Minute report: "
                            + e.getMessage());
        }
    }

    public static byte[] announcements(
            List<Announcement> list) {

        try (XSSFWorkbook wb =
                     new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("Announcements");
            Styles s = new Styles(wb);

            title(sh, wb,
                    "របាយការណ៍សេចក្ដីប្រកាស", 6);
            headers(sh, s.hdr, new String[]{
                    "ល.រ", "ចំណងជើង",
                    "អ្នកបង្កើត",
                    "អាទិភាព", "ស្ថានភាព",
                    "ថ្ងៃបង្កើត"
            });

            int r = 3;
            for (int i = 0;
                 i < list.size(); i++) {
                Announcement a = list.get(i);
                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs = alt(s, i);

                c(row, 0, (i+1)+"", cs);
                c(row, 1, a.getTitle(), cs);
                c(row, 2,
                        a.getCreatedBy() != null
                                ? a.getCreatedBy()
                                  .getUserNameKh()
                                : "", cs);
                c(row, 3,
                        a.getPriority() != null
                                ? a.getPriority().name()
                                : "", cs);
                c(row, 4,
                        lbl(a.getStatusCode()), cs);
                c(row, 5,
                        dt(a.getCreatedAt()), cs);
            }

            summary(sh, wb, r,
                    "សរុប: " + list.size()
                            + " ប្រកាស", 6);
            auto(sh, 6);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Announcement report: "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // 8. AUDIT LOGS
    // ─────────────────────────────────────────────
    public static byte[] auditLogs(
            List<ActivityLog> list) {

        try (XSSFWorkbook wb =
                     new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("Audit Logs");
            Styles s = new Styles(wb);

            title(sh, wb,
                    "របាយការណ៍សកម្មភាព", 7);
            headers(sh, s.hdr, new String[]{
                    "ល.រ", "អ្នកប្រើ", "Email",
                    "សកម្មភាព", "Entity",
                    "IP Address", "ពេលវេលា"
            });

            int r = 3;
            for (int i = 0;
                 i < list.size(); i++) {
                ActivityLog l = list.get(i);
                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs = alt(s, i);

                c(row, 0, (i+1)+"", cs);
                c(row, 1,
                        l.getUser() != null
                                ? l.getUser()
                                  .getUserNameKh()
                                : "SYSTEM", cs);
                c(row, 2,
                        l.getUserEmail() != null
                                ? l.getUserEmail()
                                : "", cs);
                c(row, 3,
                        actionKh(l.getAction()),
                        cs);
                c(row, 4,
                        nvl(l.getEntityType(), "")
                                + (l.getEntityId() != null
                                ? " #"+l.getEntityId()
                                : ""), cs);
                c(row, 5,
                        nvl(l.getIpAddress(), ""),
                        cs);
                c(row, 6,
                        dt(l.getCreatedAt()), cs);
            }

            summary(sh, wb, r,
                    "សរុប: " + list.size()
                            + " Log", 7);
            auto(sh, 7);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "AuditLog report: "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // 9. NOTIFICATIONS
    // ─────────────────────────────────────────────
    public static byte[] notifications(
            List<Notification> list) {

        try (XSSFWorkbook wb =
                     new XSSFWorkbook()) {

            XSSFSheet sh =
                    wb.createSheet("Notifications");
            Styles s = new Styles(wb);

            title(sh, wb,
                    "របាយការណ៍ការជូនដំណឹង", 6);
            headers(sh, s.hdr, new String[]{
                    "ល.រ", "ទទួលដោយ",
                    "ចំណងជើង", "ប្រភេទ",
                    "អានហើយ?", "ពេលបង្កើត"
            });

            int r = 3;
            long read = 0;

            for (int i = 0;
                 i < list.size(); i++) {
                Notification n = list.get(i);
                if (Boolean.TRUE.equals(
                        n.getIsRead())) read++;

                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs = alt(s, i);

                c(row, 0, (i+1)+"", cs);
                c(row, 1,
                        n.getUser() != null
                                ? n.getUser()
                                  .getUserNameKh()
                                : "", cs);
                c(row, 2, n.getTitle(), cs);
                c(row, 3,
                        n.getType() != null
                                ? n.getType()
                                  .getLabelKh()
                                : "", cs);
                c(row, 4,
                        Boolean.TRUE.equals(
                                n.getIsRead())
                                ? "អាន" : "មិនទាន់",
                        cs);
                c(row, 5,
                        dt(n.getCreatedAt()), cs);
            }

            summary(sh, wb, r,
                    "សរុប: " + list.size()
                            + " | អាន: " + read
                            + " | មិនទាន់: "
                            + (list.size() - read), 6);
            auto(sh, 6);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Notification report: "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // 10. USERS
    // ─────────────────────────────────────────────
    public static byte[] users(
            List<User> list) {

        try (XSSFWorkbook wb =
                     new XSSFWorkbook()) {

            XSSFSheet sh = wb.createSheet("Users");
            Styles s = new Styles(wb);

            title(sh, wb,
                    "របាយការណ៍អ្នកប្រើប្រាស់", 6);
            headers(sh, s.hdr, new String[]{
                    "ល.រ", "ឈ្មោះ KH",
                    "ឈ្មោះ EN", "Email",
                    "Role", "ស្ថានភាព"
            });

            int r = 3;
            for (int i = 0;
                 i < list.size(); i++) {
                User u = list.get(i);
                boolean blocked =
                        u.getStatusCode() != null
                                && !"ACTIVE".equals(
                                u.getStatusCode()
                                        .getStatusCode());

                Row row = sh.createRow(r++);
                row.setHeightInPoints(20);
                CellStyle cs = blocked
                        ? s.warn : alt(s, i);

                c(row, 0, (i+1)+"", cs);
                c(row, 1, u.getUserNameKh(), cs);
                c(row, 2,
                        nvl(u.getUserNameEn(), ""),
                        cs);
                c(row, 3, u.getEmail(), cs);
                c(row, 4,
                        u.getRole() != null
                                ? u.getRole()
                                  .getDisplayName()
                                : "", cs);
                c(row, 5,
                        lbl(u.getStatusCode()), cs);
            }

            summary(sh, wb, r,
                    "សរុប: " + list.size()
                            + " អ្នកប្រើ", 6);
            auto(sh, 6);
            return bytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(
                    "User report: "
                            + e.getMessage());
        }
    }

    // ══ SHARED HELPERS ════════════════════════════

    // ── Styles ────────────────────────────────────
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
            XSSFFont f = wb.createFont();
            f.setBold(true);
            f.setColor(IndexedColors.WHITE
                    .getIndex());
            f.setFontHeightInPoints((short)11);
            cs.setFont(f);
            cs.setFillForegroundColor(
                    IndexedColors.DARK_BLUE
                            .getIndex());
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

        private CellStyle mkData(
                XSSFWorkbook wb) {
            CellStyle cs = wb.createCellStyle();
            border(cs);
            cs.setVerticalAlignment(
                    VerticalAlignment.CENTER);
            return cs;
        }

        private CellStyle mkAlt(
                XSSFWorkbook wb) {
            CellStyle cs = wb.createCellStyle();
            border(cs);
            cs.setVerticalAlignment(
                    VerticalAlignment.CENTER);
            cs.setFillForegroundColor(
                    IndexedColors
                            .LIGHT_CORNFLOWER_BLUE
                            .getIndex());
            cs.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);
            return cs;
        }

        private CellStyle mkWarn(
                XSSFWorkbook wb) {
            CellStyle cs = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true);
            f.setColor(
                    IndexedColors.RED.getIndex());
            cs.setFont(f);
            border(cs);
            cs.setVerticalAlignment(
                    VerticalAlignment.CENTER);
            cs.setFillForegroundColor(
                    IndexedColors.ROSE.getIndex());
            cs.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);
            return cs;
        }

        private void border(CellStyle cs) {
            cs.setBorderBottom(BorderStyle.THIN);
            cs.setBorderTop(BorderStyle.THIN);
            cs.setBorderLeft(BorderStyle.THIN);
            cs.setBorderRight(BorderStyle.THIN);
        }
    }

    private static CellStyle alt(
            Styles s, int i) {
        return i % 2 == 0 ? s.data : s.alt;
    }

    private static void title(
            XSSFSheet sh, XSSFWorkbook wb,
            String text, int cols) {

        Row r = sh.createRow(0);
        r.setHeightInPoints(36);
        Cell cell = r.createCell(0);
        cell.setCellValue(text);

        CellStyle cs = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short)14);
        cs.setFont(f);
        cs.setAlignment(
                HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(
                VerticalAlignment.CENTER);
        cell.setCellStyle(cs);

        sh.addMergedRegion(
                new CellRangeAddress(
                        0, 0, 0, cols-1));
    }

    private static void headers(
            XSSFSheet sh,
            CellStyle style,
            String[] cols) {

        Row r = sh.createRow(2);
        r.setHeightInPoints(28);
        for (int i = 0; i < cols.length; i++) {
            Cell cell = r.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(style);
        }
    }

    private static void summary(
            XSSFSheet sh, XSSFWorkbook wb,
            int rowIdx, String text, int cols) {

        Row r = sh.createRow(rowIdx + 1);
        r.setHeightInPoints(22);
        Cell cell = r.createCell(0);
        cell.setCellValue(text);

        CellStyle cs = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true);
        cs.setFont(f);
        cs.setFillForegroundColor(
                IndexedColors.LIGHT_YELLOW
                        .getIndex());
        cs.setFillPattern(
                FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(cs);

        sh.addMergedRegion(
                new CellRangeAddress(
                        rowIdx+1, rowIdx+1,
                        0, cols-1));
    }

    private static void c(
            Row row, int col,
            String val, CellStyle cs) {
        Cell cell = row.createCell(col);
        cell.setCellValue(
                val != null ? val : "");
        cell.setCellStyle(cs);
    }

    private static void auto(
            XSSFSheet sh, int cols) {
        for (int i = 0; i < cols; i++) {
            sh.autoSizeColumn(i);
            int w = sh.getColumnWidth(i);
            if (w < 3000) sh.setColumnWidth(i, 3000);
            if (w > 15000) sh.setColumnWidth(i, 15000);
        }
    }

    private static byte[] bytes(
            XSSFWorkbook wb) throws Exception {
        ByteArrayOutputStream out =
                new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }

    private static String d(LocalDate date) {
        return date != null
                ? date.format(D) : "";
    }

    private static String dt(LocalDateTime dt) {
        return dt != null
                ? dt.format(DT) : "";
    }

    private static String lbl(Object status) {
        if (status == null) return "";
        try {
            return (String) status.getClass()
                    .getMethod("getLabelKh")
                    .invoke(status);
        } catch (Exception e) { return ""; }
    }

    private static String trunc(
            String text, int max) {
        if (text == null) return "";
        return text.length() > max
                ? text.substring(0, max) + "..."
                : text;
    }

    private static String nvl(
            String val, String def) {
        return val != null ? val : def;
    }

    private static String actionKh(
            String action) {
        if (action == null) return "";
        return switch (action) {
            case "CREATE" -> "បង្កើត";
            case "UPDATE" -> "កែប្រែ";
            case "DELETE" -> "លុប";
            case "LOGIN"  -> "ចូលប្រព័ន្ធ";
            case "LOGOUT" -> "ចេញ";
            case "RESET_PASSWORD"  -> "Reset PW";
            case "CHANGE_PASSWORD" -> "ប្ដូរ PW";
            default -> action;
        };
    }
}