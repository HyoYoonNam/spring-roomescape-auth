package roomescape.infreastructure;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.annotation.Login;
import roomescape.domain.Member;
import roomescape.service.AuthService;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthorizationExtractor<String> authorizationExtractor;
    private final AuthService authService;

    public LoginMemberArgumentResolver(AuthService authService) {
        this.authorizationExtractor = new BearerAuthorizationExtractor();
        this.authService = authService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Login.class) &&
                parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = authorizationExtractor.extract(request);
        return authService.findMemberByToken(token);
    }
}
