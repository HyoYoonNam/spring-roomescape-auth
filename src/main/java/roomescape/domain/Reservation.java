package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {

    private final Long id;
    private final Member member;
    private LocalDate date;
    private ReservationTime time;
    private final Theme theme;

    public Reservation(Long id, Member member, LocalDate date, ReservationTime time, Theme theme) {
        validate(member, date, time, theme);
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    public static Reservation withoutId(Member member, LocalDate date, ReservationTime time, Theme theme) {
        return new Reservation(null, member, date, time, theme);
    }

    public void changeDateAndTime(LocalDate date, ReservationTime time) {
        validateDate(date);
        validateTime(time);
        this.date = date;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getName() {
        return member.getName();
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(date, time.getStartAt());
    }

    public Long getTimeId() {
        return time.getId();
    }

    public Long getThemeId() {
        return theme.getId();
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", member=" + member +
                ", date=" + date +
                ", time=" + time +
                ", theme=" + theme +
                '}';
    }

    private void validate(Member member, LocalDate date, ReservationTime time, Theme theme) {
        validateMember(member);
        validateDate(date);
        validateTime(time);
        validateTheme(theme);
    }

    private void validateMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("회원 정보는 필수입니다.");
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 필수입니다.");
        }
    }

    private void validateTime(ReservationTime time) {
        if (time == null) {
            throw new IllegalArgumentException("예약 시간은 필수입니다.");
        }
    }

    private void validateTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("테마는 필수입니다.");
        }
    }
}
