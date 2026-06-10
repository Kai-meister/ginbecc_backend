package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.MeetingMinute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingMinuteRepository
        extends JpaRepository<MeetingMinute, Integer> {

    Optional<MeetingMinute> findByMeeting_MeetingId(Integer meetingId);
    boolean existsByMeeting_MeetingId(Integer meetingId);
    Page<MeetingMinute> findAll(Pageable pageable);

    // MeetingMinuteRepository
    @Query("""
    SELECT m FROM MeetingMinute m
    LEFT JOIN FETCH m.meeting    mt
    LEFT JOIN FETCH m.recordedBy u
    WHERE MONTH(mt.meetingDate) = :month
    AND   YEAR(mt.meetingDate)  = :year
    ORDER BY mt.meetingDate ASC
    """)
    List<MeetingMinute> findForReport(
            @Param("month") int month,
            @Param("year")  int year);
}