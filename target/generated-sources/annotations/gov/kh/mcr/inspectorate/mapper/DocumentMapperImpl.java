package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.DocumentRequest;
import gov.kh.mcr.inspectorate.dto.response.DocumentResponse;
import gov.kh.mcr.inspectorate.entity.Attachment;
import gov.kh.mcr.inspectorate.entity.Document;
import gov.kh.mcr.inspectorate.entity.DocumentType;
import gov.kh.mcr.inspectorate.entity.LookupDocumentStatus;
import gov.kh.mcr.inspectorate.entity.Officer;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:15+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class DocumentMapperImpl implements DocumentMapper {

    @Override
    public Document toEntity(DocumentRequest request) {
        if ( request == null ) {
            return null;
        }

        Document.DocumentBuilder document = Document.builder();

        document.documentName( request.getDocumentName() );
        document.documentNumber( request.getDocumentNumber() );
        document.note( request.getNote() );
        document.expiryDate( request.getExpiryDate() );

        return document.build();
    }

    @Override
    public DocumentResponse toResponse(Document entity) {
        if ( entity == null ) {
            return null;
        }

        DocumentResponse.DocumentResponseBuilder documentResponse = DocumentResponse.builder();

        documentResponse.officerName( entityOfficerFullNameKh( entity ) );
        documentResponse.documentTypeName( entityDocumentTypeDocumentTypeName( entity ) );
        documentResponse.statusCode( entityStatusCodeStatusCode( entity ) );
        documentResponse.statusLabel( entityStatusCodeLabelKh( entity ) );
        documentResponse.fileUrl( entityAttachmentFilePath( entity ) );
        documentResponse.documentId( entity.getDocumentId() );
        documentResponse.documentName( entity.getDocumentName() );
        documentResponse.documentNumber( entity.getDocumentNumber() );
        documentResponse.note( entity.getNote() );
        documentResponse.expiryDate( entity.getExpiryDate() );
        documentResponse.createdAt( entity.getCreatedAt() );

        return documentResponse.build();
    }

    private String entityOfficerFullNameKh(Document document) {
        if ( document == null ) {
            return null;
        }
        Officer officer = document.getOfficer();
        if ( officer == null ) {
            return null;
        }
        String fullNameKh = officer.getFullNameKh();
        if ( fullNameKh == null ) {
            return null;
        }
        return fullNameKh;
    }

    private String entityDocumentTypeDocumentTypeName(Document document) {
        if ( document == null ) {
            return null;
        }
        DocumentType documentType = document.getDocumentType();
        if ( documentType == null ) {
            return null;
        }
        String documentTypeName = documentType.getDocumentTypeName();
        if ( documentTypeName == null ) {
            return null;
        }
        return documentTypeName;
    }

    private String entityStatusCodeStatusCode(Document document) {
        if ( document == null ) {
            return null;
        }
        LookupDocumentStatus statusCode = document.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String statusCode1 = statusCode.getStatusCode();
        if ( statusCode1 == null ) {
            return null;
        }
        return statusCode1;
    }

    private String entityStatusCodeLabelKh(Document document) {
        if ( document == null ) {
            return null;
        }
        LookupDocumentStatus statusCode = document.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String labelKh = statusCode.getLabelKh();
        if ( labelKh == null ) {
            return null;
        }
        return labelKh;
    }

    private String entityAttachmentFilePath(Document document) {
        if ( document == null ) {
            return null;
        }
        Attachment attachment = document.getAttachment();
        if ( attachment == null ) {
            return null;
        }
        String filePath = attachment.getFilePath();
        if ( filePath == null ) {
            return null;
        }
        return filePath;
    }
}
