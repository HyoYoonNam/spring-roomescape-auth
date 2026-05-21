package roomescape.dto;

import roomescape.domain.Theme;

public record ThemeResponseDto(
        Long id,
        String name,
        String description,
        String imageUrl,
        Long runningTime
) {

    public static ThemeResponseDto from(Theme theme) {
        return new ThemeResponseDto(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getImageUrl(),
                theme.getRunningTime()
        );
    }
}
