package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request
        .PositionRequest;
import gov.kh.mcr.inspectorate.dto.response
        .PositionResponse;
import java.util.List;

public interface PositionService {

    List<PositionResponse> getAll(Integer departmentId,
            String keyword);

    PositionResponse getById(Integer id);

    PositionResponse create(
            PositionRequest request);

    PositionResponse update(
            Integer id, PositionRequest request);

    void delete(Integer id);
}