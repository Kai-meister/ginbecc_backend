package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MeetingRepository
        extends JpaRepository<Meeting, Integer> {

    List<Meeting> findByMeetingDate(LocalDate date);

    Page<Meeting> findByStatusCode_StatusCode(
            String status, Pageable pageable);

    Page<Meeting> findByRoom_RoomIdAndStatusCode_StatusCode(
            Integer roomId, String status, Pageable pageable);

    @Query("""
        SELECT m FROM Meeting m
        WHERE MONTH(m.meetingDate) = :month
        AND YEAR(m.meetingDate) = :year
        ORDER BY m.meetingDate, m.startTime
        """)
    List<Meeting> findByMonthAndYear(
            @Param("month") int month,
            @Param("year") int year);

    @Query("""
        SELECT m FROM Meeting m
        WHERE m.room.roomId = :roomId
        AND m.meetingDate = :date
        AND m.statusCode.statusCode
            NOT IN ('CANCELLED','COMPLETED','ARCHIVED')
        ORDER BY m.startTime ASC
        """)
    List<Meeting> findByRoomAndDate(
            @Param("roomId") Integer roomId,
            @Param("date") LocalDate date);


    @Query("""
SELECT m FROM Meeting m
WHERE m.room.roomId = :roomId
AND m.meetingDate = :date
AND m.statusCode.statusCode NOT IN :ignoredStatuses
AND :startTime < m.endTime
AND :endTime > m.startTime
""")
    List<Meeting> findConflicts(
            @Param("roomId")
            Integer roomId,
            @Param("date")
            LocalDate date,
            @Param("startTime")
            LocalTime startTime,
            @Param("endTime")
            LocalTime endTime,
            @Param("ignoredStatuses")
            List<String> ignoredStatuses);

    @Query("""
        SELECT m FROM Meeting m
        WHERE m.room.roomId = :roomId
        AND m.meetingDate = :date
        AND m.meetingId != :excludeId
        AND m.statusCode.statusCode
            NOT IN ('CANCELLED','COMPLETED','ARCHIVED')
        AND (:startTime < m.endTime AND :endTime > m.startTime)
        """)
    List<Meeting> findConflictsExclude(
            @Param("roomId") Integer roomId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Integer excludeId);

    @Query("""
        SELECT MONTH(m.meetingDate), COUNT(m)
        FROM Meeting m
        WHERE YEAR(m.meetingDate) = :year
        GROUP BY MONTH(m.meetingDate)
        """)
    List<Object[]> countByMonth(@Param("year") int year);
}