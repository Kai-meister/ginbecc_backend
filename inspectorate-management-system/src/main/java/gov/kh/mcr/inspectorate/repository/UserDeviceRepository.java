package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice,Integer> {

    List<UserDevice> findByUserUserId(Integer userId);

    Optional<UserDevice> findByToken(String token);

    // Both deletes below need their own transaction: their callers (logout in
    // DeviceController, dead-token pruning in FcmServiceImpl) run outside one.

    // Scoped to the owning user on purpose — an unscoped delete-by-token would let
    // any authenticated caller unregister someone else's device from its token value.
    @Modifying
    @Transactional
    @Query("DELETE FROM UserDevice d"
            + " WHERE d.token = :token AND d.user.userId = :userId")
    void deleteOwnedToken(@Param("token") String token,
                          @Param("userId") Integer userId);

    @Modifying
    @Transactional
    void deleteByTokenIn(Collection<String> tokens);
}
