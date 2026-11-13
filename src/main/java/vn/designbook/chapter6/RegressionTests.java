package vn.designbook.chapter6;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import static vn.designbook.chapter6.Program.Day.*;

/** Kiểm thử thuần Java, không cần Maven/JUnit; chạy Main test. */
final class RegressionTests {
    private static int passed = 0;
    private static final Movie A = new Movie("A", 2022, 90);
    private static final Movie B = new Movie("B", 2024, 120);
    private static final Movie C = new Movie("C", 2025, 100);

    private RegressionTests() { }

    static void runAll() {
        System.out.println("\n========== REGRESSION TESTS ==========");
        passed = 0;
        test("01 - Lịch mặc định có đủ 7 NullShow", () -> {
            Program p = new Program();
            for (Program.Day d : Program.Day.values()) check(isNull(p.get(d)), "Thiếu " + d);
        });
        test("02 - Program.get() trả về đối tượng sao chép", () -> {
            Program p = new Program();
            p.add(A, MONDAY);
            check(p.get(MONDAY).equals(A), "Giá trị phải bằng nhau");
            check(p.get(MONDAY) != p.get(MONDAY), "Mỗi lần gọi tạo một bản sao");
        });
        test("03 - Movie copy, equals, hashCode", () -> {
            Show copy = A.copy();
            check(copy != A && copy.equals(A), "Movie copy độc lập");
            check(copy.hashCode() == A.hashCode(), "HashCode giống nhau");
        });
        test("04 - NullShow copy độc lập", () -> {
            Show s = new NullShow();
            check(s.copy() != s && s.copy().equals(s) && s.runningTime() == 0, "Null Object");
        });
        test("05 - Add có thể Undo", () -> {
            ProgramSchedule s = new ProgramSchedule();
            UndoableCommand c = ProgramCommands.add(s, A, MONDAY);
            c.execute();
            check(s.get(MONDAY).equals(A), "Add");
            c.undo();
            check(isNull(s.get(MONDAY)), "Undo Add");
        });
        test("06 - Add thay thế phải Undo về phim cũ", () -> {
            ProgramSchedule s = new ProgramSchedule();
            s.set(MONDAY, A);
            UndoableCommand c = ProgramCommands.add(s, B, MONDAY);
            c.execute();
            c.undo();
            check(s.get(MONDAY).equals(A), "Khôi phục A");
        });
        test("07 - Remove có thể Undo", () -> {
            ProgramSchedule s = new ProgramSchedule();
            s.set(MONDAY, A);
            UndoableCommand c = ProgramCommands.remove(s, MONDAY);
            c.execute();
            check(isNull(s.get(MONDAY)), "Remove");
            c.undo();
            check(s.get(MONDAY).equals(A), "Undo Remove");
        });
        test("08 - Clear có thể Undo toàn bộ 7 ngày", () -> {
            ProgramSchedule s = new ProgramSchedule();
            s.set(MONDAY, A);
            s.set(SUNDAY, B);
            UndoableCommand c = ProgramCommands.clear(s);
            c.execute();
            for (Program.Day d : Program.Day.values()) check(isNull(s.get(d)), "Clear " + d);
            c.undo();
            check(s.get(MONDAY).equals(A) && s.get(SUNDAY).equals(B), "Undo Clear");
        });
        test("09 - Undo theo thứ tự ngược 4 thao tác 6.15", () -> {
            Main.exercise15();
        });
        test("10 - UndoableCommand không được execute 2 lần liên tiếp", () -> {
            UndoableCommand c = ProgramCommands.add(new ProgramSchedule(), A, MONDAY);
            c.execute();
            expectThrows(IllegalStateException.class, c::execute);
        });
        test("11 - UndoableCommand không được Undo khi chưa execute", () -> {
            UndoableCommand c = ProgramCommands.remove(new ProgramSchedule(), MONDAY);
            expectThrows(IllegalStateException.class, c::undo);
        });
        test("12 - UndoableCommand execute lại được sau Undo (Redo)", () -> {
            ProgramSchedule s = new ProgramSchedule();
            UndoableCommand c = ProgramCommands.add(s, A, MONDAY);
            c.execute(); c.undo(); c.execute();
            check(s.get(MONDAY).equals(A), "Thực thi lại");
        });
        test("13 - CommandProcessor lưu lịch sử và Undo LIFO", () -> {
            ProgramSchedule s = new ProgramSchedule();
            BasicCommandProcessor p = new BasicCommandProcessor();
            p.consume(ProgramCommands.add(s, A, MONDAY));
            p.consume(ProgramCommands.add(s, B, TUESDAY));
            check(p.undoCount() == 2 && !p.canRedo(), "Hai lệnh đã chạy");
            p.undoLast();
            check(isNull(s.get(TUESDAY)) && s.get(MONDAY).equals(A), "LIFO");
            p.undoLast();
            check(isNull(s.get(MONDAY)) && p.redoCount() == 2, "Đã Undo cả 2");
        });
        test("14 - Redo trả lại trạng thái", () -> {
            ProgramSchedule s = new ProgramSchedule();
            BasicCommandProcessor p = new BasicCommandProcessor();
            p.consume(ProgramCommands.add(s, A, MONDAY));
            p.undoLast(); p.redoLast();
            check(s.get(MONDAY).equals(A) && !p.canRedo() && p.canUndo(), "Redo");
        });
        test("15 - Lệnh mới sau Undo sẽ xóa lịch sử Redo", () -> {
            ProgramSchedule s = new ProgramSchedule();
            BasicCommandProcessor p = new BasicCommandProcessor();
            p.consume(ProgramCommands.add(s, A, MONDAY));
            p.undoLast();
            p.consume(ProgramCommands.add(s, B, TUESDAY));
            check(!p.canRedo(), "Nhánh redo cũ đã bị xóa");
        });
        test("16 - Redo nhiều bước tuân thủ thứ tự", () -> {
            ProgramSchedule s = new ProgramSchedule();
            BasicCommandProcessor p = new BasicCommandProcessor();
            p.consume(ProgramCommands.add(s, A, MONDAY));
            p.consume(ProgramCommands.add(s, B, TUESDAY));
            p.undoLast(); p.undoLast();
            p.redoLast();
            check(s.get(MONDAY).equals(A) && isNull(s.get(TUESDAY)), "Redo lệnh thứ nhất");
            p.redoLast();
            check(s.get(TUESDAY).equals(B), "Redo lệnh thứ hai");
        });
        test("17 - Undo khi lịch sử trống ném lỗi", () -> {
            expectThrows(IllegalStateException.class, new BasicCommandProcessor()::undoLast);
        });
        test("18 - Redo khi lịch sử trống ném lỗi", () -> {
            expectThrows(IllegalStateException.class, new BasicCommandProcessor()::redoLast);
        });
        test("19 - Execute lỗi không làm mất redoStack", () -> {
            ProgramSchedule s = new ProgramSchedule();
            BasicCommandProcessor p = new BasicCommandProcessor();
            p.consume(ProgramCommands.add(s, A, MONDAY));
            p.undoLast();
            UndoableCommand broken = new UndoableCommand() {
                public void execute() { throw new IllegalStateException("Dữ liệu lỗi"); }
                public void undo() { }
            };
            expectThrows(IllegalStateException.class, () -> p.consume(broken));
            check(p.canRedo() && p.redoCount() == 1, "Redo chưa bị mất");
        });
        test("20 - Undo lỗi không làm mất phần tử undoStack", () -> {
            BasicCommandProcessor p = new BasicCommandProcessor();
            UndoableCommand broken = new UndoableCommand() {
                public void execute() { }
                public void undo() { throw new IllegalStateException("Undo lỗi"); }
            };
            p.consume(broken);
            expectThrows(IllegalStateException.class, p::undoLast);
            check(p.undoCount() == 1 && p.redoCount() == 0, "Không đổi stack");
        });
        test("21 - Redo lỗi không làm mất phần tử redoStack", () -> {
            BasicCommandProcessor p = new BasicCommandProcessor();
            UndoableCommand flaky = new UndoableCommand() {
                int count;
                public void execute() { if (++count == 2) throw new IllegalStateException("Redo lỗi"); }
                public void undo() { }
            };
            p.consume(flaky); p.undoLast();
            expectThrows(IllegalStateException.class, p::redoLast);
            check(p.undoCount() == 0 && p.redoCount() == 1, "Không đổi stack");
        });
        test("22 - Program API Add/Remove/Clear đầy đủ", () -> {
            Program p = new Program();
            p.add(A, MONDAY); p.add(B, FRIDAY);
            p.remove(MONDAY);
            check(isNull(p.get(MONDAY)) && p.get(FRIDAY).equals(B), "Remove");
            p.clear();
            check(isNull(p.get(FRIDAY)), "Clear");
        });
        test("23 - Program Undo các thao tác nghiệp vụ", () -> {
            Program p = new Program();
            p.add(A, MONDAY); p.remove(MONDAY); p.clear();
            p.undoLast(); p.undoLast();
            check(p.get(MONDAY).equals(A), "Undo remove");
            p.undoLast();
            check(isNull(p.get(MONDAY)) && !p.canUndo(), "Undo add");
        });
        test("24 - Program Redo hoạt động sau Undo", () -> {
            Program p = new Program();
            p.add(A, MONDAY); p.undoLast(); p.redoLast();
            check(p.get(MONDAY).equals(A) && !p.canRedo(), "Program Redo");
        });
        test("25 - Hai Program không dùng chung lịch sử", () -> {
            Program p1 = new Program();
            Program p2 = new Program();
            p1.add(A, MONDAY);
            check(!p2.canUndo() && isNull(p2.get(MONDAY)), "Tách biệt p1/p2");
        });
        test("26 - Snapshot không chia sẻ object với lịch gốc", () -> {
            ProgramSchedule s = new ProgramSchedule();
            s.set(MONDAY, A);
            EnumMap<Program.Day, Show> snapshot = s.snapshot();
            check(snapshot.get(MONDAY) != s.get(MONDAY), "Bản sao độc lập");
            s.set(MONDAY, B);
            check(snapshot.get(MONDAY).equals(A), "Snapshot không đổi");
        });
        test("27 - Restore lưu đủ và chính xác 7 ngày", () -> {
            ProgramSchedule s = new ProgramSchedule();
            Map<Program.Day, Show> original = s.snapshot();
            s.set(MONDAY, C);
            s.restore(original);
            check(isNull(s.get(MONDAY)), "Restore");
        });
        test("28 - Restore snapshot thiếu ngày phải từ chối", () -> {
            ProgramSchedule s = new ProgramSchedule();
            EnumMap<Program.Day, Show> snapshot = s.snapshot();
            snapshot.remove(SUNDAY);
            expectThrows(IllegalArgumentException.class, () -> s.restore(snapshot));
        });
        test("29 - Không chấp nhận tham số Null quan trọng", () -> {
            Program p = new Program();
            expectThrows(NullPointerException.class, () -> p.add(null, MONDAY));
            expectThrows(NullPointerException.class, () -> p.remove(null));
            expectThrows(NullPointerException.class, () -> p.get(null));
        });
        test("30 - Hai lệnh sửa cùng ngày Undo theo thứ tự đúng", () -> {
            Program p = new Program();
            p.add(A, MONDAY); p.add(B, MONDAY);
            check(p.get(MONDAY).equals(B), "B thay A");
            p.undoLast();
            check(p.get(MONDAY).equals(A), "Undo B -> A");
            p.undoLast();
            check(isNull(p.get(MONDAY)), "Undo A -> Null");
        });
        test("31 - Clear sau khi Undo vẫn khôi phục đúng", () -> {
            Program p = new Program();
            p.add(A, MONDAY);
            p.clear(); p.undoLast();
            check(p.get(MONDAY).equals(A), "Undo Clear");
            p.redoLast();
            check(isNull(p.get(MONDAY)), "Redo Clear");
        });
        test("32 - Program.toString phản ánh đủ bảy ngày", () -> {
            Program p = new Program();
            String description = p.toString();
            for (Program.Day d : Program.Day.values()) {
                check(description.contains(d.name()), "Thiếu " + d);
            }
        });
        test("33 - Undo Clear khôi phục cả Movie và Concert", () -> {
            ProgramSchedule schedule = new ProgramSchedule();
            Concert concert = new Concert("Winter Live", "Band C", 77);
            schedule.set(MONDAY, A);
            schedule.set(THURSDAY, concert);
            UndoableCommand clear = ProgramCommands.clear(schedule);
            clear.execute();
            for (Program.Day d : Program.Day.values()) check(isNull(schedule.get(d)), "Clear " + d);
            clear.undo();
            check(A.equals(schedule.get(MONDAY)), "Movie được khôi phục");
            check(concert.equals(schedule.get(THURSDAY)), "Concert được khôi phục");
        });
        test("34 - Concert có thể thêm và Undo đa hình", () -> {
            Program p = new Program();
            Concert concert = new Concert("Autumn Live", "The Band", 85);
            p.add(concert, SATURDAY);
            check(p.get(SATURDAY).equals(concert), "Thêm Concert");
            check(p.get(SATURDAY) != concert, "Bản sao riêng");
            p.undoLast();
            check(isNull(p.get(SATURDAY)), "Undo Concert");
        });
        test("35 - Undo Clear phải khôi phục tất cả 7 ngày", () -> {
            Program p = new Program();
            for (Program.Day d : Program.Day.values()) p.add(A, d);
            p.clear();
            for (Program.Day d : Program.Day.values()) check(isNull(p.get(d)), "Clear " + d);
            p.undoLast();
            for (Program.Day d : Program.Day.values()) check(A.equals(p.get(d)), "Restore " + d);
        });
        test("36 - Redo Clear sau Undo khôi phục lịch trống", () -> {
            Program p = new Program();
            p.add(A, SUNDAY);
            p.clear();
            p.undoLast();
            check(A.equals(p.get(SUNDAY)), "Undo clear");
            p.redoLast();
            check(isNull(p.get(SUNDAY)), "Redo clear");
        });
        test("37 - Một lệnh mới khi có Redo cũ làm tắt Redo", () -> {
            Program p = new Program();
            p.add(A, MONDAY);
            p.undoLast();
            check(p.canRedo(), "Có thể redo");
            p.add(B, TUESDAY);
            check(!p.canRedo(), "Không còn redo sau nhánh mới");
            expectThrows(IllegalStateException.class, p::redoLast);
        });
        test("38 - Nhiều instance Program có lịch sử độc lập", () -> {
            Program p1 = new Program();
            Program p2 = new Program();
            p1.add(A, MONDAY);
            p2.add(B, TUESDAY);
            p1.undoLast();
            check(B.equals(p2.get(TUESDAY)) && p2.canUndo(), "History độc lập");
        });
        test("39 - UndoableCommand trực tiếp cho phép redo sau undo", () -> {
            ProgramSchedule s = new ProgramSchedule();
            UndoableCommand c = ProgramCommands.clear(s);
            s.set(MONDAY, A);
            c.execute();
            c.undo();
            check(A.equals(s.get(MONDAY)), "Undo Clear");
            c.execute();
            check(isNull(s.get(MONDAY)), "Redo Clear");
        });
        test("40 - Program không lộ trực tiếp Processor hoặc Schedule", () -> {
            for (var field : Program.class.getDeclaredFields()) {
                check(java.lang.reflect.Modifier.isPrivate(field.getModifiers()), "Field công khai: " + field.getName());
            }
            for (var method : Program.class.getDeclaredMethods()) {
                check(!method.getName().startsWith("getProcessor") && !method.getName().startsWith("getSchedule"),
                        "Leaking processor/schedule");
            }
        });
        test("41 - Program3 tạo đủ ba UndoableCommand", () -> {
            Program3 program = new Program3();
            UndoableCommand add = program.createAddCommand(A, MONDAY);
            UndoableCommand remove = program.createRemoveCommand(MONDAY);
            UndoableCommand clear = program.createClearCommand();
            add.execute();
            check(A.equals(program.get(MONDAY)), "Program3 add");
            remove.execute();
            check(isNull(program.get(MONDAY)), "Program3 remove");
            remove.undo();
            check(A.equals(program.get(MONDAY)), "Program3 undo remove");
            clear.execute();
            check(isNull(program.get(MONDAY)), "Program3 clear");
            clear.undo();
            check(A.equals(program.get(MONDAY)), "Program3 undo clear");
            add.undo();
            check(isNull(program.get(MONDAY)), "Program3 undo add");
        });
        test("42 - UndoableCommand kế thừa Command", () -> {
            check(Command.class.isAssignableFrom(UndoableCommand.class),
                    "Hierarchy theo Solution 15");
        });
        test("43 - Constructor injection dùng đúng processor được truyền", () -> {
            class RecordingProcessor implements CommandProcessor {
                final BasicCommandProcessor delegate = new BasicCommandProcessor();
                int consumeCount;
                public void consume(UndoableCommand cmd) { consumeCount++; delegate.consume(cmd); }
                public void undoLast() { delegate.undoLast(); }
                public void redoLast() { delegate.redoLast(); }
                public boolean canUndo() { return delegate.canUndo(); }
                public boolean canRedo() { return delegate.canRedo(); }
            }
            RecordingProcessor recorder = new RecordingProcessor();
            Program program = new Program(recorder);
            program.add(A, MONDAY);
            program.remove(MONDAY);
            program.clear();
            check(recorder.consumeCount == 3, "Program ủy quyền cho instance injected");
            program.undoLast(); program.undoLast();
            check(A.equals(program.get(MONDAY)), "Đúng hành vi Undo qua dependency");
        });
        test("44 - Program chỉ khai báo factory createXXXCommand private", () -> {
            try {
                for (String method : new String[]{"createAddCommand", "createRemoveCommand", "createClearCommand"}) {
                    boolean found = false;
                    for (var m : Program.class.getDeclaredMethods()) {
                        if (m.getName().equals(method)) {
                            check(java.lang.reflect.Modifier.isPrivate(m.getModifiers()), method + " phải private");
                            found = true;
                        }
                    }
                    check(found, "Thiếu factory " + method);
                }
                check(Program.class.getDeclaredField("aProcessor").getType() == CommandProcessor.class,
                        "Phụ thuộc vào interface, không phụ thuộc BasicCommandProcessor");
            } catch (NoSuchFieldException e) { throw new AssertionError(e); }
        });
        System.out.printf("\nKẾT QUẢ: %d/%d TEST PASS%n", passed, 44);
    }

    private static void test(String name, Runnable task) {
        try {
            task.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable error) {
            System.err.println("[FAIL] " + name + " -> " + error);
            throw error;
        }
    }

    private static boolean isNull(Show show) {
        return show instanceof NullShow;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static <T extends Throwable> void expectThrows(Class<T> expected, Runnable task) {
        Objects.requireNonNull(expected, "expected");
        try {
            task.run();
        } catch (Throwable actual) {
            if (expected.isInstance(actual)) return;
            throw new AssertionError("Mong đợi " + expected.getSimpleName() + ", nhận " + actual, actual);
        }
        throw new AssertionError("Mong đợi " + expected.getSimpleName() + " nhưng không phát sinh lỗi");
    }
}
