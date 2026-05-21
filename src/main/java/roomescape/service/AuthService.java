package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Member;
import roomescape.dto.TokenRequestDto;
import roomescape.dto.TokenResponseDto;
import roomescape.exception.AuthorizationException;
import roomescape.infraastructure.JwtTokenProvider;
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

        Member member = memberRepository.findByLoginId(tokenRequestDto.loginId())
                .orElseThrow(AuthorizationException::new);

        String accessToken = jwtTokenProvider.createToken(member.getId().toString(), member.getRole().name());
        return new TokenResponseDto(accessToken);
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(AuthorizationException::new);
    }
}
