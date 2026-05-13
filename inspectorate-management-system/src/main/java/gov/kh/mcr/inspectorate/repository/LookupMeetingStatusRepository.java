package gov.kh.mcr.inspectorate.repository;


import gov.kh.mcr.inspectorate.entity.LookupMeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LookupMeetingStatusRepository
        extends JpaRepository<LookupMeetingStatus,
        String> {

    List<LookupMeetingStatus> findByIsActiveTrueOrderBySortOrderAsc();

    boolean existsByStatusCode(String statusCode);
}