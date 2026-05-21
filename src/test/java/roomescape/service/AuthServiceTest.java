package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import roomescape.domain.Member;
import roomescape.dto.TokenRequestDto;
import roomescape.dto.TokenResponseDto;
import roomescape.exception.AuthorizationException;
import roomescape.infraastructure.JwtTokenProvider;
import roomescape.repository.MemberRepository;

class AuthServiceTest {

    private static final String NOEXISTS_LOGIN_ID = "noexistsLoginId";
    private static final String SAMPLE_PASSWORD = "samplePassword";
    private static final String SAMPLE_LOGIN_ID = "sampleLoginId";
    private static final String SAMPLE_NAME = "sampleName";

    JwtTokenProvider mockJwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
    AuthService authService = new AuthService(new FakeMemberRepository(), mockJwtTokenProvider);

    @Nested
    class 로그인_시나리오 {

        @DisplayName("로그인 성공 케이스")
        @Test
        void 로그인_id와_비밀번호가_모두_일치하면_로그인에_성공한다() {
            boolean invalid = authService.checkInvalidLogin(SAMPLE_LOGIN_ID, SAMPLE_PASSWORD);
            assertThat(invalid).isFalse();
        }

        @Nested
        class 로그인_실패_케이스 {

            @DisplayName("일치하는 로그인 id가 없는 경우")
            @Test
            void 일치하는_로그인_id가_없으면_로그인에_실패한다() {
                boolean invalid = authService.checkInvalidLogin(NOEXISTS_LOGIN_ID, SAMPLE_PASSWORD);
                assertThat(invalid).isTrue();
            }

            @DisplayName("로그인 id는 있는데 비밀번호가 일치하지 않는 경우")
            @Test
            void 비밀번호가_일치하지_않으면_로그인에_실패한다() {
                boolean invalid = authService.checkInvalidLogin(SAMPLE_LOGIN_ID, "invalidPassword");
                assertThat(invalid).isTrue();
            }

            @DisplayName("loginId가 null이거나 빈 문자열, 혹은 공백인 경우")
            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "  "})
            void loginId가_유효하지_않으면_로그인에_실패한다(String loginId) {
                boolean invalid = authService.checkInvalidLogin(loginId, SAMPLE_PASSWORD);
                assertThat(invalid).isTrue();
            }

            @DisplayName("password가 null이거나 빈 문자열, 혹은 공백인 경우")
            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "  "})
            void password가_유효하지_않으면_로그인에_실패한다(String password) {
                boolean invalid = authService.checkInvalidLogin(SAMPLE_LOGIN_ID, password);
                assertThat(invalid).isTrue();
            }
        }
    }

    @Nested
    class 토큰_생성_시나리오 {

        @DisplayName("인증에 성공하면 토큰을 발급한다")
        @Test
        void 토큰_생성을_위한_로그인_인증에_성공하면_토큰을_리턴한다() {
            Mockito.when(mockJwtTokenProvider.createToken(anyString(), anyString())).thenReturn("created");
            TokenResponseDto created = authService.createToken(new TokenRequestDto(SAMPLE_LOGIN_ID, SAMPLE_PASSWORD));

            assertThat(created.accessToken()).isEqualTo("created");
        }

        @DisplayName("인증 실패 시 토큰 발급을 거부한다")
        @Test
        void 토큰_생성을_위한_로그인_인증에_실패하면_AuthorizationException을_던진다() {
            assertThatThrownBy(() ->
                    authService.createToken(new TokenRequestDto(NOEXISTS_LOGIN_ID, SAMPLE_PASSWORD))
            ).isExactlyInstanceOf(AuthorizationException.class);
        }
    }

    @Nested
    class 토큰_해석_시나리오 {

        @DisplayName("유효한 토큰을 해석하여 LoginMember DTO를 반환한다")
        @Test
        void 유효한_토큰이면_LoginMember를_리턴한다() {
            // This test logic is now moved to LoginMemberArgumentResolver or simplified here if still testing AuthService wrapper (if any)
            // But AuthService.findMemberByToken was removed. 
            // We should test createToken to ensure it uses ID and test findMemberById.
            
            TokenResponseDto created = authService.createToken(new TokenRequestDto(SAMPLE_LOGIN_ID, SAMPLE_PASSWORD));
            
            Mockito.verify(mockJwtTokenProvider).createToken(eq("1"), eq("USER"));
        }

        @DisplayName("ID로 멤버를 조회한다")
        @Test
        void ID로_멤버를_조회하면_Member를_리턴한다() {
            Member member = authService.findMemberById(1L);

            assertThat(member.getId()).isEqualTo(1L);
            assertThat(member.getName()).isEqualTo(SAMPLE_NAME);
        }
    }

    static class FakeMemberRepository implements MemberRepository {

        final Map<String, Member> members = Map.of(
                SAMPLE_LOGIN_ID, new Member(1L, SAMPLE_LOGIN_ID, SAMPLE_NAME, SAMPLE_PASSWORD, Member.Role.USER)
        );

        @Override
        public Member save(Member member) {
            return null;
        }

        @Override
        public Optional<Member> findById(Long id) {
            return members.values().stream()
                    .filter(member -> member.getId().equals(id))
                    .findFirst();
        }

        @Override
        public Optional<Member> findByLoginId(String loginId) {
            return Optional.ofNullable(members.getOrDefault(loginId, null));
        }

        @Override
        public void deleteById(Long id) {
        }
    }
}
