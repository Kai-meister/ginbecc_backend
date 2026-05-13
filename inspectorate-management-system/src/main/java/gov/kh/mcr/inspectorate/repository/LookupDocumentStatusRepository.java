package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.LookupDocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LookupDocumentStatusRepository extends JpaRepository<LookupDocumentStatus, String> {

    List<LookupDocumentStatus> findByIsActiveTrueOrderBySortOrderAsc();


    List<LookupDocumentStatus> findByAppliesToAndIsActiveTrueOrderBySortOrderAsc(String appliesTo);

    List<LookupDocumentStatus> findByAppliesToInAndIsActiveTrueOrderBySortOrderAsc(List<String> appliesToList);

    boolean existsByStatusCode(String statusCode);
}