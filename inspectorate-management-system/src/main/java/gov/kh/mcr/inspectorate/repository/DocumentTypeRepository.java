package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.DocumentType;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Integer> {

    List<DocumentType>
    findByStatusOrderByDocumentTypeNameAsc(
            ActiveStatus status);

    List<DocumentType>
    findAllByOrderByDocumentTypeNameAsc();

    boolean existsByDocumentTypeCode(
            String code);

    Optional<DocumentType>
    findByDocumentTypeCode(String code);

    long countByDocumentTypeId(
            Integer documentTypeId);
}