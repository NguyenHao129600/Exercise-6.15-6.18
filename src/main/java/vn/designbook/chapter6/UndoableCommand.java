package vn.designbook.chapter6;

/** Theo Solutions 6.15: mở rộng Command để hỗ trợ hoàn tác. */
public interface UndoableCommand extends Command {
    void undo();
}
