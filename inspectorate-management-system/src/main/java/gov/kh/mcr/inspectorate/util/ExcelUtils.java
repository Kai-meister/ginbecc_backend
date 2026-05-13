package gov.kh.mcr.inspectorate.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;

public final class ExcelUtils {

    private ExcelUtils() {}

    public static CellStyle createBoldStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public static void writeHeader(Row row, String[] headers,
                                   CellStyle style) {
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    public static void autoSizeColumns(Sheet sheet, int count) {
        for (int i = 0; i < count; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public static byte[] toByteArray(Workbook wb) throws IOException {
        try (ByteArrayOutputStream out =
                     new ByteArrayOutputStream()) {
            wb.write(out);
            return out.toByteArray();
        }
    }

    public static String safe(String s) {
        return s != null ? s : "";
    }
}
