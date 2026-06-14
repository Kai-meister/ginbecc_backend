package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.LookupAnnouncementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LookupAnnouncementStatusRepository
        extends JpaRepository<LookupAnnouncementStatus,
        String> {

    List<LookupAnnouncementStatus> findByIsActiveTrueOrderBySortOrderAsc();

//    boolean existsByStatusCode(String statusCode);
}