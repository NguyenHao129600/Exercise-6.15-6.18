package vn.designbook.chapter6;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Solutions 6.16–6.17: consume/undoLast + một ngăn xếp riêng để redo.
 * Đưa việc quản lý lịch sử vào lớp riêng nhằm tuân thủ Law of Demeter.
 *
 * Bổ sung so với code mẫu: lệnh mới xóa nhánh redo cũ và lịch sử không
 * bị sửa nếu execute/undo xảy ra lỗi.
 */
public final class BasicCommandProcessor implements CommandProcessor {
    private final Deque<UndoableCommand> executedCommands = new ArrayDeque<>();
    private final Deque<UndoableCommand> undoneCommands = new ArrayDeque<>();

    @Override
    public void consume(UndoableCommand command) {
        Objects.requireNonNull(command, "command");
        command.execute();
        executedCommands.push(command);
        undoneCommands.clear();
    }

    @Override
    public void undoLast() {
        if (!canUndo()) throw new IllegalStateException("Không còn lệnh để Undo");
        UndoableCommand command = executedCommands.peek();
        command.undo();
        executedCommands.pop();
        undoneCommands.push(command);
    }

    @Override
    public void redoLast() {
        if (!canRedo()) throw new IllegalStateException("Không còn lệnh để Redo");
        UndoableCommand command = undoneCommands.peek();
        command.execute();
        undoneCommands.pop();
        executedCommands.push(command);
        // Không gọi consume() vì consume() xóa nhánh redo còn lại.
    }

    @Override
    public boolean canUndo() { return !executedCommands.isEmpty(); }

    @Override
    public boolean canRedo() { return !undoneCommands.isEmpty(); }

    public int undoCount() { return executedCommands.size(); }

    public int redoCount() { return undoneCommands.size(); }
}
