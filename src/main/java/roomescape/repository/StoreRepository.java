package roomescape.repository;

import java.util.List;
import java.util.Optional;
import roomescape.domain.Store;

public interface StoreRepository {
    Store save(Store store);
    List<Store> findAll();
    Optional<Store> findById(Long id);
    List<Long> findStoreIdsByManagerId(Long managerId);
    void delete(Long id);
}
