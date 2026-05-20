package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.Map;
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
class ReservationAuthorizationE2ETest {

    private String getAccessToken(String email, String password) {
        return RestAssured.given()
                .header("User-Agent", "RoomescapeApp")
                .contentType(ContentType.JSON)
                .body(new TokenRequestDto(email, password))
                .when().post("/login")
                .then().extract().as(TokenResponseDto.class).accessToken();
    }

    @DisplayName("다른 사용자의 예약을 변경하려고 하면 403 Forbidden을 응답한다")
    @Sql("/auth_test_data.sql")
    @Test
    void 다른_사용자의_예약을_변경하려고_하면_403을_응답한다() {
        String user2Token = getAccessToken("user2@email.com", "password");
        
        Map<String, Object> requestBody = Map.of(
                "date", LocalDate.now().plusDays(2),
                "timeId", 2L
        );

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + user2Token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().patch("/api/reservations/1")
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("다른 사용자의 예약을 취소하려고 하면 403 Forbidden을 응답한다")
    @Sql("/auth_test_data.sql")
    @Test
    void 다른_사용자의_예약을_취소하려고_하면_403을_응답한다() {
        String user2Token = getAccessToken("user2@email.com", "password");
        
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + user2Token)
                .queryParam("date", LocalDate.now().plusDays(1).toString())
                .queryParam("timeId", 1)
                .queryParam("themeId", 1)
                .when().delete("/api/reservations")
                .then().log().all()
                .statusCode(403);
    }
}
