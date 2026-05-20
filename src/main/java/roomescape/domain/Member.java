package roomescape.domain;

public class Member {

    private Long id;
    private final String loginId;
    private final String name;
    private final String password;

    public Member(Long id, String loginId, String name, String password) {
        validate(loginId, name, password);
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.password = password;
    }

    public static Member withoutId(String loginId, String name, String password) {
        return new Member(null, loginId, name, password);
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                '}';
    }

    private static void validate(String loginId, String name, String password) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("로그인 아이디는 비어있을 수 없습니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("회원 이름은 비어있을 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
        }
    }
}
