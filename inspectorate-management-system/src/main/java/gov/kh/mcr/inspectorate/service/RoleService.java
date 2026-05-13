package gov.kh.mcr.inspectorate.service;
import gov.kh.mcr.inspectorate.dto.request.RoleRequest;
import gov.kh.mcr.inspectorate.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    List<RoleResponse> getAll();
    RoleResponse getById(Integer id);
    RoleResponse create(RoleRequest request);
    RoleResponse update(Integer id, RoleRequest request);
}
