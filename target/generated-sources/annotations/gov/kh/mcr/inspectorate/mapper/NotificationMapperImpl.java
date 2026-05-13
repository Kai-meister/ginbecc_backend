package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response.NotificationResponse;
import gov.kh.mcr.inspectorate.entity.Notification;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-06T23:53:46+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toResponse(Notification entity) {
        if ( entity == null ) {
            return null;
        }

        NotificationResponse.NotificationResponseBuilder notificationResponse = NotificationResponse.builder();

        notificationResponse.notificationId( entity.getNotificationId() );
        notificationResponse.title( entity.getTitle() );
        notificationResponse.message( entity.getMessage() );
        notificationResponse.type( entity.getType() );
        notificationResponse.referenceId( entity.getReferenceId() );
        notificationResponse.isRead( entity.getIsRead() );
        notificationResponse.createdAt( entity.getCreatedAt() );

        return notificationResponse.build();
    }
}
