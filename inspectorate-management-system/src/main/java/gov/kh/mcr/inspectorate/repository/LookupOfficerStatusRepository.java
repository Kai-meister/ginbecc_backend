package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.LookupOfficerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LookupOfficerStatusRepository
        extends JpaRepository<LookupOfficerStatus,
        String> {


    List<LookupOfficerStatus>
    findByIsActiveTrueOrderBySortOrderAsc();


    List<LookupOfficerStatus>
    findByLabelKhContainingIgnoreCaseAndIsActiveTrue(
            String keyword);


    boolean existsByStatusCode(String statusCode);
}