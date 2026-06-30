package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request
        .DepartmentRequest;
import gov.kh.mcr.inspectorate.dto.response
        .DepartmentManagerResponse;
import gov.kh.mcr.inspectorate.dto.response
        .DepartmentResponse;
import gov.kh.mcr.inspectorate.enums
        .ActiveStatus;
import java.util.List;

public interface DepartmentService {

    List<DepartmentResponse> getAll(
            ActiveStatus status,
            String keyword);

    DepartmentResponse getById(Integer id);

    DepartmentResponse create(
            DepartmentRequest request);

    DepartmentResponse update(
            Integer id,
            DepartmentRequest request);

    void delete(Integer id);

    DepartmentManagerResponse addManager(
            Integer departmentId,
            Integer managerUserId,
            Boolean isPrimary);

    void removeManager(
            Integer departmentId,
            Integer managerUserId);

    List<DepartmentManagerResponse>
    getManagers(Integer departmentId);
}