package roomescape.domain;

public class Member {

    public enum Role {
        USER, MANAGER
    }

    private Long id;
    private final String loginId;
    private final String name;
    private final String password;
    private final Role role;

    public Member(Long id, String loginId, String name, String password, Role role) {
        validate(loginId, name, password, role);
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public static Member withoutId(String loginId, String name, String password, Role role) {
        return new Member(null, loginId, name, password, role);
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

    public Role getRole() {
        return role;
    }

    public boolean isManager() {
        return role == Role.MANAGER;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", role=" + role +
                '}';
    }

    private static void validate(String loginId, String name, String password, Role role) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("로그인 아이디는 비어있을 수 없습니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("회원 이름은 비어있을 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
        }
        if (role == null) {
            throw new IllegalArgumentException("역할은 비어있을 수 없습니다.");
        }
    }
}
