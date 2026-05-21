package roomescape.handler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.infraastructure.LoginInterceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest({BindingExceptionHandler.class, TestController.class})
class BindingExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginInterceptor loginInterceptor;

    @MockitoBean
    private roomescape.infraastructure.AdminInterceptor adminInterceptor;

    @MockitoBean
    private roomescape.infraastructure.LoginMemberArgumentResolver loginMemberArgumentResolver;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws Exception {
        when(loginInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        when(adminInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @DisplayName("필수 쿼리 파라미터가 누락되면 400 Bad Request를 응답한다")
    @Test
    void missingServletRequestParameterException을_400으로_변환한다() throws Exception {
        mockMvc.perform(get("/binding-test/missing-param"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("RequestBody 검증에 실패하면 400 Bad Request")
    @Test
    void methodArgumentNotValidException을_400으로_변환한다() throws Exception {
        mockMvc.perform(post("/binding-test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest());
    }
}
