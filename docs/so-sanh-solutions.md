# SO SÁNH VỚI SOLUTIONS DO NGƯỜI HỌC CUNG CẤP

## 1. Exercise 15 — Undo các Command

**Mẫu:** `UndoableCommand extends Command { void undo(); }`; `Program3` có `createAddCommand()`, `createRemoveCommand()`, `createClearCommand()`; `Exercise15.main()` gọi `execute()`, `undo()` và thực thi lại command đầu tiên.

**Bản đã chỉnh:**
- `Command` chỉ khai báo `execute()`, `UndoableCommand extends Command` khai báo `undo()`.
- Có `Program3` với đủ 3 factory tạo `UndoableCommand`; implementation anonymous class được trích thành helper `ProgramCommands` để tái sử dụng cho bài 18.
- `Main.exercise15()` thực hiện chuỗi thêm/xóa/clear và **Undo cả 4 lệnh theo thứ tự ngược**, xác minh trạng thái sau mỗi bước; command đầu tiên còn được demo `execute → undo → execute` giống mẫu.
- **Khác mẫu có chủ đích:** Đề mẫu `createAddCommand().undo()` gọi `remove()` nên không khôi phục suất cũ nếu thêm chồng vào cùng một ngày. Bản này lưu `previous` nên có thể khôi phục phim đã bị thay thế. Dữ liệu `Day` được dùng chung từ `Program.Day`, không định nghĩa `Program3.Day` riêng.

## 2. Exercise 16 — CommandProcessor + Law of Demeter

**Mẫu:** class `CommandProcessor`, list `aCommands`, `consume(UndoableCommand)`: gọi execute rồi lưu lệnh; `undoLast()` lấy lệnh cuối và undo.

**Bản đã chỉnh:**
- Cùng API `consume(UndoableCommand)` và `undoLast()`, lịch sử là private, xử lý theo LIFO.
- Để phù hợp sơ đồ bài 18, `CommandProcessor` trong bản cuối là **interface**, `BasicCommandProcessor` chứa mã xử lý. Không tạo một lớp thực thi tách biệt trùng tên cho riêng bài 16.
- Client chỉ giao tiếp qua hành vi processor, không truy cập các stack nội bộ (Law of Demeter).

## 3. Exercise 17 — Redo

**Mẫu:** `CommandProcessor2` thêm list `aUndoneCommands`; Undo chuyển command từ list executed sang undone; Redo lấy từ undone và gọi `consume(command)`.

**Bản đã chỉnh:**
- `BasicCommandProcessor` chứa `executedCommands` và `undoneCommands` (Deque), `undoLast()`/`redoLast()` dịch chuyển đúng thứ tự lệnh.
- **Cải tiến:** Khi `consume()` một lệnh **mới** sau Undo, xóa lịch sử Redo cũ (không được xử lý trong code snippet Solution 17). Vì vậy `redoLast()` thực thi command trực tiếp thay vì gọi `consume()`, nhằm giữ lại các lệnh redo còn lại khi redo nhiều bước.
- Chỉ chuyển command giữa các stack sau khi execute/undo thành công; khi command báo lỗi sẽ không mất lịch sử.

## 4. Exercise 18 — Program quản lý Command thông qua interface

**Sơ đồ mẫu:**

```text
                 ┌──────────────────┐
 Client ────────>│ Program          │
                 │ add/remove/clear │
                 │ undo/redo        │
                 │ private factory  │
                 └────────┬─────────┘
                          │ constructor injection
                 ┌────────▼─────────┐
                 │ CommandProcessor │ <<interface>>
                 │ consume()        │
                 │ undoLast()       │
                 │ redoLast()       │
                 └────────▲─────────┘
                          │ implements
                 ┌────────┴─────────┐
                 │ BasicCommand     │
                 │ Processor        │
                 │ 2 undo/redo stack│
                 └──────────────────┘
```

**Bản đã chỉnh đúng hướng mẫu:**
- `new Program(CommandProcessor processor)` nhận dependency từ bên ngoài; `new Program()` là constructor tiện lợi cho demo.
- `add()`, `remove()`, `clear()` tạo UndoableCommand và chuyển qua `aProcessor.consume(...)`.
- `createAddCommand()`, `createRemoveCommand()`, `createClearCommand()` trong `Program` đều **private**; `undoLast()` và `redoLast()` chuyển tiếp đến processor.
- `CommandProcessor` là interface, `BasicCommandProcessor` triển khai. `Program` không lưu stack và không phụ thuộc trực tiếp class triển khai cụ thể ở constructor nhận DI.
- **Tách thêm một lớp so với sơ đồ:** `ProgramSchedule` chỉ giữ và sao chép dữ liệu `Show` trong 7 ngày; `ProgramCommands` chỉ dựng đối tượng Command. Cách tách này làm trách nhiệm của `Program` gọn hơn, tránh God Class.
- `CommandProcessor` interface có thêm hai phương thức truy vấn `canUndo()/canRedo()` phục vụ giao diện demo; không làm lộ stack.

## Kết luận đối chiếu

Các **yêu cầu chức năng và ý tưởng kiến trúc** từ Solutions 15–18 đã được thể hiện. Đây là mã nguồn tự triển khai, giữ tinh thần lời giải mẫu, **không phải bản sao 1:1**. Các cải tiến được nêu công khai để người học dễ giải thích nếu giảng viên hỏi.
