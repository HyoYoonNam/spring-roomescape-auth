package roomescape.dto;

import roomescape.domain.Member;

public record LoginMember(Long id, Member.Role role) {
    public boolean isManager() {
        return role == Member.Role.MANAGER;
    }
}
