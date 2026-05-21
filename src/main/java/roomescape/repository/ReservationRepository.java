package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.domain.Reservation;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    List<Reservation> findAll();

    List<Reservation> findAllByMemberId(Long memberId);

    Optional<Reservation> findById(Long id);

    Optional<Long> findReservationIdWith(LocalDate date, Long timeId, Long themeId, Long storeId);

    int update(Reservation reservation);

    void deleteById(Long id);

    int deleteReservationWith(Long memberId, LocalDate date, Long timeId, Long themeId, Long storeId);

    boolean existsReservationByTimeId(Long timeId);

    boolean existsReservationByThemeId(Long themeId);
}
