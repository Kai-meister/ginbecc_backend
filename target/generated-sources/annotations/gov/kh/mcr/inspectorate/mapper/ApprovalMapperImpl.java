package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response.ApprovalResponse;
import gov.kh.mcr.inspectorate.entity.Approval;
import gov.kh.mcr.inspectorate.entity.Document;
import gov.kh.mcr.inspectorate.entity.LookupDocumentStatus;
import gov.kh.mcr.inspectorate.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T07:28:15+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class ApprovalMapperImpl implements ApprovalMapper {

    @Override
    public ApprovalResponse toResponse(Approval entity) {
        if ( entity == null ) {
            return null;
        }

        ApprovalResponse.ApprovalResponseBuilder approvalResponse = ApprovalResponse.builder();

        approvalResponse.documentId( entityDocumentDocumentId( entity ) );
        approvalResponse.documentName( entityDocumentDocumentName( entity ) );
        approvalResponse.requestedByName( entityRequestedByUserNameKh( entity ) );
        approvalResponse.approvedByName( entityApprovedByUserNameKh( entity ) );
        approvalResponse.statusCode( entityStatusCodeStatusCode( entity ) );
        approvalResponse.statusLabel( entityStatusCodeLabelKh( entity ) );
        approvalResponse.approvalId( entity.getApprovalId() );
        approvalResponse.requestedAt( entity.getRequestedAt() );
        approvalResponse.comment( entity.getComment() );
        approvalResponse.approvedAt( entity.getApprovedAt() );

        return approvalResponse.build();
    }

    private Integer entityDocumentDocumentId(Approval approval) {
        if ( approval == null ) {
            return null;
        }
        Document document = approval.getDocument();
        if ( document == null ) {
            return null;
        }
        Integer documentId = document.getDocumentId();
        if ( documentId == null ) {
            return null;
        }
        return documentId;
    }

    private String entityDocumentDocumentName(Approval approval) {
        if ( approval == null ) {
            return null;
        }
        Document document = approval.getDocument();
        if ( document == null ) {
            return null;
        }
        String documentName = document.getDocumentName();
        if ( documentName == null ) {
            return null;
        }
        return documentName;
    }

    private String entityRequestedByUserNameKh(Approval approval) {
        if ( approval == null ) {
            return null;
        }
        User requestedBy = approval.getRequestedBy();
        if ( requestedBy == null ) {
            return null;
        }
        String userNameKh = requestedBy.getUserNameKh();
        if ( userNameKh == null ) {
            return null;
        }
        return userNameKh;
    }

    private String entityApprovedByUserNameKh(Approval approval) {
        if ( approval == null ) {
            return null;
        }
        User approvedBy = approval.getApprovedBy();
        if ( approvedBy == null ) {
            return null;
        }
        String userNameKh = approvedBy.getUserNameKh();
        if ( userNameKh == null ) {
            return null;
        }
        return userNameKh;
    }

    private String entityStatusCodeStatusCode(Approval approval) {
        if ( approval == null ) {
            return null;
        }
        LookupDocumentStatus statusCode = approval.getStatusCode();
        if ( statusCode == null ) {
            return null;
        }
        String statusCode1 = statusCode.getStatusCode();
        if ( statusCode1 == null ) {
            return null;
        }
        return statusCode1;
    }

    private String entityStatusCodeLabelKh(Approval approval) {
        if ( approval == null ) {
            return null;
        }
        LookupDocumentStatus statusCode = approval.getStatusCode();
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
