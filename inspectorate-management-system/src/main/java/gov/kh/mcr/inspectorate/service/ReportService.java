package gov.kh.mcr.inspectorate.service;

import java.io.IOException;

public interface ReportService {

    byte[] exportOfficersToExcel(Integer deptId)
            throws IOException;

    byte[] exportMeetingsToExcel(String from, String to)
            throws IOException;

    byte[] exportAuditLogsToExcel()
            throws IOException;
}
