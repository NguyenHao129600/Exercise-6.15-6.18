# ĐỐI CHIẾU ĐỀ VÀ MÃ NGUỒN — EXERCISE 15–18

Phạm vi nộp: **chỉ Exercise 15, 16, 17, 18**. Exercise 14 không có class demo hay phần trình bày riêng; chỉ dùng thiết kế nền cho 15.

| Bài | Yêu cầu trong đề | Mã nguồn | Kiểm chứng |
|---|---|---|---|
| 15 | Làm Command có thể Undo; client hoàn tác và kiểm tra từng lệnh | `Command`, `UndoableCommand`, `Program3`, `ProgramCommands` | `Main.exercise15()`; regression 05–12, 41–42; JUnit `exercise15_*` |
| 16 | `CommandProcessor` execute+store+undo last, Law of Demeter | `CommandProcessor.consume()`, `BasicCommandProcessor.undoLast()` | `Main.exercise16()`; regression 13,17,19–20 |
| 17 | CommandProcessor hỗ trợ redo lệnh đã undo | `BasicCommandProcessor.redoLast()` và 2 stack | `Main.exercise17()`; regression 14–16,18,21,37 |
| 18 | Program add/remove/clear, undoLast qua processor; tránh God Class | `Program`, `CommandProcessor` interface, `BasicCommandProcessor`, `ProgramSchedule` | `Main.exercise18()`; regression 22–25,38,40,43–44 |

**Nguồn lời giải được đối chiếu:** nội dung source Java Solutions 15–17 và ảnh thiết kế 18 mà người học đã gửi trong cuộc hội thoại. **Không nhận định chương trình trùng từng dòng code gốc** vì đây là bản tự viết với thêm các cải tiến được ghi lại ở `so-sanh-solutions.md`.
