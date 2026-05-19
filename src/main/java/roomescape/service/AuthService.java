package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.dto.TokenRequestDto;
import roomescape.dto.TokenResponseDto;
import roomescape.exception.AuthorizationException;
import roomescape.infreastructure.JwtTokenProvider;
import roomescape.repository.MemberRepository;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public boolean checkInvalidLogin(String loginId, String password) {
        if (loginId == null || loginId.isBlank() || password == null || password.isBlank()) {
            return true;
        }
        return memberRepository.findByLoginId(loginId)
                .filter(member -> member.getPassword().equals(password))
                .stream()
                .findFirst()
                .isEmpty();
    }

    public TokenResponseDto createToken(TokenRequestDto tokenRequestDto) {
        if (checkInvalidLogin(tokenRequestDto.loginId(), tokenRequestDto.password())) {
            throw new AuthorizationException();
        }

        String accessToken = jwtTokenProvider.createToken(tokenRequestDto.loginId());
        return new TokenResponseDto(accessToken);
    }
}
