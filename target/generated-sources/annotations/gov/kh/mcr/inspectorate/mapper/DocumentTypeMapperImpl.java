package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request.DocumentTypeRequest;
import gov.kh.mcr.inspectorate.dto.response.DocumentTypeResponse;
import gov.kh.mcr.inspectorate.entity.DocumentType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:16+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class DocumentTypeMapperImpl implements DocumentTypeMapper {

    @Override
    public DocumentType toEntity(DocumentTypeRequest request) {
        if ( request == null ) {
            return null;
        }

        DocumentType.DocumentTypeBuilder documentType = DocumentType.builder();

        documentType.documentTypeCode( request.getDocumentTypeCode() );
        documentType.documentTypeName( request.getDocumentTypeName() );
        documentType.description( request.getDescription() );
        documentType.status( request.getStatus() );

        return documentType.build();
    }

    @Override
    public DocumentTypeResponse toResponse(DocumentType entity) {
        if ( entity == null ) {
            return null;
        }

        DocumentTypeResponse.DocumentTypeResponseBuilder documentTypeResponse = DocumentTypeResponse.builder();

        documentTypeResponse.documentTypeId( entity.getDocumentTypeId() );
        documentTypeResponse.documentTypeCode( entity.getDocumentTypeCode() );
        documentTypeResponse.documentTypeName( entity.getDocumentTypeName() );
        documentTypeResponse.description( entity.getDescription() );
        documentTypeResponse.status( entity.getStatus() );
        documentTypeResponse.createdAt( entity.getCreatedAt() );

        return documentTypeResponse.build();
    }

    @Override
    public void updateEntity(DocumentTypeRequest request, DocumentType entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getDocumentTypeCode() != null ) {
            entity.setDocumentTypeCode( request.getDocumentTypeCode() );
        }
        if ( request.getDocumentTypeName() != null ) {
            entity.setDocumentTypeName( request.getDocumentTypeName() );
        }
        if ( request.getDescription() != null ) {
            entity.setDescription( request.getDescription() );
        }
        if ( request.getStatus() != null ) {
            entity.setStatus( request.getStatus() );
        }
    }
}
