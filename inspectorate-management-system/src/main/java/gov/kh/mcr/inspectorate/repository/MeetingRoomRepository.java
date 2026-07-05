package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.MeetingRoom;
import gov.kh.mcr.inspectorate.enums.MeetingRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MeetingRoomRepository
        extends JpaRepository<MeetingRoom, Integer> {

    boolean existsByRoomCode(String code);

    List<MeetingRoom> findByStatus(MeetingRoomStatus status);

    List<MeetingRoom> findAllByOrderByRoomCodeAsc();
    @Query("""
        SELECT DISTINCT m.room
        FROM Meeting m
        WHERE m.room IS NOT NULL
        AND   m.meetingDate = :date
        AND   m.statusCode.statusCode
              NOT IN ('CANCELLED',
                      'COMPLETED')
        AND   NOT (
            m.endTime <= :startTime
            OR m.startTime >= :endTime
        )
        AND   (:excludeMeetingId IS NULL
               OR m.meetingId
                  != :excludeMeetingId)
        """)
    List<MeetingRoom> findConflictingRooms(
            @Param("date")
            LocalDate date,
            @Param("startTime")
            LocalTime startTime,
            @Param("endTime")
            LocalTime endTime,
            @Param("excludeMeetingId")
            Integer excludeMeetingId);

    @Query("""
        SELECT DISTINCT m.room
        FROM Meeting m
        WHERE m.room IS NOT NULL
        AND   m.meetingDate = :today
        AND   m.startTime <= :now
        AND   m.endTime > :now
        AND   m.statusCode.statusCode
              IN ('SCHEDULED',
                  'CONFIRMED',
                  'IN_PROGRESS')
        AND   m.room.status
              != 'IN_USE'
        """)
    List<MeetingRoom>
    findRoomsToMarkInUse(
            @Param("today")
            LocalDate today,
            @Param("now")
            LocalTime now);

    @Query("""
        SELECT r FROM MeetingRoom r
        WHERE r.status = 'IN_USE'
        AND NOT EXISTS (
            SELECT 1 FROM Meeting m
            WHERE m.room = r
            AND   m.meetingDate = :today
            AND   m.startTime <= :now
            AND   m.endTime > :now
            AND   m.statusCode.statusCode
                  IN ('SCHEDULED',
                      'CONFIRMED',
                      'IN_PROGRESS')
        )
        """)
    List<MeetingRoom>
    findRoomsToMarkAvailable(
            @Param("today")
            LocalDate today,
            @Param("now")
            LocalTime now);
}