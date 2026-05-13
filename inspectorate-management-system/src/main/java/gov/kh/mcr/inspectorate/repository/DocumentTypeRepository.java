package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.DocumentType;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Integer> {

    boolean existsByDocumentTypeCode(String code);
}