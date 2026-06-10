package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {
    boolean existsByPositionCode(String code);

    List<Position> findByPositionNameContainingIgnoreCase(String keyword);

    List<Position> findAllByOrderByPositionNameAsc();
}