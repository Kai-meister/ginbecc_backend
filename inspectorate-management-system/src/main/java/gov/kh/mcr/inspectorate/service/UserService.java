package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import java.util.List;

public interface UserService {

    PageResponse<UserResponse> getAll(
            int page, int size,
            Integer roleId,
            String status,
            String keyword);

    UserResponse getById(Integer id);

    List<String> getPermissions(Integer id);

    UserResponse create(UserRequest request);

    UserResponse update(
            Integer id, UserRequest request);

    UserResponse updateStatus(
            Integer id, StatusRequest request);

    void delete(Integer id);
}