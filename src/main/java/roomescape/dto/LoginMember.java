package roomescape.dto;

import roomescape.domain.Member;

public record LoginMember(Long id, String name, String loginId, Member.Role role) {
    public boolean isManager() {
        return role == Member.Role.MANAGER;
    }
}
