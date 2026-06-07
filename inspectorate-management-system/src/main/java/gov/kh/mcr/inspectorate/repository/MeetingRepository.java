package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.Meeting;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MeetingRepository
        extends JpaRepository<Meeting, Integer> {

    //  Filter by status
    Page<Meeting>
    findByStatusCode_StatusCode(
            String status, Pageable pageable);

    List<Meeting> findByMeetingDate(LocalDate date);

    // Filter by room
    Page<Meeting> findByRoom_RoomId(
            Integer roomId, Pageable pageable);

    // Calendar — month/year
    @Query("""
        SELECT m FROM Meeting m
        LEFT JOIN FETCH m.room        r
        LEFT JOIN FETCH m.statusCode  s
        LEFT JOIN FETCH m.organizer   u
        WHERE MONTH(m.meetingDate) = :month
        AND   YEAR(m.meetingDate)  = :year
        ORDER BY m.meetingDate ASC,
                 m.startTime   ASC
        """)
    List<Meeting> findByMonthAndYear(
            @Param("month") int month,
            @Param("year")  int year);

    //  Conflict check
    @Query("""
        SELECT m FROM Meeting m
        WHERE m.room.roomId = :roomId
        AND   m.meetingDate = :date
        AND   m.statusCode.statusCode
              NOT IN :ignoredStatuses
        AND   :startTime < m.endTime
        AND   :endTime   > m.startTime
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

    // Conflict check (exclude self)
    @Query("""
        SELECT m FROM Meeting m
        WHERE m.room.roomId  = :roomId
        AND   m.meetingDate  = :date
        AND   m.meetingId   != :excludeId
        AND   m.statusCode.statusCode
              NOT IN :ignoredStatuses
        AND   :startTime < m.endTime
        AND   :endTime   > m.startTime
        """)
    List<Meeting> findConflictsExclude(
            @Param("roomId")
            Integer roomId,
            @Param("date")
            LocalDate date,
            @Param("startTime")
            LocalTime startTime,
            @Param("endTime")
            LocalTime endTime,
            @Param("excludeId")
            Integer excludeId,
            @Param("ignoredStatuses")
            List<String> ignoredStatuses);

    // Room schedule
    @Query("""
        SELECT m FROM Meeting m
        WHERE m.room.roomId = :roomId
        AND   m.meetingDate = :date
        AND   m.statusCode.statusCode
              NOT IN :ignoredStatuses
        ORDER BY m.startTime ASC
        """)
    List<Meeting> findRoomSchedule(
            @Param("roomId")  Integer roomId,
            @Param("date")    LocalDate date,
            @Param("ignoredStatuses")
            List<String> ignoredStatuses);

    // Report query
    @Query("""
        SELECT m FROM Meeting m
        LEFT JOIN FETCH m.room       r
        LEFT JOIN FETCH m.statusCode s
        LEFT JOIN FETCH m.organizer  u
        WHERE MONTH(m.meetingDate) = :month
        AND   YEAR(m.meetingDate)  = :year
        AND   (:status IS NULL
               OR s.statusCode = :status)
        ORDER BY m.meetingDate ASC,
                 m.startTime   ASC
        """)
    List<Meeting> findForReport(
            @Param("month")  int month,
            @Param("year")   int year,
            @Param("status") String status);
}