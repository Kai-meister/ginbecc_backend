package gov.kh.mcr.inspectorate.service;


import gov.kh.mcr.inspectorate.dto.request.OfficerRequest;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.response.OfficerResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;

import java.util.List;

public interface OfficerService {

    PageResponse<OfficerResponse> getAll(int page, int size, Integer deptId, String status);

    OfficerResponse getById(Integer id);

    OfficerResponse create(OfficerRequest request);

    OfficerResponse update(Integer id, OfficerRequest request);

    OfficerResponse updateStatus(Integer id, StatusRequest request);

    void delete(Integer id);

    List<OfficerResponse> getNearRetirement();

    OfficerResponse updateProfileImage(Integer officerId, Integer attachmentId);
}