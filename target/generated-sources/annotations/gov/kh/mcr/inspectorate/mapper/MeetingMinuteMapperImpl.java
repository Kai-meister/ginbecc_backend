package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.MeetingMinuteRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingMinuteResponse;
import gov.kh.mcr.inspectorate.entity.Attachment;
import gov.kh.mcr.inspectorate.entity.Meeting;
import gov.kh.mcr.inspectorate.entity.MeetingMinute;
import gov.kh.mcr.inspectorate.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:15+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class MeetingMinuteMapperImpl implements MeetingMinuteMapper {

    @Override
    public MeetingMinute toEntity(MeetingMinuteRequest request) {
        if ( request == null ) {
            return null;
        }

        MeetingMinute.MeetingMinuteBuilder meetingMinute = MeetingMinute.builder();

        meetingMinute.summary( request.getSummary() );
        meetingMinute.decisions( request.getDecisions() );
        meetingMinute.actionItems( request.getActionItems() );

        return meetingMinute.build();
    }

    @Override
    public MeetingMinuteResponse toResponse(MeetingMinute entity) {
        if ( entity == null ) {
            return null;
        }

        MeetingMinuteResponse.MeetingMinuteResponseBuilder meetingMinuteResponse = MeetingMinuteResponse.builder();

        meetingMinuteResponse.meetingId( entityMeetingMeetingId( entity ) );
        meetingMinuteResponse.meetingTitle( entityMeetingTitle( entity ) );
        meetingMinuteResponse.recordedByName( entityRecordedByUserNameKh( entity ) );
        meetingMinuteResponse.attachmentUrl( entityAttachmentPathFilePath( entity ) );
        meetingMinuteResponse.minuteId( entity.getMinuteId() );
        meetingMinuteResponse.summary( entity.getSummary() );
        meetingMinuteResponse.decisions( entity.getDecisions() );
        meetingMinuteResponse.actionItems( entity.getActionItems() );
        meetingMinuteResponse.createdAt( entity.getCreatedAt() );

        return meetingMinuteResponse.build();
    }

    @Override
    public void updateEntity(MeetingMinuteRequest request, MeetingMinute entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getSummary() != null ) {
            entity.setSummary( request.getSummary() );
        }
        if ( request.getDecisions() != null ) {
            entity.setDecisions( request.getDecisions() );
        }
        if ( request.getActionItems() != null ) {
            entity.setActionItems( request.getActionItems() );
        }
    }

    private Integer entityMeetingMeetingId(MeetingMinute meetingMinute) {
        if ( meetingMinute == null ) {
            return null;
        }
        Meeting meeting = meetingMinute.getMeeting();
        if ( meeting == null ) {
            return null;
        }
        Integer meetingId = meeting.getMeetingId();
        if ( meetingId == null ) {
            return null;
        }
        return meetingId;
    }

    private String entityMeetingTitle(MeetingMinute meetingMinute) {
        if ( meetingMinute == null ) {
            return null;
        }
        Meeting meeting = meetingMinute.getMeeting();
        if ( meeting == null ) {
            return null;
        }
        String title = meeting.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }

    private String entityRecordedByUserNameKh(MeetingMinute meetingMinute) {
        if ( meetingMinute == null ) {
            return null;
        }
        User recordedBy = meetingMinute.getRecordedBy();
        if ( recordedBy == null ) {
            return null;
        }
        String userNameKh = recordedBy.getUserNameKh();
        if ( userNameKh == null ) {
            return null;
        }
        return userNameKh;
    }

    private String entityAttachmentPathFilePath(MeetingMinute meetingMinute) {
        if ( meetingMinute == null ) {
            return null;
        }
        Attachment attachmentPath = meetingMinute.getAttachmentPath();
        if ( attachmentPath == null ) {
            return null;
        }
        String filePath = attachmentPath.getFilePath();
        if ( filePath == null ) {
            return null;
        }
        return filePath;
    }
}
