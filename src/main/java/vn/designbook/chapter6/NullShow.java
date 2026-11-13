package vn.designbook.chapter6;

/** Null Object: không có suất diễn trong một ngày. */
public final class NullShow implements Show {
    @Override
    public String description() {
        return "(Không có suất diễn)";
    }

    @Override
    public int runningTime() {
        return 0;
    }

    @Override
    public Show copy() {
        return new NullShow();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NullShow;
    }

    @Override
    public int hashCode() {
        return NullShow.class.hashCode();
    }
}
