package roomescape.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.dto.TokenRequestDto;
import roomescape.dto.TokenResponseDto;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LoginControllerE2ETest {

    private static final String SAMPLE_LOGIN_ID = "sample@sample.com";
    private static final String NOT_EXISTS_LOGIN_ID = "notExistsEmail";
    private static final String SAMPLE_PASSWORD = "samplePassword";
    private static final String NOT_EXISTS_PASSWORD = "notExistsPassword";

    @Nested
    class 로그인_시나리오 {

        @Autowired
        JdbcTemplate jdbcTemplate;

        @DisplayName("로그인에 성공하면 토큰을 발급받는다")
        @Test
        void 로그인_성공() {
            jdbcTemplate.update(
                    "INSERT INTO member (id, login_id, name, password) VALUES (?, ?, ?, ?)",
                    1, SAMPLE_LOGIN_ID, "루드비코", SAMPLE_PASSWORD
            );

            RestAssured
                    .given().log().all()
                    .body(new TokenRequestDto(SAMPLE_LOGIN_ID, SAMPLE_PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/login")
                    .then().log().all().extract().as(TokenResponseDto.class).accessToken();
        }

        @DisplayName("로그인에 실패하면 401 Unauthorzied를 응답한다")
        @Test
        void 로그인_실패() {
            RestAssured
                    .given().log().all()
                    .body(new TokenRequestDto(NOT_EXISTS_LOGIN_ID, NOT_EXISTS_PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/login")
                    .then().log().all()
                    .statusCode(401);
        }
    }
}
