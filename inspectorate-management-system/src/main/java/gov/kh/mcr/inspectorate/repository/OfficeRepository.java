package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Office;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface OfficeRepository extends JpaRepository<Office, Integer> {

    List<Office> findByStatus(ActiveStatus status);

    List<Office> findByOfficeNameContainingIgnoreCase(String keyword);

    List<Office> findByStatusAndOfficeNameContainingIgnoreCase(ActiveStatus status, String keyword);

    List<Office> findAllByOrderByOfficeNameAsc();

    boolean existsByOfficeCode(String code);

}
