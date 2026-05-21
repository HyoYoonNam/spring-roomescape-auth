package roomescape.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.infraastructure.AdminInterceptor;
import roomescape.infraastructure.ClientTypeInterceptor;
import roomescape.infraastructure.LoginInterceptor;
import roomescape.infraastructure.LoginMemberArgumentResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final ClientTypeInterceptor clientTypeInterceptor;

    public WebMvcConfig(
            LoginInterceptor loginInterceptor,
            AdminInterceptor adminInterceptor,
            LoginMemberArgumentResolver loginMemberArgumentResolver,
            ClientTypeInterceptor clientTypeInterceptor
    ) {
        this.loginInterceptor = loginInterceptor;
        this.adminInterceptor = adminInterceptor;
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
        this.clientTypeInterceptor = clientTypeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(clientTypeInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/reservations", "/api/reservations/**", "/members/me");
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }
}
