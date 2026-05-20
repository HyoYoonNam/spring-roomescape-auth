package roomescape.config;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import roomescape.infreastructure.AuthTokenReturnValueHandler;
import roomescape.infreastructure.ClientTypeInterceptor;
import roomescape.infreastructure.LoginInterceptor;
import roomescape.infreastructure.LoginMemberArgumentResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final ClientTypeInterceptor clientTypeInterceptor;

    public WebMvcConfig(
            LoginInterceptor loginInterceptor,
            LoginMemberArgumentResolver loginMemberArgumentResolver,
            ClientTypeInterceptor clientTypeInterceptor
    ) {
        this.loginInterceptor = loginInterceptor;
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
        this.clientTypeInterceptor = clientTypeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(clientTypeInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/reservations/**", "/members/me");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }
}
