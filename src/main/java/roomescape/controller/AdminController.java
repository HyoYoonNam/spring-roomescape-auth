package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Login;
import roomescape.dto.LoginMember;
import roomescape.dto.ReservationResponseDto;
import roomescape.dto.ReservationTimeRequestDto;
import roomescape.dto.ReservationTimeResponseDto;
import roomescape.dto.ThemeRequestDto;
import roomescape.dto.ThemeResponseDto;
import roomescape.service.ReservationService;
import roomescape.service.ReservationTimeService;
import roomescape.service.ThemeService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ReservationService reservationService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;

    public AdminController(
            ReservationService reservationService,
            ReservationTimeService reservationTimeService,
            ThemeService themeService
    ) {
        this.reservationService = reservationService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
    }

    @GetMapping("/reservations")
    public List<ReservationResponseDto> readAllReservations(@Login LoginMember loginMember) {
        return reservationService.readAllReservation(loginMember);
    }

    @GetMapping("/reservations/{id}")
    public ReservationResponseDto findReservationById(@Login LoginMember loginMember, @PathVariable Long id) {
        return reservationService.findById(id, loginMember);
    }

    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponseDto> add(@Valid @RequestBody ReservationTimeRequestDto request) {
        ReservationTimeResponseDto saved = reservationTimeService.addReservationTime(request);
        return ResponseEntity.created(URI.create("/times/" + saved.id())).build();
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable Long id) {
        reservationTimeService.deleteReservationTime(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/themes")
    public ResponseEntity<ThemeResponseDto> add(@Valid @RequestBody ThemeRequestDto request) {
        ThemeResponseDto saved = themeService.addTheme(request);
        return ResponseEntity.created(URI.create("/themes/" + saved.id())).build();
    }

    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        themeService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
}
