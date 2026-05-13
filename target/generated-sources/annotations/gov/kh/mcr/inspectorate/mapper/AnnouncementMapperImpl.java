package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.AnnouncementRequest;
import gov.kh.mcr.inspectorate.dto.response.AnnouncementResponse;
import gov.kh.mcr.inspectorate.entity.Announcement;
import gov.kh.mcr.inspectorate.entity.Attachment;
import gov.kh.mcr.inspectorate.entity.LookupAnnouncementStatus;
import gov.kh.mcr.inspectorate.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:15+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class AnnouncementMapperImpl implements AnnouncementMapper {

    @Override
    public Announcement toEntity(AnnouncementRequest request) {
        if ( request == null ) {
            return null;
        }

        Announcement.AnnouncementBuilder announcement = Announcement.builder();

        announcement.title( request.getTitle() );
        announcement.content( request.getContent() );
        announcement.publishAt( request.getPublishAt() );
        announcement.priority( request.getPriority() );

        return announcement.build();
    }

    @Override
    public AnnouncementResponse toResponse(Announcement entity) {
        if ( entity == null ) {
            return null;
        }

        AnnouncementResponse.AnnouncementResponseBuilder announcementResponse = AnnouncementResponse.builder();

        announcementResponse.createdByName( entityCreatedByUserNameKh( entity ) );
        announcementResponse.statusCode( entityStatusCodeStatusCode( entity ) );
        announcementResponse.statusLabel( entityStatusCodeLabelKh( entity ) );
        announcementResponse.attachmentUrl( entityAttachmentPathFilePath( entity ) );
        announcementResponse.announcementId( entity.getAnnouncementId() );
        announcementResponse.title( entity.getTitle() );
        announcementResponse.content( entity.getContent() );
        announcementResponse.publishAt( entity.getPublishAt() );
        announcementResponse.priority( entity.getPriority() );
        announcementResponse.createdAt( entity.getCreatedAt() );

        return announcementResponse.build();
    }

    @Override
    public void updateEntity(AnnouncementRequest request, Announcement entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getTitle() != null ) {
            entity.setTitle( request.getTitle() );
        }
        if ( request.getContent() != null ) {
            entity.setContent( request.getContent() );
        }
        if ( request.getPublishAt() != null ) {
            entity.setPublishAt( request.getPublishAt() );
        }
        if ( request.getPriority() != null ) {
            entity.setPriority( request.getPriority() );
        }
    }

    private String entityCreatedByUserNameKh(Announcement announcement) {
        if ( announcement == null ) {
            return null;
        }
        User createdBy = announcement.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        String userNameKh = createdBy.getUserNameKh();
        if ( userNameKh == null ) {
            return null;
        }
        return userNameKh;
    }

    private String entityStatusCodeStatusCode(Announcement announcement) {
        if ( announcement == null ) {
            return null;
        }
        LookupAnnouncementStatus statusCode = announcement.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String statusCode1 = statusCode.getStatusCode();
        if ( statusCode1 == null ) {
            return null;
        }
        return statusCode1;
    }

    private String entityStatusCodeLabelKh(Announcement announcement) {
        if ( announcement == null ) {
            return null;
        }
        LookupAnnouncementStatus statusCode = announcement.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String labelKh = statusCode.getLabelKh();
        if ( labelKh == null ) {
            return null;
        }
        return labelKh;
    }

    private String entityAttachmentPathFilePath(Announcement announcement) {
        if ( announcement == null ) {
            return null;
        }
        Attachment attachmentPath = announcement.getAttachmentPath();
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
