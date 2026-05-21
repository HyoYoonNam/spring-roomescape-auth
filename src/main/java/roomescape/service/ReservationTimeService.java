package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationTime;
import roomescape.dto.ReservationTimeRequestDto;
import roomescape.dto.ReservationTimeResponseDto;
import roomescape.exception.ReservationTimeInUseException;
import roomescape.exception.ReservationTimeNotFoundException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;

@Service
@Transactional
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(
            ReservationTimeRepository reservationTimeRepository,
            ReservationRepository reservationRepository
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationTimeResponseDto addReservationTime(ReservationTimeRequestDto reservationTimeRequest) {
        ReservationTime reservationTime = ReservationTime.withoutId(reservationTimeRequest.startAt());

        ReservationTime savedTime = reservationTimeRepository.save(reservationTime);
        return ReservationTimeResponseDto.from(savedTime);
    }

    public List<ReservationTimeResponseDto> findAllReservationTime() {
        return reservationTimeRepository.findAll().stream()
                .map(ReservationTimeResponseDto::from)
                .toList();
    }

    public List<ReservationTimeResponseDto> findReservedTimes(LocalDate selectedDate, Long themeId) {
        return reservationTimeRepository.findReservedTimes(selectedDate, themeId)
                .stream()
                .map(ReservationTimeResponseDto::from)
                .toList();
    }

    public void deleteReservationTime(Long id) {
        if (reservationRepository.existsReservationByTimeId(id)) {
            throw new ReservationTimeInUseException("예약 시간 삭제 실패 (사용 중): " + id);
        }
        if (reservationTimeRepository.findById(id).isEmpty()) {
            throw new ReservationTimeNotFoundException("예약 시간 삭제 실패 (존재하지 않음): " + id);
        }
        reservationTimeRepository.delete(id);
    }
}
