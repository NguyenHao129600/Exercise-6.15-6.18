package vn.designbook.chapter6;

import java.util.ArrayList;
import java.util.List;

import static vn.designbook.chapter6.Program.Day.*;


public final class Main {
    private Main() { }

    private static final Movie MOVIE_A = new Movie("The First Movie", 2023, 110);
    private static final Movie MOVIE_B = new Movie("The Second Movie", 2024, 95);

    public static void main(String[] args) {
        String mode = args.length == 0 ? "all" : args[0];
        switch (mode) {
            case "15" -> exercise15();
            case "16" -> exercise16();
            case "17" -> exercise17();
            case "18" -> exercise18();
            case "test" -> RegressionTests.runAll();
            case "all" -> {
                exercise15();
                exercise16();
                exercise17();
                exercise18();
                RegressionTests.runAll();
            }
            default -> {
                System.err.println("Dùng: all | 15 | 16 | 17 | 18 | test");
                System.exit(2);
            }
        }
    }

    static void exercise15() {
        System.out.println("\n========== EXERCISE 6.15 – UNDO COMMAND ==========");
        Program3 program = new Program3();
        UndoableCommand addA = program.createAddCommand(MOVIE_A, MONDAY);
        UndoableCommand addB = program.createAddCommand(MOVIE_B, TUESDAY);
        UndoableCommand removeA = program.createRemoveCommand(MONDAY);
        UndoableCommand clear = program.createClearCommand();
        List<UndoableCommand> executed = new ArrayList<>();

        // Kiểm tra trạng thái sau từng execute() trước khi hoàn tác từng lệnh.
        List<UndoableCommand> commands = List.of(addA, addB, removeA, clear);
        for (int step = 0; step < commands.size(); step++) {
            commands.get(step).execute();
            executed.add(commands.get(step));
            if (step == 0) {
                // Giống kịch bản trong Solution 15: execute -> undo -> execute.
                addA.undo();
                verify(isNull(program.get(MONDAY)), "Undo Add lần thứ nhất");
                addA.execute();
                verify(MOVIE_A.equals(program.get(MONDAY)), "Thực thi lại Add");
                System.out.println("[PASS] Add A: execute -> undo -> execute");
            }
            switch (step) {
                case 0 -> verify(MOVIE_A.equals(program.get(MONDAY)) && isNull(program.get(TUESDAY)), "Sau Add A");
                case 1 -> verify(MOVIE_A.equals(program.get(MONDAY)) && MOVIE_B.equals(program.get(TUESDAY)), "Sau Add B");
                case 2 -> verify(isNull(program.get(MONDAY)) && MOVIE_B.equals(program.get(TUESDAY)), "Sau Remove A");
                case 3 -> verify(isNull(program.get(MONDAY)) && isNull(program.get(TUESDAY)), "Sau Clear");
                default -> throw new AssertionError();
            }
            printState("Execute bước " + (step + 1), program);
        }
        printState("Sau 4 lệnh", program);
        verify(isNull(program.get(MONDAY)) && isNull(program.get(TUESDAY)), "Clear phải xóa cả hai ngày");

        for (int i = executed.size() - 1; i >= 0; i--) {
            executed.get(i).undo();
            switch (i) {
                case 3 -> verify(isNull(program.get(MONDAY)) && MOVIE_B.equals(program.get(TUESDAY)), "Undo Clear");
                case 2 -> verify(MOVIE_A.equals(program.get(MONDAY)) && MOVIE_B.equals(program.get(TUESDAY)), "Undo Remove");
                case 1 -> verify(MOVIE_A.equals(program.get(MONDAY)) && isNull(program.get(TUESDAY)), "Undo Add B");
                case 0 -> verify(isNull(program.get(MONDAY)) && isNull(program.get(TUESDAY)), "Undo Add A");
                default -> throw new AssertionError();
            }
            printState("Undo bước " + (4 - i), program);
        }
        System.out.println("[PASS] 6.15: cả 4 lệnh đều Undo đúng");
    }

    static void exercise16() {
        System.out.println("\n========== EXERCISE 6.16 – COMMAND PROCESSOR ==========");
        ProgramSchedule schedule = new ProgramSchedule();
        BasicCommandProcessor processor = new BasicCommandProcessor();
        processor.consume(ProgramCommands.add(schedule, MOVIE_A, MONDAY));
        processor.consume(ProgramCommands.add(schedule, MOVIE_B, TUESDAY));
        processor.consume(ProgramCommands.remove(schedule, MONDAY));
        printState("Sau 3 lệnh", schedule);
        verify(processor.undoCount() == 3, "Processor lưu 3 lệnh");
        processor.undoLast();
        verify(MOVIE_A.equals(schedule.get(MONDAY)), "Undo cuối trả lại A");
        processor.undoLast();
        verify(isNull(schedule.get(TUESDAY)), "Undo tiếp bỏ B");
        processor.undoLast();
        verify(!processor.canUndo() && isNull(schedule.get(MONDAY)), "Hết lệnh để Undo");
        printState("Sau 3 lần Undo", schedule);
        System.out.println("[PASS] 6.16: Processor lưu lịch sử và Undo LIFO");
    }

    static void exercise17() {
        System.out.println("\n========== EXERCISE 6.17 – REDO COMMAND ==========");
        ProgramSchedule schedule = new ProgramSchedule();
        BasicCommandProcessor processor = new BasicCommandProcessor();
        processor.consume(ProgramCommands.add(schedule, MOVIE_A, MONDAY));
        processor.consume(ProgramCommands.add(schedule, MOVIE_B, TUESDAY));
        processor.undoLast();
        verify(isNull(schedule.get(TUESDAY)) && processor.canRedo(), "Undo đưa B vào redoStack");
        processor.redoLast();
        verify(MOVIE_B.equals(schedule.get(TUESDAY)), "Redo khôi phục B");
        printState("Sau Redo", schedule);
        processor.undoLast();
        processor.consume(ProgramCommands.remove(schedule, MONDAY));
        verify(!processor.canRedo(), "Lệnh mới xóa redoStack");
        System.out.println("[PASS] 6.17: Redo chạy đúng và mất hiệu lực khi có lệnh mới");
    }

    static void exercise18() {
        System.out.println("\n========== EXERCISE 6.18 – PROGRAM FACADE ==========");
        CommandProcessor processor = new BasicCommandProcessor();
        Program program = new Program(processor); // Dependency Injection (Solution 6.18)
        program.add(MOVIE_A, MONDAY);
        program.add(MOVIE_B, TUESDAY);
        program.remove(MONDAY);
        program.clear();
        printState("Program sau 4 lệnh", program);
        program.undoLast();
        verify(MOVIE_B.equals(program.get(TUESDAY)) && isNull(program.get(MONDAY)), "Undo clear");
        program.undoLast();
        verify(MOVIE_A.equals(program.get(MONDAY)), "Undo remove");
        program.redoLast();
        verify(isNull(program.get(MONDAY)), "Redo remove");
        printState("Program sau Undo/Redo", program);
        System.out.println("[PASS] 6.18: Client gọi trực tiếp Program, logic history tách riêng");
    }

    static boolean isNull(Show show) {
        return show instanceof NullShow;
    }

    static void verify(boolean ok, String message) {
        if (!ok) throw new AssertionError("FAIL: " + message);
    }

    private static void printState(String label, Program3 program) {
        System.out.printf("%s: MONDAY=%s | TUESDAY=%s%n",
                label, program.get(MONDAY).description(), program.get(TUESDAY).description());
    }

    private static void printState(String label, ProgramSchedule schedule) {
        System.out.printf("%s: MONDAY=%s | TUESDAY=%s%n",
                label, schedule.get(MONDAY).description(), schedule.get(TUESDAY).description());
    }

    private static void printState(String label, Program program) {
        System.out.printf("%s: MONDAY=%s | TUESDAY=%s%n",
                label, program.get(MONDAY).description(), program.get(TUESDAY).description());
    }
}
