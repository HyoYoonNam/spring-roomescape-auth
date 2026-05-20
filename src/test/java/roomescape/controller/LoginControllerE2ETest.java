package roomescape.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import roomescape.dto.MemberResponse;
import roomescape.dto.TokenRequestDto;
import roomescape.dto.TokenResponseDto;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql("/auth_test_data.sql")
class LoginControllerE2ETest {

    private static final String SAMPLE_LOGIN_ID = "user1@email.com";
    private static final String NOT_EXISTS_LOGIN_ID = "notExistsEmail";
    private static final String SAMPLE_PASSWORD = "password";
    private static final String NOT_EXISTS_PASSWORD = "notExistsPassword";
    private static final String SAMPLE_NAME = "유저1";

    @Nested
    class 로그인_시나리오 {

        @DisplayName("로그인에 성공하면 웹 브라우저에는 HttpOnly 쿠키를 발급한다")
        @Test
        void 로그인_성공_웹() {
            Response response = RestAssured
                    .given().log().all()
                    .body(new TokenRequestDto(SAMPLE_LOGIN_ID, SAMPLE_PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/login");

            response.then().log().all()
                    .statusCode(200)
                    .cookie("token", notNullValue());

            String setCookieHeader = response.getHeader(HttpHeaders.SET_COOKIE);
            assertThat(setCookieHeader).contains("HttpOnly");
        }

        @DisplayName("로그인에 성공하면 모바일 앱에는 JSON 바디로 토큰을 발급한다")
        @Test
        void 로그인_성공_모바일() {
            RestAssured
                    .given().log().all()
                    .header("User-Agent", "RoomescapeApp")
                    .body(new TokenRequestDto(SAMPLE_LOGIN_ID, SAMPLE_PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/login")
                    .then().log().all()
                    .statusCode(200)
                    .body("accessToken", notNullValue());
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

    @Nested
    class 로그인_이후_인증_시나리오 {

        @DisplayName("쿠키를 이용해 내 정보를 조회한다")
        @Test
        void 내_정보_조회_쿠키() {
            String token = RestAssured
                    .given().log().all()
                    .body(new TokenRequestDto(SAMPLE_LOGIN_ID, SAMPLE_PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/login")
                    .then().log().all()
                    .extract().cookie("token");

            MemberResponse member = RestAssured
                    .given().log().all()
                    .cookie("token", token)
                    .when().get("/members/me")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value()).extract().as(MemberResponse.class);

            assertThat(member.name()).isEqualTo(SAMPLE_NAME);
        }

        @DisplayName("Authorization 헤더를 이용해 내 정보를 조회한다")
        @Test
        void 내_정보_조회_헤더() {
            String accessToken = RestAssured
                    .given().log().all()
                    .header("User-Agent", "RoomescapeApp")
                    .body(new TokenRequestDto(SAMPLE_LOGIN_ID, SAMPLE_PASSWORD))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/login")
                    .then().log().all().extract().as(TokenResponseDto.class).accessToken();

            MemberResponse member = RestAssured
                    .given().log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when().get("/members/me")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value()).extract().as(MemberResponse.class);

            assertThat(member.name()).isEqualTo(SAMPLE_NAME);
        }

        @DisplayName("토큰 없이 내 정보를 조회하면 401 Unauthorized를 응답한다")
        @Test
        void 토큰없이_내정보를_조회하면_401을_응답한다() {
            RestAssured
                    .given().log().all()
                    .when().get("/members/me")
                    .then().log().all()
                    .statusCode(401);
        }
    }
}
