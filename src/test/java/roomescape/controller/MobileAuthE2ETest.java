package roomescape.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import roomescape.dto.TokenRequestDto;
import roomescape.dto.TokenResponseDto;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MobileAuthE2ETest {

    @DisplayName("모바일 앱 시나리오: 로그인 후 발급받은 토큰을 헤더에 담아 요청을 보낸다")
    @Sql("/auth_test_data.sql")
    @Test
    void mobileAuthScenario() {
        // 1. 로그인 요청 (모바일 앱은 응답 바디에서 토큰을 추출)
        TokenResponseDto loginResponse = RestAssured.given().log().all()
                .header("User-Agent", "RoomescapeApp")
                .contentType(ContentType.JSON)
                .body(new TokenRequestDto("user1@email.com", "password"))
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .body("accessToken", is(notNullValue()))
                .extract().as(TokenResponseDto.class);

        String accessToken = loginResponse.accessToken();

        // 2. 인증이 필요한 API 호출 (Authorization 헤더에 Bearer 토큰 포함)
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/members/me")
                .then().log().all()
                .statusCode(200)
                .body("name", is("유저1"));
    }

    @DisplayName("인증 정보가 유효하지 않은 모바일 요청은 거부한다")
    @Test
    void invalidMobileAuth() {
        RestAssured.given().log().all()
                .header("Authorization", "Bearer invalid-token-value")
                .when().get("/members/me")
                .then().log().all()
                .statusCode(401);
    }

    @DisplayName("인증 정보가 없는 모바일 요청은 거부한다")
    @Test
    void missingMobileAuth() {
        RestAssured.given().log().all()
                .when().get("/members/me")
                .then().log().all()
                .statusCode(401);
    }
}
