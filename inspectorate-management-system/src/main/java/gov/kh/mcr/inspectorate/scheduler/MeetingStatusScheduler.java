package gov.kh.mcr.inspectorate.scheduler;

import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .MeetingRoomStatus;
import gov.kh.mcr.inspectorate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation
        .Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation
        .Transactional;
import java.time.*;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingStatusScheduler {

    private final MeetingRepository
            meetingRepo;
    private final MeetingRoomRepository
            roomRepo;
    private final LookupMeetingStatusRepository
            statusRepo;

    // ─────────────────────────────────────────────
    // Run every 1 minute
    // ─────────────────────────────────────────────
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void syncMeetingAndRoomStatus() {

        LocalDate today = LocalDate.now();
        LocalTime now   = LocalTime.now();

        // Fix ១ — Meeting SCHEDULED/CONFIRMED
        // → IN_PROGRESS ពេល startTime ដល់
        markMeetingsInProgress(today, now);

        // Fix ២ — Meeting IN_PROGRESS
        // → COMPLETED ពេល endTime ដល់
        markMeetingsCompleted(today, now);

        // Fix ៣ — Room AVAILABLE
        // → IN_USE ពេល Meeting ចាប់ផ្ដើម
        markRoomsInUse(today, now);

        // Fix ៤ — Room IN_USE
        // → AVAILABLE ពេល Meeting បញ្ចប់
        markRoomsAvailable(today, now);
    }

    // ─────────────────────────────────────────────
    // Fix — Meeting → IN_PROGRESS
    // ─────────────────────────────────────────────
    private void markMeetingsInProgress(
            LocalDate today,
            LocalTime now) {

        List<Meeting> meetings =
                meetingRepo
                        .findByDateAndStartedNotInProgress(
                                today, now);

        if (meetings.isEmpty()) return;

        var inProgressStatus =
                statusRepo.findById(
                        "IN_PROGRESS").orElse(null);

        if (inProgressStatus == null) {
            log.warn(
                    "Status IN_PROGRESS"
                            + " not found in lookup");
            return;
        }

        for (Meeting m : meetings) {
            m.setStatusCode(
                    inProgressStatus);
            meetingRepo.save(m);

            log.info(
                    "Meeting [{}] → IN_PROGRESS",
                    m.getTitle());
        }
    }

    // ─────────────────────────────────────────────
    // Fix — Meeting → COMPLETED
    // ─────────────────────────────────────────────
    private void markMeetingsCompleted(
            LocalDate today,
            LocalTime now) {

        List<Meeting> meetings =
                meetingRepo
                        .findByDateAndEndedNotCompleted(
                                today, now);

        if (meetings.isEmpty()) return;

        var completedStatus =
                statusRepo.findById(
                        "COMPLETED").orElse(null);

        if (completedStatus == null) {
            log.warn(
                    "Status COMPLETED"
                            + " not found in lookup");
            return;
        }

        for (Meeting m : meetings) {
            m.setStatusCode(
                    completedStatus);
            meetingRepo.save(m);

            log.info(
                    "Meeting [{}] → COMPLETED",
                    m.getTitle());
        }
    }

    // ─────────────────────────────────────────────
    // Fix — Room → IN_USE
    // ─────────────────────────────────────────────
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
                                + " Meeting: [{}]",
                        room.getRoomCode(),
                        meeting.getTitle());
            }
        }
    }

    // ─────────────────────────────────────────────
    // Fix — Room → AVAILABLE
    // ─────────────────────────────────────────────
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

                String prevMeeting =
                        room.getCurrentMeeting()
                                != null
                                ? room.getCurrentMeeting()
                                .getTitle()
                                : "?";

                room.setStatus(
                        MeetingRoomStatus
                                .AVAILABLE);
                room.setCurrentMeeting(null);
                roomRepo.save(room);

                log.info(
                        "Room [{}] → AVAILABLE"
                                + " (Meeting [{}]"
                                + " ended)",
                        room.getRoomCode(),
                        prevMeeting);
            }
        }
    }

    // ─────────────────────────────────────────────
    // Fix — Midnight Reset
    // ─────────────────────────────────────────────
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void midnightReset() {

        LocalDate today = LocalDate.now();
        LocalTime now   = LocalTime.now();

        // Reset stuck IN_USE rooms
        List<MeetingRoom> stuckRooms =
                roomRepo
                        .findRoomsToMarkAvailable(
                                today, now);

        stuckRooms.forEach(room -> {
            room.setStatus(
                    MeetingRoomStatus.AVAILABLE);
            room.setCurrentMeeting(null);
            roomRepo.save(room);
            log.info(
                    "Midnight reset: Room [{}]"
                            + " → AVAILABLE",
                    room.getRoomCode());
        });

        log.info(
                "Midnight reset done:"
                        + " {} rooms",
                stuckRooms.size());
    }
}