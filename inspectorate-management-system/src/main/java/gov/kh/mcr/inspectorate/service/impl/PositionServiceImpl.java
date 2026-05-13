package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.PositionRequest;
import gov.kh.mcr.inspectorate.dto.response.PositionResponse;
import gov.kh.mcr.inspectorate.entity.Position;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.PositionMapper;
import gov.kh.mcr.inspectorate.repository.PositionRepository;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionServiceImpl
        implements PositionService {

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional(readOnly = true)
    public List<PositionResponse> getAll(
            String keyword) {

        List<Position> list =
                StringUtils.hasText(keyword)
                        ? positionRepository
                          .findByPositionNameContainingIgnoreCase(
                                  keyword)
                        : positionRepository
                          .findAllByOrderByPositionNameAsc();

        return list.stream()
                .map(positionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PositionResponse getById(Integer id) {
        return positionMapper.toResponse(
                findById(id));
    }

    @Override
    public PositionResponse create(
            PositionRequest request) {

        if (positionRepository.existsByPositionCode(
                request.getPositionCode())) {
            throw new DuplicateResourceException(
                    "កូដ ["
                            + request.getPositionCode()
                            + "] មានស្ទួន");
        }

        Position saved = positionRepository.save(
                positionMapper.toEntity(request));

        activityLogService.log(
                "CREATE", "Position",
                saved.getPositionId(),
                "បង្កើតតំណែង: "
                        + saved.getPositionName());

        return positionMapper.toResponse(saved);
    }

    @Override
    public PositionResponse update(
            Integer id, PositionRequest request) {

        Position position = findById(id);
        position.setPositionName(
                request.getPositionName());

        activityLogService.log(
                "UPDATE", "Position", id,
                "កែប្រែ: "
                        + position.getPositionName());

        return positionMapper.toResponse(
                positionRepository.save(position));
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        positionRepository.deleteById(id);
        activityLogService.log(
                "DELETE", "Position",
                id, "លុបតំណែង");
    }

    private Position findById(Integer id) {
        return positionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "តំណែង", id));
    }
}