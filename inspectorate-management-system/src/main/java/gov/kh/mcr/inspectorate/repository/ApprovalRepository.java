package gov.kh.mcr.inspectorate.repository;


import gov.kh.mcr.inspectorate.entity.Approval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApprovalRepository
        extends JpaRepository<Approval, Integer> {

    Page<Approval> findByStatusCode_StatusCode(
            String status, Pageable pageable);

    Page<Approval> findByRequestedBy_UserId(
            Integer userId, Pageable pageable);

    List<Approval> findByDocument_DocumentId(
            Integer documentId);

    boolean existsByDocument_DocumentIdAndStatusCode_StatusCode(
            Integer documentId, String statusCode);
}