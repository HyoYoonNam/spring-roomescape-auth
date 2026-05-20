package roomescape.infreastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.annotation.AuthResponse;
import roomescape.dto.TokenResponseDto;

@Component
public class AuthTokenReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final ObjectMapper objectMapper;

    public AuthTokenReturnValueHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(AuthResponse.class) &&
                returnType.getParameterType().equals(TokenResponseDto.class);
    }

    @Override
    public void handleReturnValue(
            Object returnValue,
            MethodParameter returnType,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest
    ) throws IOException {
        mavContainer.setRequestHandled(true);
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();

        ClientType clientType = (ClientType) request.getAttribute(ClientTypeInterceptor.CLIENT_TYPE_ATTRIBUTE);
        TokenResponseDto tokenResponse = (TokenResponseDto) returnValue;

        if (clientType == ClientType.MOBILE) {
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
            return;
        }

        ResponseCookie cookie = ResponseCookie.from("token", tokenResponse.accessToken())
                .httpOnly(true)
                .path("/")
                .maxAge(3600)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
