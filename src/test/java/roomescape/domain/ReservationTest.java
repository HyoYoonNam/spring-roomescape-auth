package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationTest {

    private final Member member = Member.withoutId("sample@sample.com", "루드비코", "samplePassword", Member.Role.USER);

    @DisplayName("회원 정보가 없는 예약은 생성할 수 없다")
    @Test
    void 회원_정보가_없으면_IllegalArgumentException_예외를_던진다() {
        assertThatThrownBy(() -> Reservation.withoutId(
                        null,
                        LocalDate.now(),
                        ReservationTime.withoutId(LocalTime.now()),
                        Theme.withoutId("sample theme", "sample description", "sample url"),
                        1L
                )
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("날짜가 없는 예약은 생성할 수 없다")
    @Test
    void 날짜가_없으면_IllegalArgumentException_예외를_던진다() {
        assertThatThrownBy(() -> Reservation.withoutId(
                        member,
                        null,
                        ReservationTime.withoutId(LocalTime.now()),
                        Theme.withoutId("sample theme", "sample description", "sample url"),
                        1L
                )
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("예약 시간이 없는 예약은 생성할 수 없다")
    @Test
    void 예약_시간이_없으면_IllegalArgumentException_예외를_던진다() {
        assertThatThrownBy(() -> Reservation.withoutId(
                        member,
                        LocalDate.now(),
                        null,
                        Theme.withoutId("sample theme", "sample description", "sample url"),
                        1L
                )
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테마가 없는 예약은 생성할 수 없다")
    @Test
    void 테마가_없으면_IllegalArgumentException_예외를_던진다() {
        assertThatThrownBy(() -> Reservation.withoutId(
                        member,
                        LocalDate.now(),
                        ReservationTime.withoutId(LocalTime.now()),
                        null,
                        1L
                )
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("예약 정보 수정 시 날짜가 없으면 예외를 던진다")
    @Test
    void 수정_시_날짜가_없으면_IllegalArgumentException_예외를_던진다() {
        Reservation reservation = Reservation.withoutId(
                member,
                LocalDate.now(),
                ReservationTime.withoutId(LocalTime.now()),
                Theme.withoutId("sample theme", "sample description", "sample url"),
                1L
        );

        assertThatThrownBy(() -> reservation.changeDateAndTime(null, ReservationTime.withoutId(LocalTime.now())))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("예약 정보 수정 시 시간이 없으면 예외를 던진다")
    @Test
    void 수정_시_시간이_없으면_IllegalArgumentException_예외를_던진다() {
        Reservation reservation = Reservation.withoutId(
                member,
                LocalDate.now(),
                ReservationTime.withoutId(LocalTime.now()),
                Theme.withoutId("sample theme", "sample description", "sample url"),
                1L
        );

        assertThatThrownBy(() -> reservation.changeDateAndTime(LocalDate.now(), null))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
