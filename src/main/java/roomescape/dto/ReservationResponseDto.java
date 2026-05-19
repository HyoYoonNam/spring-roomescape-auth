package roomescape.dto;

import java.time.LocalDate;
import roomescape.domain.Reservation;

public record ReservationResponseDto(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeResponseDTO time,
        ThemeResponseDTO theme
) {

    public static ReservationResponseDto from(Reservation reservation) {
        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getName(),
                reservation.getDate(),
                ReservationTimeResponseDTO.from(reservation.getTime()),
                ThemeResponseDTO.from(reservation.getTheme())
        );
    }
}
