package gov.kh.mcr.inspectorate.service;


import gov.kh.mcr.inspectorate.dto.request.ApprovalDecisionRequest;
import gov.kh.mcr.inspectorate.dto.request.ApprovalRequest;
import gov.kh.mcr.inspectorate.dto.response.ApprovalResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;

public interface ApprovalService {

    PageResponse<ApprovalResponse> getAll(
            int page, int size, String status);

    ApprovalResponse getById(Integer id);

    ApprovalResponse requestApproval(ApprovalRequest request);

    ApprovalResponse decide(Integer id,
                            ApprovalDecisionRequest request);
}