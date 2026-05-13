package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.MeetingMinute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MeetingMinuteRepository
        extends JpaRepository<MeetingMinute, Integer> {

    Optional<MeetingMinute> findByMeeting_MeetingId(Integer meetingId);
    boolean existsByMeeting_MeetingId(Integer meetingId);
    Page<MeetingMinute> findAll(Pageable pageable);

}