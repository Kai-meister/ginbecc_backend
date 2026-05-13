package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.LookupUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LookupUserStatusRepository
        extends JpaRepository<LookupUserStatus,
        String> {

    List<LookupUserStatus>
    findByIsActiveTrueOrderBySortOrderAsc();

    boolean existsByStatusCode(String statusCode);
}