package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response
        .ApprovalResponse;
import gov.kh.mcr.inspectorate.dto.response
        .PageResponse;
import gov.kh.mcr.inspectorate.entity.Document;

public interface ApprovalService {

    ApprovalResponse requestApproval(
            ApprovalRequest request);

    ApprovalResponse decide(
            Integer approvalId,
            DecideRequest request);

    PageResponse<ApprovalResponse> getAll(
            int page, int size,
            String status,
            Integer officerId,
            Integer documentId);

    ApprovalResponse getById(Integer id);

    PageResponse<ApprovalResponse>
    getMyPending(int page, int size);

    PageResponse<ApprovalResponse>
    getMyRequests(
            int page, int size,
            String status);

    PageResponse<ApprovalResponse>
    getMyDecided(
            int page, int size);
    void autoCreateApproval(
            Document document);
}