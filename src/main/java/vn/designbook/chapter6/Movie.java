package vn.designbook.chapter6;

import java.util.Objects;

/** Một bộ phim là một Show; đối tượng không thay đổi sau khi khởi tạo. */
public final class Movie implements Show {
    private final String title;
    private final int year;
    private final int minutes;

    public Movie(String title, int year, int minutes) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Tiêu đề phim không được rỗng");
        }
        if (year <= 0 || minutes <= 0) {
            throw new IllegalArgumentException("Năm và thời lượng phải lớn hơn 0");
        }
        this.title = title;
        this.year = year;
        this.minutes = minutes;
    }

    @Override
    public String description() {
        return title + " (" + year + ")";
    }

    @Override
    public int runningTime() {
        return minutes;
    }

    @Override
    public Show copy() {
        return new Movie(title, year, minutes);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Movie m)) return false;
        return year == m.year && minutes == m.minutes && title.equals(m.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, year, minutes);
    }
}
