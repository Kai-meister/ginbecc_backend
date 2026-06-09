package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Integer> {

    List<UserDevice> findByUserUserId(Integer userId);

    Optional<UserDevice> findByToken(String token);

    void deleteByToken(String token);

    void deleteByTokenIn(Collection<String> tokens);
}
