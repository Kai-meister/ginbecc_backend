package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.entity.Attachment;
import gov.kh.mcr.inspectorate.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:15+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class AttachmentMapperImpl implements AttachmentMapper {

    @Override
    public AttachmentResponse toResponse(Attachment entity) {
        if ( entity == null ) {
            return null;
        }

        AttachmentResponse.AttachmentResponseBuilder attachmentResponse = AttachmentResponse.builder();

        attachmentResponse.fileUrl( entity.getFilePath() );
        attachmentResponse.uploadedBy( entityUploadedByUserNameKh( entity ) );
        attachmentResponse.attachmentId( entity.getAttachmentId() );
        attachmentResponse.filePath( entity.getFilePath() );
        attachmentResponse.originalName( entity.getOriginalName() );
        attachmentResponse.referenceType( entity.getReferenceType() );
        attachmentResponse.referenceId( entity.getReferenceId() );
        attachmentResponse.fileType( entity.getFileType() );
        attachmentResponse.fileSize( entity.getFileSize() );
        attachmentResponse.isActive( entity.getIsActive() );
        attachmentResponse.createdAt( entity.getCreatedAt() );
        attachmentResponse.updatedAt( entity.getUpdatedAt() );

        attachmentResponse.fileSizeDisplay( formatFileSize(entity.getFileSize()) );

        return attachmentResponse.build();
    }

    private String entityUploadedByUserNameKh(Attachment attachment) {
        if ( attachment == null ) {
            return null;
        }
        User uploadedBy = attachment.getUploadedBy();
        if ( uploadedBy == null ) {
            return null;
        }
        String userNameKh = uploadedBy.getUserNameKh();
        if ( userNameKh == null ) {
            return null;
        }
        return userNameKh;
    }
}
