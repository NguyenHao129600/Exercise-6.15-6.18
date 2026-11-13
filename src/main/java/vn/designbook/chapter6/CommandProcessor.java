package vn.designbook.chapter6;

/**
 * Abstraction theo sơ đồ Solution 6.18.
 * Client chỉ gửi lệnh qua consume() và dùng Undo/Redo; không truy cập stack.
 */
public interface CommandProcessor {
    void consume(UndoableCommand command);
    void undoLast();
    void redoLast();

    // Truy vấn tiện ích, không làm lộ cấu trúc lưu lịch sử.
    boolean canUndo();
    boolean canRedo();
}
