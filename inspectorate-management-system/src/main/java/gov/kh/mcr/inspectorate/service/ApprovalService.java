package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.ApprovalRequest;
import gov.kh.mcr.inspectorate.dto.request.DecideRequest;
import gov.kh.mcr.inspectorate.dto.response.ApprovalResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;

public interface ApprovalService {

    // Officer submit document
    ApprovalResponse requestApproval(
            ApprovalRequest request);

    // Admin decide
    ApprovalResponse decide(
            Integer approvalId,
            DecideRequest request);

    // GET all — filter
    PageResponse<ApprovalResponse> getAll(
            int page, int size,
            String status,
            Integer officerId,
            Integer documentId);

    // GET by ID
    ApprovalResponse getById(Integer id);

    // GET pending - for Approver
    PageResponse<ApprovalResponse>
    getMyPending(int page, int size);
}