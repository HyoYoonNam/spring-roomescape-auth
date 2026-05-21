package roomescape.infreastructure;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.domain.Member;
import roomescape.exception.AuthorizationException;
import roomescape.exception.ForbiddenException;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final AuthorizationExtractor<String> authorizationExtractor;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.authorizationExtractor = new BearerAuthorizationExtractor();
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String token = extractToken(request);
        if (token == null) {
            throw new AuthorizationException();
        }

        jwtTokenProvider.validateToken(token);
        String role = jwtTokenProvider.getRole(token);

        if (!Member.Role.MANAGER.name().equals(role)) {
            throw new ForbiddenException("매니저 권한이 필요합니다.");
        }

        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String token = null;

        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (token == null) {
            token = authorizationExtractor.extract(request);
        }
        return token;
    }
}
