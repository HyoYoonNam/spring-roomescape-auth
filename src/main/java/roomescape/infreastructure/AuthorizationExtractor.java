package roomescape.infreastructure;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthorizationExtractor<T> {

    String AUTHORIZATION = "Authorization";

    T extract(HttpServletRequest request);
}
