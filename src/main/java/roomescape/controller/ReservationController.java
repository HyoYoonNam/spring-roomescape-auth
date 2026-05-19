package roomescape.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import roomescape.domain.Member;
import roomescape.dto.ReservationRequestDTO;
import roomescape.dto.ReservationResponseDto;
import roomescape.dto.ReservationUpdateDtoDateAndTimeIdOnly;
import roomescape.infreastructure.AuthorizationExtractor;
import roomescape.infreastructure.BearerAuthorizationExtractor;
import roomescape.service.AuthService;
import roomescape.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final AuthService authService;
    private final AuthorizationExtractor<String> authorizationExtractor;

    public ReservationController(ReservationService reservationService, AuthService authService) {
        this.reservationService = reservationService;
        this.authService = authService;
        this.authorizationExtractor = new BearerAuthorizationExtractor();
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> readMyReservations(HttpServletRequest request) {
        String token = authorizationExtractor.extract(request);
        Member member = authService.findMemberByToken(token);
        return ResponseEntity
                .ok(reservationService.findAllByMember(member));
    }

    @PostMapping
    public ResponseEntity<Void> add(
            HttpServletRequest request,
            @Valid @RequestBody ReservationRequestDTO reservationRequest
    ) {
        String token = authorizationExtractor.extract(request);
        Member member = authService.findMemberByToken(token);
        ReservationResponseDto saved = reservationService.reserve(member, reservationRequest);
        return ResponseEntity
                .created(URI.create("/reservations/" + saved.id()))
                .build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateDtoDateAndTimeIdOnly updateDto
    ) {
        reservationService.update(id, updateDto);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            HttpServletRequest request,
            @Valid @ModelAttribute ReservationRequestDTO reservationRequest
    ) {
        String token = authorizationExtractor.extract(request);
        Member member = authService.findMemberByToken(token);
        reservationService.cancelReservation(member, reservationRequest);
        return ResponseEntity
                .noContent()
                .build();
    }
}
