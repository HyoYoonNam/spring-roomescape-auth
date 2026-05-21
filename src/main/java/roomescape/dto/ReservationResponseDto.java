package roomescape.dto;

import java.time.LocalDate;
import roomescape.domain.Reservation;

public record ReservationResponseDto(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeResponseDto time,
        ThemeResponseDto theme,
        Long storeId
) {
    public static ReservationResponseDto from(Reservation reservation) {
        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getName(),
                reservation.getDate(),
                ReservationTimeResponseDto.from(reservation.getTime()),
                ThemeResponseDto.from(reservation.getTheme()),
                reservation.getStoreId()
        );
    }
}

