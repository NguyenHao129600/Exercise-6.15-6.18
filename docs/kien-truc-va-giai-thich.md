# KIẾN TRÚC VÀ GIẢI THÍCH CODE — EXERCISE 15–18

## Tổng quan

- `Show`, `Movie`, `Concert`, `NullShow`: mô hình suất biểu diễn.
- `ProgramSchedule`: `EnumMap<Day, Show>` lưu suất của bảy ngày; `snapshot()` và `restore()` phục vụ Undo Clear.
- `Command`, `UndoableCommand`: Command Pattern để đóng gói một thao tác, cùng khả năng hoàn tác.
- `ProgramCommands`: factory tạo 3 Command ẩn danh, giữ trạng thái cũ tại chính command.
- `Program3`: facade cho Exercise 15 để client tự tạo, execute và undo lệnh.
- `CommandProcessor`: interface lưu các phương thức `consume`, `undoLast`, `redoLast`.
- `BasicCommandProcessor`: quản lý lịch sử trong hai stack kín.
- `Program`: facade nghiệp vụ Exercise 18, nhận processor qua constructor, không chứa stack.

## Exercise 15 — Vì sao Undo được?

### `ProgramCommands.add()`

Khi `execute()` chạy, command lưu `previous = schedule.get(day)` rồi `schedule.set(day, newShow)`. Khi `undo()`, command dùng `schedule.set(day, previous)` để khôi phục suất cũ, kể cả trường hợp thêm đè lên suất khác. Cờ `applied` ngăn execute hai lần liên tiếp hoặc undo khi chưa execute.

### `ProgramCommands.remove()`

`execute()` lưu suất hiện tại của ngày đó vào `previous`, thay bằng `NullShow`. `undo()` đặt lại `previous`.

### `ProgramCommands.clear()`

`execute()` dùng `snapshot()` lưu toàn bộ bảy ngày trước khi `clear()`. `undo()` dùng `restore(previous)` để đưa lại lịch cũ. Bản lưu sao chép dữ liệu, tránh bị thay đổi gián tiếp qua đối tượng gốc.

### `Main.exercise15()`

Thứ tự: `add Movie A` → `add Movie B` → `remove A` → `clear`. Sau đó Undo `clear` → `remove A` → `add B` → `add A`; **mỗi bước đều assert trạng thái lịch**. Lệnh Add A đầu tiên được chạy thử `execute → undo → execute` theo mẫu Solution 15.

## Exercise 16 — Processor và Law of Demeter

`BasicCommandProcessor.consume(command)` chạy command và `push()` lên `executedCommands`. `undoLast()` chỉ tác động đến lệnh ở đỉnh stack. Mọi list/stack đều `private`, client không truy cập chuỗi `processor.getStack().remove...`; chỉ được phép `processor.consume(cmd)` hoặc `processor.undoLast()`.

## Exercise 17 — Redo

Sau Undo, command chuyển từ `executedCommands` sang `undoneCommands`. Khi Redo, command được execute lại rồi quay về `executedCommands`. Nếu sau Undo người dùng gọi một lệnh **mới**, `consume()` xóa `undoneCommands` vì chuỗi trạng thái đã rẽ sang nhánh mới. Cần phân biệt **Redo nhiều lần** với **tạo lệnh mới sau Undo**.

## Exercise 18 — Tránh God Class như thế nào?

- `Program.add/remove/clear`: chứa logic điều phối ngắn, gọi `aProcessor.consume(createXCommand(...))`.
- Các `createXCommand()` trong `Program` đều private; client không cần biết Command có tồn tại.
- `Program` nhận `CommandProcessor` ở constructor; có thể thay `BasicCommandProcessor` bằng một implementation khác để kiểm thử.
- `ProgramSchedule` giữ dữ liệu, `ProgramCommands` dựng lệnh, `BasicCommandProcessor` giữ lịch sử. Mỗi thành phần có trách nhiệm rõ ràng.

## Ảnh nên chụp để nộp báo cáo

1. `UndoableCommand.java` cùng `Program3.java`: chứng minh kế thừa và 3 factory của Exercise 15.
2. `ProgramCommands.java`: đoạn `execute()`/`undo()` của Add, Remove và Clear.
3. `BasicCommandProcessor.java`: `consume()`, `undoLast()`, hai stack và `redoLast()`.
4. `CommandProcessor.java` và `Program.java`: interface, constructor injection, factory private.
5. Console chạy `.\run.cmd all`: bốn demo và dòng tổng kết `44/44 TEST PASS`.

## Câu hỏi thường gặp khi giải thích

- **Sao không code thêm Exercise 14?** Đề chỉ giao 15–18; 15 cần kế thừa ý tưởng thiết kế từ 14, nên project dùng mã nền mà không có demo 14.
- **Undo khác Redo?** Undo gọi `undo()` trên lệnh đã thực thi, Redo gọi `execute()` lần nữa trên lệnh đã Undo.
- **Vì sao phải có interface CommandProcessor?** Giảm phụ thuộc giữa Program và implementation quản lý lịch sử, đúng DIP/Dependency Injection theo Solution 18.
- **Law of Demeter được thể hiện ở đâu?** Client chỉ gọi API processor hoặc Program; không lấy stack/historical list ra để tự thao tác.
- **Tại sao không để Program tự giữ stack?** Vì sẽ trộn trách nhiệm quản lý dữ liệu biểu diễn với quản lý lịch sử, dễ trở thành God Class.
