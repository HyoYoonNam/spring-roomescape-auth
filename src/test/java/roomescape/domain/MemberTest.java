package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {

    @DisplayName("로그인 아이디가 비어 있는 회원은 생성할 수 없다")
    @ParameterizedTest(name = "로그인 아이디가 [{0}]이면 생성할 수 없다.")
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void 로그인_아이디가_비어_있으면_IllegalArgumentException_예외를_던진다(String emptyLoginId) {
        assertThatThrownBy(() -> Member.withoutId(
                emptyLoginId,
                "루드비코",
                "password",
                Member.Role.USER
        ))
.isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이름이 비어 있는 회원은 생성할 수 없다")
    @ParameterizedTest(name = "이름이 [{0}]이면 생성할 수 없다.")
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void 이름이_비어_있으면_IllegalArgumentException_예외를_던진다(String emptyName) {
        assertThatThrownBy(() -> Member.withoutId(
                "loginId",
                emptyName,
                "password",
                Member.Role.USER
        ))
.isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("비밀번호가 비어 있는 회원은 생성할 수 없다")
    @ParameterizedTest(name = "비밀번호가 [{0}]이면 생성할 수 없다.")
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void 비밀번호가_비어_있으면_IllegalArgumentException_예외를_던진다(String emptyPassword) {
        assertThatThrownBy(() -> Member.withoutId(
                "loginId",
                "루드비코",
                emptyPassword,
                Member.Role.USER
        ))
.isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
