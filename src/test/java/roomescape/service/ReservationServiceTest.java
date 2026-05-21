package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.Member;
import roomescape.dto.LoginMember;
import roomescape.dto.ReservationRequestDto;
import roomescape.dto.ReservationResponseDto;
import roomescape.dto.ReservationUpdateDtoDateAndTimeIdOnly;
import roomescape.exception.DuplicatedReservationException;
import roomescape.exception.PastDateReservationException;
import roomescape.exception.PastDateCancellationException;
import roomescape.exception.PastDateModificationException;
import roomescape.exception.ReservationNotFoundException;
import roomescape.exception.ReservationTimeNotFoundException;
import roomescape.repository.JdbcMemberRepository;
import roomescape.repository.JdbcReservationRepository;
import roomescape.repository.JdbcReservationTimeRepository;
import roomescape.repository.JdbcStoreRepository;
import roomescape.repository.JdbcThemeRepository;
import roomescape.repository.MemberRepository;

@JdbcTest
@Import({JdbcReservationRepository.class, JdbcReservationTimeRepository.class, JdbcThemeRepository.class,
        JdbcMemberRepository.class,
        JdbcStoreRepository.class, ReservationService.class})
@Sql(value = "/initialize_theme_and_time.sql")
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    MemberRepository memberRepository;

    private LoginMember getLoginMember() {
        Member member = memberRepository.findById(1L).orElseThrow();
        return new LoginMember(member.getId(), Member.Role.USER);
    }

    @DisplayName("예약을 생성한다")
    @Test
    void ReservationRequestDTO를_받아_ReservationResponseDTO를_리턴한다() {
        ReservationRequestDto reservationRequestDTO = new ReservationRequestDto(
                LocalDate.now().plusDays(1), 1L, 1L, 1L
        );

        ReservationResponseDto addedReservation = reservationService.reserve(getLoginMember(), reservationRequestDTO);

        assertThat(addedReservation.date()).isEqualTo(reservationRequestDTO.date());
        assertThat(addedReservation.time().id()).isEqualTo(reservationRequestDTO.timeId());
        assertThat(addedReservation.theme().id()).isEqualTo(reservationRequestDTO.themeId());
    }

    @DisplayName("지나간 날짜/시간에 대한 예약은 거부한다")
    @Test
    void 지나간_시점에_대한_예약_요청에는_PastDateReservationException_예외를_던진다() {
        ReservationRequestDto outdatedRequest = new ReservationRequestDto(
                LocalDate.now().minusDays(1),
                1L,
                1L,
                1L
        );

        assertThatThrownBy(() -> reservationService.reserve(getLoginMember(), outdatedRequest))
                .isExactlyInstanceOf(PastDateReservationException.class);
    }

    @DisplayName("중복된 예약은 거부한다")
    @Test
    void 날짜와_시간_그리고_테마가_중복된_예약_요청에는_DuplicatedReservationException_예외를_던진다() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        long timeId = 1L;
        long themeId = 1L;
        ReservationRequestDto reservationRequestDTO = new ReservationRequestDto(
                date, timeId, themeId, 1L
        );
        reservationService.reserve(getLoginMember(), reservationRequestDTO);

        // when and then
        ReservationRequestDto duplicatedRequestDto = new ReservationRequestDto(date, timeId, themeId, 1L);
        assertThatThrownBy(() -> reservationService.reserve(getLoginMember(), duplicatedRequestDto))
                .isExactlyInstanceOf(DuplicatedReservationException.class);
    }

    @DisplayName("모든 예약을 조회한다")
    @Test
    void 존재하는_모든_예약의_ReservationResponseDTO가_담긴_리스트를_리턴한다() {
        // given
        ReservationRequestDto rudevicoReservationRequestDTO =
                new ReservationRequestDto(LocalDate.now().plusDays(1), 1L, 1L, 1L);

        ReservationRequestDto echoReservationRequestDTO =
                new ReservationRequestDto(LocalDate.now().plusDays(1), 1L, 2L, 1L);

        ReservationResponseDto rudevicoReservation = reservationService.reserve(getLoginMember(),
                rudevicoReservationRequestDTO);
        ReservationResponseDto echoReservation = reservationService.reserve(getLoginMember(),
                echoReservationRequestDTO);

        // when
        LoginMember manager = new LoginMember(2L, Member.Role.MANAGER);
        List<ReservationResponseDto> allReservations = reservationService.readAllReservation(manager);

        // then
        assertThat(allReservations)
                .hasSize(2)
                .containsExactlyInAnyOrder(rudevicoReservation, echoReservation);
    }

    @DisplayName("사용자가 본인의 예약을 취소한다")
    @Test
    void 사용자_이름과_날짜와_시간과_테마가_일치하는_예약을_취소한다() {
        // given
        ReservationRequestDto reservationRequestDTO = new ReservationRequestDto(
                LocalDate.now().plusDays(1), 1L, 1L, 1L
        );

        ReservationResponseDto addedReservation = reservationService.reserve(getLoginMember(), reservationRequestDTO);

        // when
        reservationService.cancelReservation(getLoginMember(), reservationRequestDTO);

        // then
        assertThatThrownBy(() -> reservationService.findById(addedReservation.id(), getLoginMember()))
                .isExactlyInstanceOf(ReservationNotFoundException.class);
    }

    @DisplayName("과거 시점의 예약은 취소할 수 없다")
    @Sql("/data.sql")
    @Test
    void 과거_시점의_예약을_취소하면_PastDateCancellationException을_던진다() {
        // when and then
        assertThatThrownBy(() -> reservationService.cancelReservation(
                getLoginMember(),
                new ReservationRequestDto(
                        LocalDate.now().minusDays(7),
                        1L,
                        1L,
                        1L
                )
        )).isExactlyInstanceOf(PastDateCancellationException.class);
    }

    @DisplayName("존재하지 않는 예약은 취소할 수 없다")
    @Sql("/initialize_theme_and_time.sql")
    @Test
    void 존재하지_않는_예약을_취소하면_ReservationNotFoundException을_던진다() {
        // when and then
        assertThatThrownBy(() -> reservationService.cancelReservation(
                getLoginMember(),
                new ReservationRequestDto(LocalDate.now().plusDays(1), 1L, 1L, 1L))
        ).isExactlyInstanceOf(ReservationNotFoundException.class);
    }

    @DisplayName("회원 정보로 예약을 조회한다")
    @Test
    void 회원이_일치하는_모든_예약의_ReservationResponseDTO가_담긴_리스트를_리턴한다() {
        // given
        ReservationRequestDto rudevicoReservationRequestDTO =
                new ReservationRequestDto(LocalDate.now().plusDays(1), 1L, 1L, 1L);

        ReservationRequestDto echoReservationRequestDTO =
                new ReservationRequestDto(LocalDate.now().plusDays(1), 2L, 1L, 1L);

        ReservationResponseDto rudevicoReservation = reservationService.reserve(getLoginMember(),
                rudevicoReservationRequestDTO);
        ReservationResponseDto echoReservation = reservationService.reserve(getLoginMember(),
                echoReservationRequestDTO);

        // when and then
        assertThat(reservationService.findAllByMember(getLoginMember()))
                .hasSize(2)
                .containsExactlyInAnyOrder(rudevicoReservation, echoReservation);
    }

    @DisplayName("사용자가 본인의 예약의 날짜나 시간을 변경한다")
    @Sql("/initialize_theme_and_time.sql")
    @Test
    void 예약_id가_일치하는_예약의_날짜나_시간을_변경하고_ReservationResponseDTO를_리턴한다() {
        // given
        ReservationResponseDto added = reservationService.reserve(getLoginMember(), new ReservationRequestDto(
                LocalDate.now().plusDays(1), 1L, 1L, 1L)
        );

        // when
        LocalDate dateForUpdate = added.date().plusDays(1);
        reservationService.update(
                getLoginMember(),
                added.id(),
                new ReservationUpdateDtoDateAndTimeIdOnly(
                        dateForUpdate,
                        added.time().id()
                )
        );

        // then
        ReservationResponseDto foundAfterUpdate = reservationService.findById(added.id(), getLoginMember());

        assertThat(foundAfterUpdate)
                .usingRecursiveComparison()
                .ignoringFields("date")
                .isEqualTo(added);

        assertThat(foundAfterUpdate.date()).isEqualTo(dateForUpdate);
    }

    @DisplayName("과거 시점으로 변경할 수는 없다")
    @Sql("/initialize_theme_and_time.sql")
    @Test
    void 과거_시점으로_변경하면_PastDateReservationException을_던진다() {
        // given
        ReservationResponseDto added = reservationService.reserve(getLoginMember(), new ReservationRequestDto(
                LocalDate.now().plusDays(1), 1L, 1L, 1L
        ));

        // when and then
        LocalDate dateForUpdate = added.date().minusDays(2);
        assertThatThrownBy(() -> reservationService.update(
                getLoginMember(),
                added.id(),
                new ReservationUpdateDtoDateAndTimeIdOnly(
                        dateForUpdate,
                        added.time().id()
                )
        )).isExactlyInstanceOf(PastDateReservationException.class);
    }

    @DisplayName("과거 시점의 예약을 변경할 수 없다")
    @Sql("/data.sql")
    @Test
    void 과거_시점의_예약을_변경하면_PastDateModificationException을_던진다() {
        assertThatThrownBy(() -> reservationService.update(
                getLoginMember(),
                1L,
                new ReservationUpdateDtoDateAndTimeIdOnly(
                        LocalDate.now().plusDays(1),
                        1L
                )
        )).isExactlyInstanceOf(PastDateModificationException.class);
    }

    @DisplayName("존재하지 않는 예약 시간으로 변경할 수는 없다")
    @Sql("/initialize_theme_and_time.sql")
    @Test
    void 존재하지_않는_예약_시간으로_변경하면_ReservationTimeNotFoundException을_던진다() {
        // given
        ReservationResponseDto added = reservationService.reserve(getLoginMember(), new ReservationRequestDto(
                LocalDate.now().plusDays(1), 1L, 1L, 1L
        ));

        // when and then
        LocalDate dateForUpdate = added.date().plusDays(1);
        assertThatThrownBy(() -> reservationService.update(
                getLoginMember(),
                added.id(),
                new ReservationUpdateDtoDateAndTimeIdOnly(
                        dateForUpdate,
                        Long.MAX_VALUE
                )
        )).isExactlyInstanceOf(ReservationTimeNotFoundException.class);
    }

    @DisplayName("존재하지 않는 예약을 변경할 수는 없다")
    @Sql("/initialize_theme_and_time.sql")
    @Test
    void 존재하지_않는_예약을_변경하면_ReservationNotFoundException을_던진다() {
        // given
        ReservationResponseDto added = reservationService.reserve(getLoginMember(), new ReservationRequestDto(
                LocalDate.now().plusDays(1), 1L, 1L, 1L
        ));

        // when and then
        LocalDate dateForUpdate = added.date().plusDays(1);
        assertThatThrownBy(() -> reservationService.update(
                getLoginMember(),
                Long.MAX_VALUE,
                new ReservationUpdateDtoDateAndTimeIdOnly(
                        dateForUpdate,
                        added.time().id()
                )
        )).isExactlyInstanceOf(ReservationNotFoundException.class);
    }

    @DisplayName("이미 다른 예약이 존재하는 시점으로 변경할 수는 없다")
    @Sql("/initialize_theme_and_time.sql")
    @Test
    void 이미_다른_예약이_존재하는_시점으로_예약을_변경하면_DuplicatedReservationException을_던진다() {
        // given
        ReservationResponseDto added = reservationService.reserve(getLoginMember(), new ReservationRequestDto(
                LocalDate.now().plusDays(1), 1L, 1L, 1L
        ));

        LocalDate dateForUpdate = added.date().plusDays(2);
        reservationService.reserve(getLoginMember(), new ReservationRequestDto(
                dateForUpdate, 1L, 1L, 1L
        ));

        // when and then
        assertThatThrownBy(() -> reservationService.update(
                getLoginMember(),
                added.id(),
                new ReservationUpdateDtoDateAndTimeIdOnly(
                        dateForUpdate,
                        added.time().id()
                )
        )).isExactlyInstanceOf(DuplicatedReservationException.class);
    }

    @DisplayName("변경 사항이 없다면 예외를 던지지 않는다")
    @Sql("/initialize_theme_and_time.sql")
    @Test
    void 변경_사항이_없다면_예외를_던지지_않는다() {
        // given
        ReservationResponseDto added = reservationService.reserve(getLoginMember(), new ReservationRequestDto(
                LocalDate.now().plusDays(1), 1L, 1L, 1L
        ));

        // when and then
        assertThatNoException().isThrownBy(() -> reservationService.update(
                getLoginMember(),
                added.id(),
                new ReservationUpdateDtoDateAndTimeIdOnly(
                        added.date(),
                        added.time().id()
                )
        ));
    }

    @DisplayName("수정 요청 시 데이터가 누락되면 IllegalArgumentException을 던진다")
    @Sql("/initialize_theme_and_time.sql")
    @Test
    void 수정_요청_시_데이터가_누락되면_IllegalArgumentException을_던진다() {
        // given
        ReservationResponseDto added = reservationService.reserve(getLoginMember(), new ReservationRequestDto(
                LocalDate.now().plusDays(1), 1L, 1L, 1L
        ));

        // when and then
        assertThatThrownBy(() -> reservationService.update(
                getLoginMember(),
                added.id(),
                new ReservationUpdateDtoDateAndTimeIdOnly(null, null)
        )).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
