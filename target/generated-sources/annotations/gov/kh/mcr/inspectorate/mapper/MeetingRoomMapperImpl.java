package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.MeetingRoomRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingRoomResponse;
import gov.kh.mcr.inspectorate.entity.Attachment;
import gov.kh.mcr.inspectorate.entity.MeetingRoom;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:15+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class MeetingRoomMapperImpl implements MeetingRoomMapper {

    @Override
    public MeetingRoom toEntity(MeetingRoomRequest request) {
        if ( request == null ) {
            return null;
        }

        MeetingRoom.MeetingRoomBuilder meetingRoom = MeetingRoom.builder();

        meetingRoom.roomCode( request.getRoomCode() );
        meetingRoom.location( request.getLocation() );
        meetingRoom.capacity( request.getCapacity() );
        meetingRoom.status( request.getStatus() );
        meetingRoom.facilities( request.getFacilities() );

        return meetingRoom.build();
    }

    @Override
    public MeetingRoomResponse toResponse(MeetingRoom entity) {
        if ( entity == null ) {
            return null;
        }

        MeetingRoomResponse.MeetingRoomResponseBuilder meetingRoomResponse = MeetingRoomResponse.builder();

        meetingRoomResponse.imageUrl( entityImagePathFilePath( entity ) );
        meetingRoomResponse.roomId( entity.getRoomId() );
        meetingRoomResponse.roomCode( entity.getRoomCode() );
        meetingRoomResponse.location( entity.getLocation() );
        meetingRoomResponse.capacity( entity.getCapacity() );
        meetingRoomResponse.status( entity.getStatus() );
        meetingRoomResponse.facilities( entity.getFacilities() );

        return meetingRoomResponse.build();
    }

    @Override
    public void updateEntity(MeetingRoomRequest request, MeetingRoom entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getRoomCode() != null ) {
            entity.setRoomCode( request.getRoomCode() );
        }
        if ( request.getLocation() != null ) {
            entity.setLocation( request.getLocation() );
        }
        if ( request.getCapacity() != null ) {
            entity.setCapacity( request.getCapacity() );
        }
        if ( request.getStatus() != null ) {
            entity.setStatus( request.getStatus() );
        }
        if ( request.getFacilities() != null ) {
            entity.setFacilities( request.getFacilities() );
        }
    }

    private String entityImagePathFilePath(MeetingRoom meetingRoom) {
        if ( meetingRoom == null ) {
            return null;
        }
        Attachment imagePath = meetingRoom.getImagePath();
        if ( imagePath == null ) {
            return null;
        }
        String filePath = imagePath.getFilePath();
        if ( filePath == null ) {
            return null;
        }
        return filePath;
    }
}
