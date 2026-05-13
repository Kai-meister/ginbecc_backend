package gov.kh.mcr.inspectorate.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends ApiException {

    public BusinessException(String message) {
        super(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", message);
    }
}