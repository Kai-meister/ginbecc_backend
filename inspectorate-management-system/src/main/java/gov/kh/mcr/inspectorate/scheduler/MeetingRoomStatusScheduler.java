package gov.kh.mcr.inspectorate.scheduler;

import gov.kh.mcr.inspectorate.entity
        .Meeting;
import gov.kh.mcr.inspectorate.entity
        .MeetingRoom;
import gov.kh.mcr.inspectorate.enums
        .MeetingRoomStatus;
import gov.kh.mcr.inspectorate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation
        .Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation
        .Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingRoomStatusScheduler {

    private final MeetingRoomRepository
            roomRepo;
    private final MeetingRepository
            meetingRepo;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void updateRoomStatuses() {

        LocalDate today = LocalDate.now();
        LocalTime now   = LocalTime.now();

        markRoomsInUse(today, now);

        markRoomsAvailable(today, now);
    }

    private void markRoomsInUse(
            LocalDate today,
            LocalTime now) {

        List<Meeting> running =
                meetingRepo
                        .findCurrentlyRunning(
                                today, now);

        for (Meeting meeting : running) {
            MeetingRoom room =
                    meeting.getRoom();
            if (room == null) continue;

            if (room.getStatus()
                    != MeetingRoomStatus
                    .IN_USE) {

                room.setStatus(
                        MeetingRoomStatus
                                .IN_USE);
                room.setCurrentMeeting(
                        meeting);
                roomRepo.save(room);

                log.info(
                        "Room [{}] → IN_USE"
                                + " (Meeting: {})",
                        room.getRoomCode(),
                        meeting.getTitle());
            }
        }
    }

    private void markRoomsAvailable(
            LocalDate today,
            LocalTime now) {

        List<MeetingRoom> rooms =
                roomRepo
                        .findRoomsToMarkAvailable(
                                today, now);

        for (MeetingRoom room : rooms) {
            if (room.getStatus()
                    == MeetingRoomStatus
                    .IN_USE) {

                room.setStatus(
                        MeetingRoomStatus
                                .AVAILABLE);
                room.setCurrentMeeting(null);
                roomRepo.save(room);

                log.info(
                        "Room [{}] → AVAILABLE"
                                + " (Meeting ended)",
                        room.getRoomCode());
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void midnightReset() {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<MeetingRoom> stuckRooms =
                roomRepo
                        .findRoomsToMarkAvailable(
                                today, now);

        for (MeetingRoom room : stuckRooms) {
            if (room.getStatus()
                    == MeetingRoomStatus
                    .IN_USE) {
                room.setStatus(
                        MeetingRoomStatus
                                .AVAILABLE);
                room.setCurrentMeeting(null);
                roomRepo.save(room);
                log.info(
                        "Midnight Reset:"
                                + " Room [{}] → AVAILABLE",
                        room.getRoomCode());
            }
        }
    }
}