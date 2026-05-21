package roomescape.controller;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import roomescape.dto.TokenRequestDto;
import roomescape.dto.TokenResponseDto;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationControllerE2ETest {

    private static final LocalDate PAST_DATE = LocalDate.now().minusDays(1);
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(1);
    private String accessToken;

    private String getAccessToken() {
        if (accessToken != null) return accessToken;

        accessToken = RestAssured.given()
                .header("User-Agent", "RoomescapeApp")
                .contentType(ContentType.JSON)
                .body(new TokenRequestDto("sample@sample.com", "samplePassword"))
                .when().post("/login")
                .then().extract().as(TokenResponseDto.class).accessToken();
        return accessToken;
    }

    @Nested
    class 인증_실패_케이스 {

        @DisplayName("토큰 없이 예약을 조회하면 401 Unauthorized를 응답한다")
        @Test
        void 토큰없이_예약울_조회하면_401을_응답한다() {
            RestAssured.given().log().all()
                    .when().get("/api/reservations")
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("토큰 없이 예약을 생성하면 401 Unauthorized를 응답한다")
        @Test
        void 토큰없이_예약을_생성하면_401을_응답한다() {
            Map<String, Object> requestBody = Map.of(
                    "date", FUTURE_DATE,
                    "timeId", 1,
                    "themeId", 1,
                    "storeId", 1
            );

            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when().post("/api/reservations")
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("토큰 없이 예약을 변경하면 401 Unauthorized를 응답한다")
        @Test
        void 토큰없이_예약을_변경하면_401을_응답한다() {
            Map<String, Object> requestBody = Map.of(
                    "date", FUTURE_DATE,
                    "timeId", 1
            );

            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when().patch("/api/reservations/1")
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("토큰 없이 예약을 취소하면 401 Unauthorized를 응답한다")
        @Test
        void 토큰없이_예약을_취소하면_401을_응답한다() {
            RestAssured.given().log().all()
                    .when().delete("/api/reservations")
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("유효하지 않은 토큰으로 요청하면 401 Unauthorized를 응답한다")
        @Test
        void 유효하지_않은_토큰으로_요청하면_401을_응답한다() {
            RestAssured.given().log().all()
                    .header("Authorization", "Bearer invalid-token-here")
                    .when().get("/api/reservations")
                    .then().log().all()
                    .statusCode(401);
        }
    }

    @Nested
    class 예약_생성_케이스 {

        @DisplayName("정상 요청에 대한 예약을 생성한다")
        @Sql("/initialize_theme_and_time.sql")
        @Test
        void 예약_생성() {
            // given
            Map<String, Object> requestBody = Map.of(
                    "date", FUTURE_DATE,
                    "timeId", 1,
                    "themeId", 1,
                    "storeId", 1
            );

            // when
            Response response = RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when().post("/api/reservations");

            // then
            response.then().log().all()
                    .statusCode(201);

            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAdminToken())
                    .when().get("/admin" + response.getHeader(HttpHeaders.LOCATION))
                    .then().log().all()
                    .statusCode(200)
                    .body("name", is("루드비코"))
                    .body("date", is(FUTURE_DATE.toString()))
                    .body("time.id", is(1))
                    .body("theme.id", is(1));
        }

        private String getAdminToken() {
            return RestAssured.given()
                    .header("User-Agent", "RoomescapeApp")
                    .contentType(ContentType.JSON)
                    .body(Map.of("loginId", "admin@roomescape.com", "password", "adminPassword"))
                    .when().post("/login")
                    .then().extract().path("accessToken");
        }

        @DisplayName("지난 시점으로 예약하면 422 Unprocessable Entity를 응답한다")
        @Sql("/initialize_theme_and_time.sql")
        @Test
        void 지난_시점을_예약하면_422를_응답한다() {
            Map<String, Object> requestBodyWithPastDateTime = Map.of(
                    "date", PAST_DATE,
                    "timeId", 1,
                    "themeId", 1,
                    "storeId", 1
            );

            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(requestBodyWithPastDateTime)
                    .when().post("/api/reservations")
                    .then().log().all()
                    .statusCode(422);
        }

        @DisplayName("예약 생성 시 필수 파라미터가 누락되면 400 Bad Request를 응답한다")
        @Sql({"/initialize_theme_and_time.sql"})
        @ParameterizedTest(name = "{0}")
        @MethodSource("provideInvalidReservationRequests")
        void 필수_파라미터가_누락되면_400을_응답한다(String description, Map<String, Object> invalidRequest) {
            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(invalidRequest)
                    .when().post("/api/reservations")
                    .then().log().all()
                    .statusCode(400);
        }

        @DisplayName("JSON 본문의 필드 타입이 일치하지 않으면 400 Bad Request를 응답한다")
        @Sql("/initialize_theme_and_time.sql")
        @Test
        void JSON_본문의_필드_타입이_일치하지_않으면_400을_응답한다() {
            Map<String, Object> requestBodyWithTypeMismatch = Map.of(
                    "date", FUTURE_DATE,
                    "timeId", "not-a-number",
                    "themeId", 1
            );

            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(requestBodyWithTypeMismatch)
                    .when().post("/api/reservations")
                    .then().log().all()
                    .statusCode(400)
                    .body("title", is("잘못된 요청 본문"));
        }

        @DisplayName("같은 날짜/시간/테마로 중복 예약하면 409 Conflict를 응답한다")
        @Sql("/initialize_theme_and_time.sql")
        @Test
        void 날짜와_시간_그리고_테마가_중복된_예약은_409를_응답한다() {
            Map<String, Object> requestBodyWithDuplicatedReservation = Map.of(
                    "date", FUTURE_DATE,
                    "timeId", 1,
                    "themeId", 1,
                    "storeId", 1
            );

            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(requestBodyWithDuplicatedReservation)
                    .when().post("/api/reservations")
                    .then().log().all()
                    .statusCode(201);

            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(requestBodyWithDuplicatedReservation)
                    .when().post("/api/reservations")
                    .then().log().all()
                    .statusCode(409);
        }

        @DisplayName("올바르지 않은 날짜 형식으로 예약하면 422 Unprocessable Entity를 응답한다")
        @Sql("/initialize_theme_and_time.sql")
        @Test
        void 날짜_형식이_올바르지_않게_예약하면_422를_응답한다() {
            String illegalDateFormat = "YYYY/MM/dd";
            Map<String, Object> requestBodyWithIllegalDateFormat = Map.of(
                    "date", FUTURE_DATE.format(DateTimeFormatter.ofPattern(illegalDateFormat)),
                    "timeId", 1,
                    "themeId", 1,
                    "storeId", 1
            );

            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(requestBodyWithIllegalDateFormat)
                    .when().post("/api/reservations")
                    .then().log().all()
                    .statusCode(422);
        }

        private static Stream<Arguments> provideInvalidReservationRequests() {
            return Stream.of(
                    Arguments.of("date 누락", Map.of("timeId", 1, "themeId", 1, "storeId", 1)),
                    Arguments.of("timeId 누락", Map.of("date", FUTURE_DATE, "themeId", 1, "storeId", 1)),
                    Arguments.of("themeId 누락", Map.of("date", FUTURE_DATE, "timeId", 1, "storeId", 1)),
                    Arguments.of("storeId 누락", Map.of("date", FUTURE_DATE, "timeId", 1, "themeId", 1))
            );
        }
    }

    @Nested
    class 예약_조회_케이스 {

        @DisplayName("사용자가 자신의 예약을 조회한다")
        @Sql("/data.sql")
        @Test
        void 자신의_예약_조회() {
            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .when().get("/api/reservations")
                    .then().log().all()
                    .statusCode(200)
                    .body("size()", is(8));
        }

    }

    @Nested
    class 예약_취소_케이스 {

        @DisplayName("예약 취소에 성공하면 204 No Content를 응답한다")
        @Sql("/data.sql")
        @Test
        void 예약을_성공적으로_취소하면_204를_응답한다() {
            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .queryParam("date", LocalDate.now().plusDays(1).toString())
                    .queryParam("timeId", 1)
                    .queryParam("themeId", 1)
                    .queryParam("storeId", 1)
                    .when().delete("/api/reservations")
                    .then().log().all()
                    .statusCode(204);
        }

        @DisplayName("이전 시점의 예약 취소를 요청하면 422 Unprocessable Entity를 응답한다")
        @Sql("/initialize_theme_and_time.sql")
        @Test
        void 이전_시점의_예약_취소를_요청하면_422를_응답한다() {
            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .queryParam("date", LocalDate.now().minusDays(7).toString())
                    .queryParam("timeId", 1)
                    .queryParam("themeId", 1)
                    .queryParam("storeId", 1)
                    .when().delete("/api/reservations")
                    .then().log().all()
                    .statusCode(422);
        }

        @DisplayName("존재하지 않는 예약 취소를 요청하면 422 Unprocessable Entity를 응답한다")
        @Sql("/initialize_theme_and_time.sql")
        @Test
        void 존재하지_않는_예약_취소를_요청하면_422를_응답한다() {
            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .queryParam("date", LocalDate.now().plusDays(1).toString())
                    .queryParam("timeId", Long.MAX_VALUE)
                    .queryParam("themeId", Long.MAX_VALUE)
                    .queryParam("storeId", 1)
                    .when().delete("/api/reservations")
                    .then().log().all()
                    .statusCode(422);
        }
    }

    @Nested
    class 예약_변경_케이스 {

        @DisplayName("예약 변경에 성공하면 204 No Content를 응답한다")
        @Sql("/data.sql")
        @Test
        void 예약을_성공적으로_변경하면_204를_응답한다() {
            Map<String, Object> requestBody = Map.of(
                    "date", LocalDate.now().plusDays(7L),
                    "timeId", 1L
            );

            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when().patch("api/reservations/" + 24L)
                    .then().log().all()
                    .statusCode(204);
        }

        @DisplayName("변경 사항이 없다면 204 No Content를 응답한다")
        @Sql("/data.sql")
        @Test
        void 업데이트_하려는_값이_기존과_같은_경우_204를_응답한다() {
            Map<String, Object> requestBody = Map.of(
                    "date", LocalDate.now().plusDays(1),
                    "timeId", 1L
            );

            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when().patch("api/reservations/" + 23L)
                    .then().log().all()
                    .statusCode(204);
        }

        @DisplayName("과거 시점으로 변경을 요청하면 422 Unprocessable Entity를 응답한다")
        @Sql("/initialize_theme_and_time.sql")
        @Test
        void 과거_시점으로_변경을_요청하면_422를_응답한다() {
            Map<String, Object> requestBody = Map.of(
                    "date", LocalDate.now().minusDays(1),
                    "timeId", 1L
            );

            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when().patch("api/reservations/" + 23L)
                    .then().log().all()
                    .statusCode(422);
        }

        @DisplayName("이미 다른 예약이 존재하는 시점으로 변경을 요청하면 409 Conflict를 응답한다")
        @Sql("/data.sql")
        @Test
        void 이미_다른_예약이_존재하는_시점으로_변경을_요청하면_409를_응답한다() {
            Map<String, Object> requestBody = Map.of(
                    "date", LocalDate.now().plusDays(2),
                    "timeId", 1L
            );

            RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when().patch("api/reservations/" + 23L)
                    .then().log().all()
                    .statusCode(409);
        }
    }
}
