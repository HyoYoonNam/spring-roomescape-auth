package roomescape.infraastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ClientTypeInterceptor implements HandlerInterceptor {

    public static final String CLIENT_TYPE_ATTRIBUTE = "client-type";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.contains("RoomescapeApp")) {
            request.setAttribute(CLIENT_TYPE_ATTRIBUTE, ClientType.MOBILE);
        } else {
            request.setAttribute(CLIENT_TYPE_ATTRIBUTE, ClientType.WEB);
        }
        return true;
    }
}
