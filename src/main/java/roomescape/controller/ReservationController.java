package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Login;
import roomescape.dto.LoginMember;
import roomescape.dto.ReservationRequestDTO;
import roomescape.dto.ReservationResponseDto;
import roomescape.dto.ReservationUpdateDtoDateAndTimeIdOnly;
import roomescape.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> readMyReservations(@Login LoginMember loginMember) {
        return ResponseEntity
                .ok(reservationService.findAllByMember(loginMember));
    }

    @PostMapping
    public ResponseEntity<Void> add(
            @Login LoginMember loginMember,
            @Valid @RequestBody ReservationRequestDTO reservationRequest
    ) {
        ReservationResponseDto saved = reservationService.reserve(loginMember, reservationRequest);
        return ResponseEntity
                .created(URI.create("/reservations/" + saved.id()))
                .build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(
            @Login LoginMember loginMember,
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateDtoDateAndTimeIdOnly updateDto
    ) {
        reservationService.update(loginMember, id, updateDto);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @Login LoginMember loginMember,
            @Valid @ModelAttribute ReservationRequestDTO reservationRequest
    ) {
        reservationService.cancelReservation(loginMember, reservationRequest);
        return ResponseEntity
                .noContent()
                .build();
    }
}
