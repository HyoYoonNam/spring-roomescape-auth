package roomescape.infraastructure;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration
public class ReturnValueHandlerConfig implements InitializingBean {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    private final AuthTokenReturnValueHandler authTokenReturnValueHandler;

    public ReturnValueHandlerConfig(
            RequestMappingHandlerAdapter requestMappingHandlerAdapter,
            AuthTokenReturnValueHandler authTokenReturnValueHandler
    ) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
        this.authTokenReturnValueHandler = authTokenReturnValueHandler;
    }

    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>(requestMappingHandlerAdapter.getReturnValueHandlers());
        handlers.add(0, authTokenReturnValueHandler);
        requestMappingHandlerAdapter.setReturnValueHandlers(handlers);
    }
}
