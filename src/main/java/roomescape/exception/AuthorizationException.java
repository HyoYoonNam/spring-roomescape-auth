package roomescape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public class AuthorizationException extends RoomEscapeException implements ErrorResponse {

    public AuthorizationException() {
        super("인증에 실패했습니다. 올바른 자격 증명이 필요합니다.");
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public ProblemDetail getBody() {
        return ProblemDetail.forStatusAndDetail(
                getStatusCode(),
                getMessage()
        );
    }
}
