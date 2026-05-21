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

import java.time.LocalDate;
import java.util.Map;

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
        // manager1 (ID: 6)은 잠실점(ID: 1) 관리 (data.sql 기준)
        String manager1Token = getAccessToken("manager1@store.com", "managerPassword");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + manager1Token)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("", hasSize(20)); // 잠실점 예약 개수 (data.sql 기준 20개)
    }

    @DisplayName("다수의 매장을 관리하는 어드민은 모든 관리 매장의 예약을 조회할 수 있다")
    @Sql("/data.sql")
    @Test
    void 어드민_다수_매장_예약_조회_테스트() {
        // admin (ID: 5)은 잠실점(ID: 1), 강남점(ID: 2) 모두 관리
        String adminToken = getAccessToken("admin@roomescape.com", "adminPassword");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + adminToken)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("", hasSize(25)); // 전체 예약 개수
    }

    @DisplayName("매니저가 다른 매장의 예약을 상세 조회하려고 하면 403 Forbidden을 응답한다")
    @Sql("/data.sql")
    @Test
    void 매니저_다른_매장_예약_상세_조회_실패_테스트() {
        // manager1 (잠실점 관리)
        String manager1Token = getAccessToken("manager1@store.com", "managerPassword");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + manager1Token)
                .when().get("/admin/reservations/19") // 감옥 탈출(테마 7, 강남점) 예약
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("매니저가 다른 매장에 예약을 대행(생성)하려고 하면 403 Forbidden을 응답한다")
    @Sql("/data.sql")
    @Test
    void 매니저_다른_매장_예약_생성_실패_테스트() {
        String manager1Token = getAccessToken("manager1@store.com", "managerPassword");

        Map<String, Object> requestBody = Map.of(
                "date", LocalDate.now().plusDays(5).toString(),
                "timeId", 1,
                "themeId", 1,
                "storeId", 2 // 권한이 없는 강남점(ID: 2) 지정
        );

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + manager1Token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post("/api/reservations") // 매니저가 직접 /api/reservations로 대행 예약하는 경우 가정
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("매니저가 다른 매장의 예약을 수정하려고 하면 403 Forbidden을 응답한다")
    @Sql("/data.sql")
    @Test
    void 매니저_다른_매장_예약_수정_실패_테스트() {
        String manager1Token = getAccessToken("manager1@store.com", "managerPassword");

        Map<String, Object> requestBody = Map.of(
                "date", LocalDate.now().plusDays(5).toString(),
                "timeId", 2
        );

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + manager1Token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().patch("/api/reservations/19") // 강남점 예약 수정 시도
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("매니저가 다른 매장의 예약을 취소하려고 하면 403 Forbidden을 응답한다")
    @Sql("/data.sql")
    @Test
    void 매니저_다른_매장_예약_삭제_실패_테스트() {
        String manager1Token = getAccessToken("manager1@store.com", "managerPassword");

        // reservation ID 19는 강남점(store_id=2) 소속 (member=4, time=1, theme=7)
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + manager1Token)
                .queryParam("date", LocalDate.now().plusDays(2).toString())
                .queryParam("timeId", 1)
                .queryParam("themeId", 7)
                .queryParam("storeId", 2)
                .when().delete("/api/reservations")
                .then().log().all()
                .statusCode(403);
    }
}

