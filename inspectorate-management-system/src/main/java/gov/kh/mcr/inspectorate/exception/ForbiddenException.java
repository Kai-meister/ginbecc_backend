package gov.kh.mcr.inspectorate.exception;


import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN,
                "FORBIDDEN", message);
    }
}