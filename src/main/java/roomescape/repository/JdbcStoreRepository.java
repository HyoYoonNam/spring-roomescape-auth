package roomescape.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Store;

@Repository
public class JdbcStoreRepository implements StoreRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStoreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Store save(Store store) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("store")
                .usingGeneratedKeyColumns("id");

        long generatedKey = insert.executeAndReturnKey(
                new BeanPropertySqlParameterSource(store)
        ).longValue();

        return new Store(generatedKey, store.getName());
    }

    @Override
    public List<Store> findAll() {
        return jdbcTemplate.query(
                "SELECT id, name FROM store",
                (rs, rowNum) -> new Store(rs.getLong("id"), rs.getString("name"))
        );
    }

    @Override
    public Optional<Store> findById(Long id) {
        List<Store> stores = jdbcTemplate.query(
                "SELECT id, name FROM store WHERE id = ?",
                (rs, rowNum) -> new Store(rs.getLong("id"), rs.getString("name")),
                id
        );
        return stores.stream().findFirst();
    }

    @Override
    public List<Long> findStoreIdsByManagerId(Long managerId) {
        return jdbcTemplate.query(
                "SELECT store_id FROM manager_store WHERE member_id = ?",
                (rs, rowNum) -> rs.getLong("store_id"),
                managerId
        );
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM store WHERE id = ?", id);
    }
}
