package roomescape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public class ForbiddenException extends BusinessException {

    public ForbiddenException() {
        super("해당 자원에 대한 권한이 없습니다.");
    }

    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public ProblemDetail getBody() {
        return ProblemDetail.forStatusAndDetail(
                getStatusCode(),
                getMessage()
        );
    }
}
