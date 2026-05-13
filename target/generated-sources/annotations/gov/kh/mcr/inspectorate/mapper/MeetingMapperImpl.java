package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.MeetingRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingResponse;
import gov.kh.mcr.inspectorate.entity.LookupMeetingStatus;
import gov.kh.mcr.inspectorate.entity.Meeting;
import gov.kh.mcr.inspectorate.entity.MeetingRoom;
import gov.kh.mcr.inspectorate.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:15+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class MeetingMapperImpl implements MeetingMapper {

    @Override
    public Meeting toEntity(MeetingRequest request) {
        if ( request == null ) {
            return null;
        }

        Meeting.MeetingBuilder meeting = Meeting.builder();

        meeting.title( request.getTitle() );
        meeting.meetingDate( request.getMeetingDate() );
        meeting.startTime( request.getStartTime() );
        meeting.endTime( request.getEndTime() );
        meeting.meetingType( request.getMeetingType() );
        meeting.meetingLink( request.getMeetingLink() );
        meeting.agenda( request.getAgenda() );
        meeting.note( request.getNote() );

        return meeting.build();
    }

    @Override
    public MeetingResponse toResponse(Meeting entity) {
        if ( entity == null ) {
            return null;
        }

        MeetingResponse.MeetingResponseBuilder meetingResponse = MeetingResponse.builder();

        meetingResponse.roomId( entityRoomRoomId( entity ) );
        meetingResponse.roomCode( entityRoomRoomCode( entity ) );
        meetingResponse.roomLocation( entityRoomLocation( entity ) );
        meetingResponse.organizerId( entityOrganizerUserId( entity ) );
        meetingResponse.organizerName( entityOrganizerUserNameKh( entity ) );
        meetingResponse.statusCode( entityStatusCodeStatusCode( entity ) );
        meetingResponse.statusLabel( entityStatusCodeLabelKh( entity ) );
        meetingResponse.meetingId( entity.getMeetingId() );
        meetingResponse.title( entity.getTitle() );
        meetingResponse.meetingDate( entity.getMeetingDate() );
        meetingResponse.startTime( entity.getStartTime() );
        meetingResponse.endTime( entity.getEndTime() );
        meetingResponse.meetingType( entity.getMeetingType() );
        meetingResponse.meetingLink( entity.getMeetingLink() );
        meetingResponse.agenda( entity.getAgenda() );
        meetingResponse.note( entity.getNote() );
        meetingResponse.createdAt( entity.getCreatedAt() );
        meetingResponse.updatedAt( entity.getUpdatedAt() );

        return meetingResponse.build();
    }

    @Override
    public void updateEntity(MeetingRequest request, Meeting entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getTitle() != null ) {
            entity.setTitle( request.getTitle() );
        }
        if ( request.getMeetingDate() != null ) {
            entity.setMeetingDate( request.getMeetingDate() );
        }
        if ( request.getStartTime() != null ) {
            entity.setStartTime( request.getStartTime() );
        }
        if ( request.getEndTime() != null ) {
            entity.setEndTime( request.getEndTime() );
        }
        if ( request.getMeetingType() != null ) {
            entity.setMeetingType( request.getMeetingType() );
        }
        if ( request.getMeetingLink() != null ) {
            entity.setMeetingLink( request.getMeetingLink() );
        }
        if ( request.getAgenda() != null ) {
            entity.setAgenda( request.getAgenda() );
        }
        if ( request.getNote() != null ) {
            entity.setNote( request.getNote() );
        }
    }

    private Integer entityRoomRoomId(Meeting meeting) {
        if ( meeting == null ) {
            return null;
        }
        MeetingRoom room = meeting.getRoom();
        if ( room == null ) {
            return null;
        }
        Integer roomId = room.getRoomId();
        if ( roomId == null ) {
            return null;
        }
        return roomId;
    }

    private String entityRoomRoomCode(Meeting meeting) {
        if ( meeting == null ) {
            return null;
        }
        MeetingRoom room = meeting.getRoom();
        if ( room == null ) {
            return null;
        }
        String roomCode = room.getRoomCode();
        if ( roomCode == null ) {
            return null;
        }
        return roomCode;
    }

    private String entityRoomLocation(Meeting meeting) {
        if ( meeting == null ) {
            return null;
        }
        MeetingRoom room = meeting.getRoom();
        if ( room == null ) {
            return null;
        }
        String location = room.getLocation();
        if ( location == null ) {
            return null;
        }
        return location;
    }

    private Integer entityOrganizerUserId(Meeting meeting) {
        if ( meeting == null ) {
            return null;
        }
        User organizer = meeting.getOrganizer();
        if ( organizer == null ) {
            return null;
        }
        Integer userId = organizer.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }

    private String entityOrganizerUserNameKh(Meeting meeting) {
        if ( meeting == null ) {
            return null;
        }
        User organizer = meeting.getOrganizer();
        if ( organizer == null ) {
            return null;
        }
        String userNameKh = organizer.getUserNameKh();
        if ( userNameKh == null ) {
            return null;
        }
        return userNameKh;
    }

    private String entityStatusCodeStatusCode(Meeting meeting) {
        if ( meeting == null ) {
            return null;
        }
        LookupMeetingStatus statusCode = meeting.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String statusCode1 = statusCode.getStatusCode();
        if ( statusCode1 == null ) {
            return null;
        }
        return statusCode1;
    }

    private String entityStatusCodeLabelKh(Meeting meeting) {
        if ( meeting == null ) {
            return null;
        }
        LookupMeetingStatus statusCode = meeting.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String labelKh = statusCode.getLabelKh();
        if ( labelKh == null ) {
            return null;
        }
        return labelKh;
    }
}
