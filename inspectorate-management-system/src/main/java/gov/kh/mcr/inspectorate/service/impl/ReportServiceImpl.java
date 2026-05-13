package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.entity.ActivityLog;
import gov.kh.mcr.inspectorate.entity.Meeting;
import gov.kh.mcr.inspectorate.entity.Officer;
import gov.kh.mcr.inspectorate.repository.ActivityLogRepository;
import gov.kh.mcr.inspectorate.repository.MeetingRepository;
import gov.kh.mcr.inspectorate.repository.OfficerRepository;
import gov.kh.mcr.inspectorate.service.ReportService;
import gov.kh.mcr.inspectorate.util.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final OfficerRepository     officerRepository;
    private final MeetingRepository     meetingRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    public byte[] exportOfficersToExcel(Integer deptId)
            throws IOException {

        List<Officer> officers = deptId != null
                ? officerRepository
                  .findByDepartment_DepartmentId(
                          deptId, Pageable.unpaged()).getContent()
                : officerRepository.findAll();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("មន្ត្រី");
            CellStyle bold = ExcelUtils.createBoldStyle(wb);

            String[] headers = {
                    "លេខ","ឈ្មោះខ្មែរ","ឈ្មោះអង់គ្លេស",
                    "ភេទ","ថ្ងៃកំណើត","នាយកដ្ឋាន",
                    "តំណែង","ទូរស័ព្ទ","អ៊ីមែល","ស្ថានភាព"
            };

            ExcelUtils.writeHeader(
                    sheet.createRow(0), headers, bold);

            int rowNum = 1;
            for (Officer o : officers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(
                        ExcelUtils.safe(o.getOfficerCode()));
                row.createCell(1).setCellValue(
                        ExcelUtils.safe(o.getFullNameKh()));
                row.createCell(2).setCellValue(
                        ExcelUtils.safe(o.getFullNameEn()));
                row.createCell(3).setCellValue(
                        o.getGender() != null
                                ? o.getGender().name() : "");
                row.createCell(4).setCellValue(
                        o.getDob() != null
                                ? o.getDob().toString() : "");
                row.createCell(5).setCellValue(
                        o.getDepartment() != null
                                ? o.getDepartment().getDepartmentName() : "");
                row.createCell(6).setCellValue(
                        o.getPosition() != null
                                ? o.getPosition().getPositionName() : "");
                row.createCell(7).setCellValue(
                        ExcelUtils.safe(o.getPhone()));
                row.createCell(8).setCellValue(
                        ExcelUtils.safe(o.getEmail()));
                row.createCell(9).setCellValue(
                        o.getStatusCode() != null
                                ? o.getStatusCode().getLabelKh() : "");
            }

            ExcelUtils.autoSizeColumns(sheet, headers.length);
            return ExcelUtils.toByteArray(wb);
        }
    }

    @Override
    public byte[] exportMeetingsToExcel(String from, String to)
            throws IOException {

        List<Meeting> meetings;
        if (from != null) {
            String[] parts = from.split("-");
            meetings = meetingRepository.findByMonthAndYear(
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[0]));
        } else {
            meetings = meetingRepository.findAll();
        }

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("ការប្រជុំ");
            CellStyle bold = ExcelUtils.createBoldStyle(wb);

            String[] headers = {
                    "ចំណងជើង","ថ្ងៃប្រជុំ","ចាប់ផ្តើម",
                    "បញ្ចប់","ប្រភេទ","បន្ទប់","ស្ថានភាព"
            };

            ExcelUtils.writeHeader(
                    sheet.createRow(0), headers, bold);

            int rowNum = 1;
            for (Meeting m : meetings) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(
                        ExcelUtils.safe(m.getTitle()));
                row.createCell(1).setCellValue(
                        m.getMeetingDate() != null
                                ? m.getMeetingDate().toString() : "");
                row.createCell(2).setCellValue(
                        m.getStartTime() != null
                                ? m.getStartTime().toString() : "");
                row.createCell(3).setCellValue(
                        m.getEndTime() != null
                                ? m.getEndTime().toString() : "");
                row.createCell(4).setCellValue(
                        m.getMeetingType() != null
                                ? m.getMeetingType().name() : "");
                row.createCell(5).setCellValue(
                        m.getRoom() != null
                                ? m.getRoom().getRoomCode() : "");
                row.createCell(6).setCellValue(
                        m.getStatusCode() != null
                                ? m.getStatusCode().getLabelKh() : "");
            }

            ExcelUtils.autoSizeColumns(sheet, headers.length);
            return ExcelUtils.toByteArray(wb);
        }
    }

    @Override
    public byte[] exportAuditLogsToExcel() throws IOException {
        List<ActivityLog> logs = activityLogRepository.findAll();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Audit Logs");
            CellStyle bold = ExcelUtils.createBoldStyle(wb);

            String[] headers = {
                    "អ្នកប្រើ","សកម្មភាព","ប្រភេទ",
                    "Entity ID","ព័ត៌មាន","ពេលវេលា"
            };

            ExcelUtils.writeHeader(
                    sheet.createRow(0), headers, bold);

            int rowNum = 1;
            for (ActivityLog log : logs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(
                        log.getUser() != null
                                ? log.getUser().getUserNameKh() : "");
                row.createCell(1).setCellValue(
                        ExcelUtils.safe(log.getAction()));
                row.createCell(2).setCellValue(
                        ExcelUtils.safe(log.getEntityType()));
                row.createCell(3).setCellValue(
                        log.getEntityId() != null
                                ? log.getEntityId().toString() : "");
                row.createCell(4).setCellValue(
                        ExcelUtils.safe(log.getDetails()));
                row.createCell(5).setCellValue(
                        log.getCreatedAt() != null
                                ? log.getCreatedAt().toString() : "");
            }

            ExcelUtils.autoSizeColumns(sheet, headers.length);
            return ExcelUtils.toByteArray(wb);
        }
    }
}