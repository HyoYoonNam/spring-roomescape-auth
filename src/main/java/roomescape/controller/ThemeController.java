package roomescape.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.ThemeResponseDto;
import roomescape.service.ThemeService;

@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public List<ThemeResponseDto> readThemes() {
        return themeService.findAllThemes();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponseDto>> readPopularThemes() {
        List<ThemeResponseDto> popularThemes = themeService.findPopularThemes();
        return ResponseEntity.ok(popularThemes);
    }

    @GetMapping("/{id}")
    public ThemeResponseDto findById(@PathVariable Long id) {
        return themeService.findById(id);
    }
}
