package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Theme;
import roomescape.dto.ThemeRequestDto;
import roomescape.dto.ThemeResponseDto;
import roomescape.exception.ThemeInUseException;
import roomescape.exception.ThemeNotFoundException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;

@Service
@Transactional
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeService(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ThemeResponseDto addTheme(ThemeRequestDto request) {
        Theme theme =
                Theme.withoutId(request.name(), request.description(), request.imageUrl());
        Theme savedTheme = themeRepository.save(theme);
        return ThemeResponseDto.from(savedTheme);
    }

    public List<ThemeResponseDto> findAllThemes() {
        return themeRepository.findAll()
                .stream()
                .map(ThemeResponseDto::from)
                .toList();
    }

    public ThemeResponseDto findById(Long id) {
        Theme result = themeRepository.findById(id)
                .orElseThrow(() -> new ThemeNotFoundException("ID로 테마 조회 실패: " + id));
        return ThemeResponseDto.from(result);
    }

    public List<ThemeResponseDto> findPopularThemes() {
        return themeRepository.findPopularThemes()
                .stream()
                .map(ThemeResponseDto::from)
                .toList();
    }

    public void deleteTheme(Long id) {
        if (reservationRepository.existsReservationByThemeId(id)) {
            throw new ThemeInUseException("테마 삭제 실패 (사용 중): " + id);
        }
        if (themeRepository.findById(id).isEmpty()) {
            throw new ThemeNotFoundException("테마 삭제 실패 (존재하지 않음): " + id);
        }
        themeRepository.delete(id);
    }
}
