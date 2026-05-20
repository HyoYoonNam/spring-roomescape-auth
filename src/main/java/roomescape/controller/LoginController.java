package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Login;
import roomescape.dto.LoginMember;
import roomescape.dto.MemberResponse;
import roomescape.dto.TokenRequestDto;
import roomescape.dto.TokenResponseDto;
import roomescape.service.AuthService;

@RestController
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * ex) request sample
     * <p>
     * POST /login/token HTTP/1.1
     * accept: application/json
     * content-type: application/json; charset=UTF-8
     * <p>
     * {
     * "loginId": "sample@sample.com",
     * "password": "samplePassword"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> tokenLogin(@RequestBody TokenRequestDto tokenRequest) {
        TokenResponseDto tokenResponse = authService.createToken(tokenRequest);
        return ResponseEntity
                .ok()
                .body(tokenResponse);
    }

    @GetMapping("/members/me")
    public ResponseEntity<MemberResponse> findMe(@Login LoginMember loginMember) {
        return ResponseEntity
                .ok()
                .body(MemberResponse.from(loginMember));
    }
}
