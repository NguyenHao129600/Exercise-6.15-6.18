package vn.designbook.chapter6;

/**
 * Nền của Exercise 6.15, tương ứng Program3 trong lời giải mẫu.
 * Chỉ tạo UndoableCommand; chưa có CommandProcessor hay lịch sử lệnh.
 *
 * Sử dụng chung Program.Day cho bốn bài để tránh định nghĩa trùng enum Day.
 * Không có bài thực hành hoặc chế độ chạy riêng cho Exercise 14.
 */
public final class Program3 {
    private final ProgramSchedule schedule = new ProgramSchedule();

    public UndoableCommand createAddCommand(Show show, Program.Day day) {
        return ProgramCommands.add(schedule, show, day);
    }

    public UndoableCommand createRemoveCommand(Program.Day day) {
        return ProgramCommands.remove(schedule, day);
    }

    public UndoableCommand createClearCommand() {
        return ProgramCommands.clear(schedule);
    }

    public Show get(Program.Day day) {
        return schedule.get(day);
    }

    @Override
    public String toString() {
        return schedule.describe();
    }
}
