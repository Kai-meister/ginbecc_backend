package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.DepartmentRequest;
import gov.kh.mcr.inspectorate.dto.request.OfficeRequest;
import gov.kh.mcr.inspectorate.dto.response.DepartmentResponse;
import gov.kh.mcr.inspectorate.dto.response.OfficeResponse;
import gov.kh.mcr.inspectorate.entity.Office;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;

import java.util.List;

public interface OfficeService {

    List<OfficeResponse> getAll(ActiveStatus status, String keyword);

    OfficeResponse getById(Integer id);

    OfficeResponse create(OfficeRequest request);

    OfficeResponse update(Integer id, OfficeRequest request);

    void delete(Integer id);

}
