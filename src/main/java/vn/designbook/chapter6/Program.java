package vn.designbook.chapter6;

import java.util.Objects;

/**
 * Exercise 6.18: Program là API nghiệp vụ và ủy quyền xử lý command.
 * - ProgramSchedule: quản lý dữ liệu 7 ngày.
 * - ProgramCommands: tạo UndoableCommand.
 * - CommandProcessor: abstraction quản lý lịch sử Undo/Redo.
 *
 * Constructor injection theo sơ đồ Solutions để không phụ thuộc cụ thể
 * vào BasicCommandProcessor; Program tránh trở thành God Class.
 */
public final class Program {
    public enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    private final ProgramSchedule schedule = new ProgramSchedule();
    private final CommandProcessor aProcessor;

    /** Constructor Injection giống lời giải mẫu. */
    public Program(CommandProcessor processor) {
        this.aProcessor = Objects.requireNonNull(processor, "processor");
    }

    /** Tiện ích cho việc chạy demo và bài test. */
    public Program() {
        this(new BasicCommandProcessor());
    }

    public void add(Show show, Day day) {
        aProcessor.consume(createAddCommand(show, day));
    }

    public void remove(Day day) {
        aProcessor.consume(createRemoveCommand(day));
    }

    /** Minh họa trực tiếp sơ đồ Solution Exercise 18. */
    public void clear() {
        aProcessor.consume(createClearCommand());
    }

    // Factory methods private: client không thao tác Command trực tiếp.
    private UndoableCommand createAddCommand(Show show, Day day) {
        return ProgramCommands.add(schedule, show, day);
    }

    private UndoableCommand createRemoveCommand(Day day) {
        return ProgramCommands.remove(schedule, day);
    }

    private UndoableCommand createClearCommand() {
        return ProgramCommands.clear(schedule);
    }

    public Show get(Day day) {
        return schedule.get(day);
    }

    public void undoLast() { aProcessor.undoLast(); }

    public void redoLast() { aProcessor.redoLast(); }

    public boolean canUndo() { return aProcessor.canUndo(); }

    public boolean canRedo() { return aProcessor.canRedo(); }

    @Override
    public String toString() { return schedule.describe(); }
}
