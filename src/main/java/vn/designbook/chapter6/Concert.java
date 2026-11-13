package vn.designbook.chapter6;

import java.util.Objects;

/** Buổi hòa nhạc: thêm để kiểm chứng Program lưu được đa hình Show. */
public final class Concert implements Show {
    private final String title;
    private final String performer;
    private final int duration;

    public Concert(String title, String performer, int duration) {
        if (title == null || title.isBlank() || performer == null || performer.isBlank()) {
            throw new IllegalArgumentException("Tên chương trình và nghệ sĩ không được rỗng");
        }
        if (duration <= 0) throw new IllegalArgumentException("Thời lượng phải dương");
        this.title = title;
        this.performer = performer;
        this.duration = duration;
    }

    @Override public String description() { return title + " by " + performer; }
    @Override public int runningTime() { return duration; }
    @Override public Show copy() { return new Concert(title, performer, duration); }
    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Concert c)) return false;
        return duration == c.duration && title.equals(c.title) && performer.equals(c.performer);
    }
    @Override public int hashCode() { return Objects.hash(title, performer, duration); }
}
