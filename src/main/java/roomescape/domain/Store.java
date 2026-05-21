package roomescape.domain;

public class Store {
    private final Long id;
    private final String name;

    public Store(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Store withoutId(String name) {
        return new Store(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
