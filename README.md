# DesignBook — Thực hành Exercise 6.15–6.18 (đã đối chiếu Solutions)

**Chỉ thực hiện và nộp bốn bài Exercise 15, 16, 17, 18 — Chapter 6.**  
Project Java 21, chạy PowerShell trên Windows và import Eclipse dưới dạng Maven Project.

## Nguồn đối chiếu

- Đề gốc: `DesignBook-master/exercises/e-chapter6.md` trong ZIP DesignBook.
- **Solutions 15, 16, 17**: nội dung Java do người học cung cấp (gồm `Exercise15`, `Program3`, `UndoableCommand`, `CommandProcessor`, `CommandProcessor2`).
- **Solution 18**: ảnh sơ đồ và mô tả do người học cung cấp (`Program` → `CommandProcessor` interface ← `BasicCommandProcessor`; constructor injection; factory private).
- Đây là **bản tự triển khai bám sát mẫu**, không phải bản sao nguyên xi tất cả class trong tài liệu.

## Kết quả theo từng bài

| Bài | Yêu cầu chính | File minh chứng |
|---|---|---|
| 6.15 | UndoableCommand extends Command; Program3 tạo lệnh add/remove/clear có execute/undo; Undo từng bước | `Command.java`, `UndoableCommand.java`, `Program3.java`, `ProgramCommands.java`, `Main.exercise15()` |
| 6.16 | Bộ xử lý nhận lệnh qua consume(), ghi lại lệnh đã chạy; undoLast(); Law of Demeter | `CommandProcessor.java`, `BasicCommandProcessor.java`, `Main.exercise16()` |
| 6.17 | Dùng thêm stack lệnh đã undo để redoLast(), đúng thứ tự | `BasicCommandProcessor.java`, `Main.exercise17()` |
| 6.18 | Client gọi add/remove/clear/undoLast() trên Program; interface processor, constructor injection, factory private | `Program.java`, `CommandProcessor.java`, `BasicCommandProcessor.java`, `Main.exercise18()` |

**Exercise 14** chỉ xuất hiện trong mô tả vì Exercise 15 kế thừa thiết kế sẵn có. Không có demo hay bài nộp 14.

## Cấu trúc

```text
src/main/java/vn/designbook/chapter6/
├── Command.java                    # interface execute()
├── UndoableCommand.java            # extends Command, thêm undo()
├── Program3.java                   # các factory công khai cho bài 15
├── ProgramCommands.java            # anonymous UndoableCommand, lưu trạng thái cũ
├── CommandProcessor.java           # interface dùng cho bài 16–18
├── BasicCommandProcessor.java      # consume, undoLast, redoLast
├── Program.java                    # nghiệp vụ add/remove/clear, DI trong bài 18
├── ProgramSchedule.java            # dữ liệu 7 ngày, snapshot / restore
├── Show.java, Movie.java, Concert.java, NullShow.java
├── Main.java                       # demo riêng 15,16,17,18
└── RegressionTests.java            # 44 ca kiểm thử thuần Java
src/test/java/vn/designbook/chapter6/
└── CommandSeriesJUnitTest.java     # 19 ca JUnit 5
run.cmd
run.ps1
pom.xml
docs/
├── doi-chieu-de-va-ma.md
├── so-sanh-solutions.md
├── kien-truc-va-giai-thich.md
└── demo-output.txt
```

## Chạy trên Windows — không cần Maven

Giải nén, mở PowerShell ngay trong thư mục chứa `run.cmd`:

```powershell
java -version
javac -version
.\run.cmd 15
.\run.cmd 16
.\run.cmd 17
.\run.cmd 18
.\run.cmd test
.\run.cmd all
```

Lệnh `all` chạy 4 demo và 44 kiểm thử Java thuần. Chỉ cần **JDK 21** có `java` và `javac` trong PATH.

## Import Eclipse

1. File → Import → Maven → Existing Maven Projects → Browse chọn thư mục có `pom.xml`.
2. Kiểm tra Project JRE = **JDK 21**.
3. Mở `src/main/java/vn/designbook/chapter6/Main.java` → Run As → Java Application.
4. Run Configurations → Arguments → nhập `15`, `16`, `17`, `18` hoặc `test` để chạy riêng.
5. Chạy JUnit: mở `CommandSeriesJUnitTest.java` → Run As → JUnit Test (Eclipse/Maven cần tải các dependency JUnit 5).

```powershell
mvn clean test
mvn -q compile exec:java -Dexec.args=all
```

## Trạng thái kiểm thử

- **44/44 PASS**: đã thực sự biên dịch/chạy bằng `javac`/`java` trên JDK 21.
- **19 test JUnit 5**: có mã nguồn trong project; Maven/JUnit 5 chưa được chạy xác minh ở môi trường tạo file vì không có `mvn`/dependency.
- Các thiết kế được bổ sung để an toàn hơn mẫu ngắn: Undo Add thay thế khôi phục suất cũ, lệnh mới xóa redo cũ, xử lý exception không làm sai lịch sử, snapshot sao chép dữ liệu.

Đọc `docs/so-sanh-solutions.md` để biết **điểm nào giống mẫu và điểm nào cố ý khác**; `docs/kien-truc-va-giai-thich.md` để học, làm báo cáo và chuẩn bị giải thích code.
