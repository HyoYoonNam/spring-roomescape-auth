package roomescape.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.StoreResponseDto;
import roomescape.repository.StoreRepository;

@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreRepository storeRepository;

    public StoreController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @GetMapping
    public List<StoreResponseDto> readAll() {
        return storeRepository.findAll().stream()
                .map(StoreResponseDto::from)
                .toList();
    }
}
