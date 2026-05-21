package roomescape.infraastructure;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.annotation.Login;
import roomescape.domain.Member;
import roomescape.dto.LoginMember;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthorizationExtractor<String> authorizationExtractor;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginMemberArgumentResolver(JwtTokenProvider jwtTokenProvider) {
        this.authorizationExtractor = new BearerAuthorizationExtractor();
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Login.class) &&
                parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
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

        jwtTokenProvider.validateToken(token);
        Long id = Long.parseLong(jwtTokenProvider.getPayload(token));
        Member.Role role = Member.Role.valueOf(jwtTokenProvider.getRole(token));

        return new LoginMember(id, role);
    }
}
