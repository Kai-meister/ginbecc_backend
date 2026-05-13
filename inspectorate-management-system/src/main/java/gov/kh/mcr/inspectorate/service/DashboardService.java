package gov.kh.mcr.inspectorate.service;


import gov.kh.mcr.inspectorate.dto.response.DashboardSummaryResponse;

public interface DashboardService {

    DashboardSummaryResponse getSummary(Integer userId);
}
