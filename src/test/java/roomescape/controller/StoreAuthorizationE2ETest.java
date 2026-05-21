package roomescape.controller;

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

import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StoreAuthorizationE2ETest {

    private String getAccessToken(String email, String password) {
        return RestAssured.given().log().all()
                .header("User-Agent", "RoomescapeApp")
                .contentType(ContentType.JSON)
                .body(new TokenRequestDto(email, password))
                .when().post("/login")
                .then().log().all()
                .extract().as(TokenResponseDto.class).accessToken();
    }

    @DisplayName("매니저는 본인이 관리하는 매장의 예약만 조회할 수 있다")
    @Sql("/data.sql")
    @Test
    void 매니저_예약_조회_인가_테스트() {
        // manager1 (ID: 3)은 잠실점(ID: 1) 관리
        String manager1Token = getAccessToken("manager1@store.com", "managerPassword");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + manager1Token)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("", hasSize(20)); // 잠실점 테마(ID 1~6)에 대한 예약 개수 (6+5+3+2+2+2 = 20)
    }

    @DisplayName("매니저가 다른 매장의 예약을 상세 조회하려고 하면 403 Forbidden을 응답한다")
    @Sql("/data.sql")
    @Test
    void 매니저_다른_매장_예약_상세_조회_실패_테스트() {
        String manager1Token = getAccessToken("manager1@store.com", "managerPassword");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + manager1Token)
                .when().get("/admin/reservations/19") // 감옥 탈출(테마 7, 강남점) 예약
                .then().log().all()
                .statusCode(403);
    }
}
