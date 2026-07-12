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

    Page<Meeting>
    findByStatusCode_StatusCode(
            String status, Pageable pageable);

    List<Meeting> findByMeetingDate(LocalDate date);

    // Dashboard "today" count, scoped to the viewer's department so the
    // number matches the meeting list a department user can actually see.
    List<Meeting>
    findByMeetingDateAndOrganizer_Officer_Department_DepartmentId(
            LocalDate date,
            Integer departmentId);

    Page<Meeting> findByRoom_RoomId(
            Integer roomId, Pageable pageable);

    Page<Meeting>
    findByOrganizer_Officer_Department_DepartmentId(
            Integer departmentId,
            Pageable pageable);

    // Department-scoped + status — ?status= was ignored for non-admin
    // users (mobile home "upcoming" showed cancelled/completed meetings).
    Page<Meeting>
    findByOrganizer_Officer_Department_DepartmentIdAndStatusCode_StatusCode(
            Integer departmentId,
            String status,
            Pageable pageable);
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

    @Query("""
SELECT m FROM Meeting m
LEFT JOIN FETCH m.organizer u
LEFT JOIN FETCH m.statusCode s
WHERE m.room.roomId = :roomId
AND   m.statusCode.statusCode
      NOT IN ('CANCELLED')
AND   m.meetingDate >= :from
AND   m.meetingDate <= :to
ORDER BY m.meetingDate ASC,
         m.startTime   ASC
""")
    List<Meeting> findByRoomSchedule(
            @Param("roomId") Integer roomId,
            @Param("from")   LocalDate from,
            @Param("to")     LocalDate to);
    // Fix — Room's meetings on a
// specific date
    @Query("""
    SELECT m FROM Meeting m
    LEFT JOIN FETCH m.organizer u
    LEFT JOIN FETCH m.statusCode s
    WHERE m.room.roomId = :roomId
    AND   m.meetingDate = :date
    AND   m.statusCode.statusCode
          NOT IN ('CANCELLED')
    ORDER BY m.startTime ASC
    """)
    List<Meeting> findByRoomAndDate(
            @Param("roomId") Integer roomId,
            @Param("date")   LocalDate date);

    // Fix — Currently running meetings
    @Query("""
    SELECT m FROM Meeting m
    LEFT JOIN FETCH m.room r
    WHERE m.meetingDate = :today
    AND   m.startTime <= :now
    AND   m.endTime   >  :now
    AND   m.statusCode.statusCode
          IN ('SCHEDULED',
              'CONFIRMED',
              'IN_PROGRESS')
    """)
    List<Meeting> findCurrentlyRunning(
            @Param("today") LocalDate today,
            @Param("now")   LocalTime now);


    @Query("""
    SELECT m FROM Meeting m
    WHERE m.meetingDate = :today
    AND   m.startTime <= :now
    AND   m.endTime   >  :now
    AND   m.statusCode.statusCode
          IN ('SCHEDULED', 'CONFIRMED')
    """)
    List<Meeting>
    findByDateAndStartedNotInProgress(
            @Param("today") LocalDate today,
            @Param("now")   LocalTime now);

    // Fix — Meetings that should be
// COMPLETED (endTime <= now
// but not yet COMPLETED)
    @Query("""
    SELECT m FROM Meeting m
    WHERE m.meetingDate = :today
    AND   m.endTime <= :now
    AND   m.statusCode.statusCode
          IN ('IN_PROGRESS',
              'SCHEDULED',
              'CONFIRMED')
    """)
    List<Meeting>
    findByDateAndEndedNotCompleted(
            @Param("today") LocalDate today,
            @Param("now")   LocalTime now);

    // Fix — Currently running (for Room
// IN_USE detection)

    // Fix — Room schedule by room

    // Fix — Conflict check
    @Query("""
    SELECT m FROM Meeting m
    WHERE m.room.roomId = :roomId
    AND   m.meetingDate = :date
    AND   m.statusCode.statusCode
          NOT IN ('CANCELLED',
                  'COMPLETED')
    AND NOT (
        m.endTime <= :startTime
        OR m.startTime >= :endTime
    )
    AND   (:excludeId IS NULL
           OR m.meetingId
              != :excludeId)
    """)
    List<Meeting> findConflicts(
            @Param("roomId")    Integer roomId,
            @Param("date")      LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime")   LocalTime endTime,
            @Param("excludeId") Integer excludeId);

    // Fix — Room booking for date range
// (for booking display)
    @Query("""
    SELECT m FROM Meeting m
    LEFT JOIN FETCH m.organizer u
    LEFT JOIN FETCH m.statusCode s
    LEFT JOIN FETCH m.room r
    WHERE (:roomId IS NULL
           OR r.roomId = :roomId)
    AND   m.meetingDate
          BETWEEN :from AND :to
    AND   m.statusCode.statusCode
          NOT IN ('CANCELLED')
    ORDER BY m.meetingDate ASC,
             m.startTime   ASC
    """)
    List<Meeting> findBookingsInRange(
            @Param("roomId") Integer roomId,
            @Param("from")   LocalDate from,
            @Param("to")     LocalDate to);
}