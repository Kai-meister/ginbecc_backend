package gov.kh.mcr.inspectorate.repository;

import gov.kh.mcr.inspectorate.entity.MeetingRoom;
import gov.kh.mcr.inspectorate.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeetingRoomRepository
        extends JpaRepository<MeetingRoom, Integer> {

    List<MeetingRoom> findByStatus(
            RoomStatus status);

    boolean existsByRoomCode(String roomCode);


    List<MeetingRoom>
    findAllByOrderByRoomCodeAsc();
}