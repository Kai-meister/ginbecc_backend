package gov.kh.mcr.inspectorate.service;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.request.UserRequest;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    PageResponse<UserResponse> getAll(
            int page, int size,
            Integer roleId,
            String status,
            String keyword);

    UserResponse getById(Integer id);

    UserResponse create(UserRequest request);

    UserResponse update(Integer id, UserRequest request);

    UserResponse updateStatus(Integer id, StatusRequest request);

    List<String> getPermissions(Integer id);

    void delete(Integer id);
}