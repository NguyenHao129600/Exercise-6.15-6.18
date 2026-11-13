package vn.designbook.chapter6;

import java.util.EnumMap;
import java.util.Objects;

/**
 * Bài 6.15: Factory tạo Command dạng anonymous class có khả năng Undo.
 * Các Command chỉ gọi thao tác dữ liệu của ProgramSchedule, không đụng đến lịch sử.
 */
final class ProgramCommands {
    private ProgramCommands() { }

    static UndoableCommand add(ProgramSchedule schedule, Show show, Program.Day day) {
        Objects.requireNonNull(schedule, "schedule");
        Objects.requireNonNull(show, "show");
        Objects.requireNonNull(day, "day");
        final Show newShow = show.copy();
        return new UndoableCommand() {
            private Show previous;
            private boolean applied;

            @Override
            public void execute() {
                requireNotApplied(applied);
                previous = schedule.get(day); // Lưu trạng thái trước khi thay đổi.
                schedule.set(day, newShow);
                applied = true;
            }

            @Override
            public void undo() {
                requireApplied(applied);
                schedule.set(day, previous); // Khôi phục ngày bị thay đổi.
                applied = false;
            }
        };
    }

    static UndoableCommand remove(ProgramSchedule schedule, Program.Day day) {
        Objects.requireNonNull(schedule, "schedule");
        Objects.requireNonNull(day, "day");
        return new UndoableCommand() {
            private Show previous;
            private boolean applied;

            @Override
            public void execute() {
                requireNotApplied(applied);
                previous = schedule.get(day);
                schedule.set(day, new NullShow());
                applied = true;
            }

            @Override
            public void undo() {
                requireApplied(applied);
                schedule.set(day, previous);
                applied = false;
            }
        };
    }

    static UndoableCommand clear(ProgramSchedule schedule) {
        Objects.requireNonNull(schedule, "schedule");
        return new UndoableCommand() {
            private EnumMap<Program.Day, Show> previous;
            private boolean applied;

            @Override
            public void execute() {
                requireNotApplied(applied);
                previous = schedule.snapshot(); // Lưu trạng thái của cả 7 ngày.
                schedule.clear();
                applied = true;
            }

            @Override
            public void undo() {
                requireApplied(applied);
                schedule.restore(previous);  // Khôi phục toàn bộ lịch.
                applied = false;
            }
        };
    }

    private static void requireNotApplied(boolean applied) {
        if (applied) throw new IllegalStateException("Lệnh đã thực thi, phải undo trước khi execute lại");
    }

    private static void requireApplied(boolean applied) {
        if (!applied) throw new IllegalStateException("Chưa thực thi lệnh nên không thể undo");
    }
}
