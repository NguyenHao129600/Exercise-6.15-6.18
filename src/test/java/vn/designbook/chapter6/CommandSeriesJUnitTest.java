package vn.designbook.chapter6;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static vn.designbook.chapter6.Program.Day.*;

/** Kiểm thử JUnit độc lập cho 4 bài (mvn test). */
class CommandSeriesJUnitTest {
    private final Movie first = new Movie("First", 2023, 95);
    private final Movie second = new Movie("Second", 2024, 105);

    @Test
    void exercise15_addUndo() {
        ProgramSchedule schedule = new ProgramSchedule();
        UndoableCommand add = ProgramCommands.add(schedule, first, MONDAY);
        add.execute();
        assertEquals(first, schedule.get(MONDAY));
        add.undo();
        assertInstanceOf(NullShow.class, schedule.get(MONDAY));
    }

    @Test
    void exercise15_removeUndo() {
        ProgramSchedule schedule = new ProgramSchedule();
        schedule.set(MONDAY, first);
        UndoableCommand remove = ProgramCommands.remove(schedule, MONDAY);
        remove.execute();
        assertInstanceOf(NullShow.class, schedule.get(MONDAY));
        remove.undo();
        assertEquals(first, schedule.get(MONDAY));
    }

    @Test
    void exercise15_clearUndo() {
        ProgramSchedule schedule = new ProgramSchedule();
        schedule.set(MONDAY, first);
        schedule.set(FRIDAY, second);
        UndoableCommand clear = ProgramCommands.clear(schedule);
        clear.execute();
        assertInstanceOf(NullShow.class, schedule.get(MONDAY));
        clear.undo();
        assertEquals(first, schedule.get(MONDAY));
        assertEquals(second, schedule.get(FRIDAY));
    }

    @Test
    void exercise16_undoLastIsLifo() {
        ProgramSchedule schedule = new ProgramSchedule();
        BasicCommandProcessor processor = new BasicCommandProcessor();
        processor.consume(ProgramCommands.add(schedule, first, MONDAY));
        processor.consume(ProgramCommands.add(schedule, second, TUESDAY));
        processor.undoLast();
        assertEquals(first, schedule.get(MONDAY));
        assertInstanceOf(NullShow.class, schedule.get(TUESDAY));
        assertEquals(1, processor.undoCount());
    }

    @Test
    void exercise17_redoReappliesCommand() {
        ProgramSchedule schedule = new ProgramSchedule();
        BasicCommandProcessor processor = new BasicCommandProcessor();
        processor.consume(ProgramCommands.add(schedule, first, MONDAY));
        processor.undoLast();
        processor.redoLast();
        assertEquals(first, schedule.get(MONDAY));
        assertFalse(processor.canRedo());
    }

    @Test
    void exercise17_newCommandClearsRedo() {
        ProgramSchedule schedule = new ProgramSchedule();
        BasicCommandProcessor processor = new BasicCommandProcessor();
        processor.consume(ProgramCommands.add(schedule, first, MONDAY));
        processor.undoLast();
        processor.consume(ProgramCommands.add(schedule, second, TUESDAY));
        assertFalse(processor.canRedo());
    }

    @Test
    void exercise18_clientUsesOnlyProgramApi() {
        Program program = new Program();
        program.add(first, MONDAY);
        program.add(second, TUESDAY);
        program.remove(MONDAY);
        program.clear();
        program.undoLast();
        assertEquals(second, program.get(TUESDAY));
        program.undoLast();
        assertEquals(first, program.get(MONDAY));
        program.redoLast();
        assertInstanceOf(NullShow.class, program.get(MONDAY));
    }

    @Test
    void programReturnsIndependentCopy() {
        Program program = new Program();
        program.add(first, MONDAY);
        assertNotSame(first, program.get(MONDAY));
        assertEquals(first, program.get(MONDAY));
    }

    @Test
    void emptyHistoryRejectsUndoAndRedo() {
        Program program = new Program();
        assertThrows(IllegalStateException.class, program::undoLast);
        assertThrows(IllegalStateException.class, program::redoLast);
    }

    @Test
    void exercise15_verifyAfterEachExecutionAndUndo() {
        ProgramSchedule schedule = new ProgramSchedule();
        UndoableCommand addFirst = ProgramCommands.add(schedule, first, MONDAY);
        UndoableCommand addSecond = ProgramCommands.add(schedule, second, TUESDAY);
        UndoableCommand removeFirst = ProgramCommands.remove(schedule, MONDAY);
        UndoableCommand clear = ProgramCommands.clear(schedule);

        addFirst.execute();
        assertEquals(first, schedule.get(MONDAY));
        assertInstanceOf(NullShow.class, schedule.get(TUESDAY));

        addSecond.execute();
        assertEquals(first, schedule.get(MONDAY));
        assertEquals(second, schedule.get(TUESDAY));

        removeFirst.execute();
        assertInstanceOf(NullShow.class, schedule.get(MONDAY));
        assertEquals(second, schedule.get(TUESDAY));

        clear.execute();
        for (Program.Day day : Program.Day.values()) {
            assertInstanceOf(NullShow.class, schedule.get(day));
        }

        clear.undo();
        assertInstanceOf(NullShow.class, schedule.get(MONDAY));
        assertEquals(second, schedule.get(TUESDAY));

        removeFirst.undo();
        assertEquals(first, schedule.get(MONDAY));
        assertEquals(second, schedule.get(TUESDAY));

        addSecond.undo();
        assertEquals(first, schedule.get(MONDAY));
        assertInstanceOf(NullShow.class, schedule.get(TUESDAY));

        addFirst.undo();
        for (Program.Day day : Program.Day.values()) {
            assertInstanceOf(NullShow.class, schedule.get(day));
        }
    }

    @Test
    void exercise15_undoEveryCommandInReverseOrder() {
        ProgramSchedule schedule = new ProgramSchedule();
        UndoableCommand a = ProgramCommands.add(schedule, first, MONDAY);
        UndoableCommand b = ProgramCommands.add(schedule, second, TUESDAY);
        UndoableCommand remove = ProgramCommands.remove(schedule, MONDAY);
        UndoableCommand clear = ProgramCommands.clear(schedule);
        a.execute(); b.execute(); remove.execute(); clear.execute();
        clear.undo();
        assertInstanceOf(NullShow.class, schedule.get(MONDAY));
        assertEquals(second, schedule.get(TUESDAY));
        remove.undo();
        assertEquals(first, schedule.get(MONDAY));
        b.undo();
        assertInstanceOf(NullShow.class, schedule.get(TUESDAY));
        a.undo();
        for (Program.Day day : Program.Day.values()) {
            assertInstanceOf(NullShow.class, schedule.get(day));
        }
    }

    @Test
    void exercise15_undoReplaceRestoresPreviousShow() {
        ProgramSchedule schedule = new ProgramSchedule();
        schedule.set(FRIDAY, first);
        UndoableCommand replace = ProgramCommands.add(schedule, second, FRIDAY);
        replace.execute();
        assertEquals(second, schedule.get(FRIDAY));
        replace.undo();
        assertEquals(first, schedule.get(FRIDAY));
    }

    @Test
    void exercise17_manyUndosAndRedosPreserveLifoOrder() {
        ProgramSchedule schedule = new ProgramSchedule();
        BasicCommandProcessor processor = new BasicCommandProcessor();
        processor.consume(ProgramCommands.add(schedule, first, MONDAY));
        processor.consume(ProgramCommands.add(schedule, second, TUESDAY));
        processor.undoLast(); processor.undoLast();
        processor.redoLast();
        assertEquals(first, schedule.get(MONDAY));
        assertInstanceOf(NullShow.class, schedule.get(TUESDAY));
        processor.redoLast();
        assertEquals(second, schedule.get(TUESDAY));
    }

    @Test
    void exercise17_processorFailureDoesNotCorruptCommandHistory() {
        BasicCommandProcessor processor = new BasicCommandProcessor();
        UndoableCommand rejected = new UndoableCommand() {
            public void execute() { throw new IllegalStateException("blocked"); }
            public void undo() { }
        };
        assertThrows(IllegalStateException.class, () -> processor.consume(rejected));
        assertEquals(0, processor.undoCount());
    }

    @Test
    void exercise18_separateProgramInstancesHaveSeparateHistories() {
        Program left = new Program();
        Program right = new Program();
        left.add(first, MONDAY);
        right.add(second, MONDAY);
        left.undoLast();
        assertInstanceOf(NullShow.class, left.get(MONDAY));
        assertEquals(second, right.get(MONDAY));
        assertTrue(right.canUndo());
    }

    @Test
    void concertImplementsShowAndCanBeUndone() {
        Program p = new Program();
        Concert show = new Concert("Autumn Live", "The Band", 85);
        p.add(show, SATURDAY);
        assertEquals(show, p.get(SATURDAY));
        assertNotSame(show, p.get(SATURDAY));
        p.undoLast();
        assertInstanceOf(NullShow.class, p.get(SATURDAY));
    }

    @Test
    void exercise15_program3FactoryMatchesSolution() {
        Program3 p = new Program3();
        UndoableCommand a = p.createAddCommand(first, MONDAY);
        assertInstanceOf(Command.class, a);
        a.execute();
        assertEquals(first, p.get(MONDAY));
        a.undo();
        assertInstanceOf(NullShow.class, p.get(MONDAY));
        a.execute();
        assertEquals(first, p.get(MONDAY));
    }

    @Test
    void exercise18_dependencyInjectionUsesPassedProcessor() {
        class RecordingProcessor implements CommandProcessor {
            private final BasicCommandProcessor delegate = new BasicCommandProcessor();
            int consumed;
            public void consume(UndoableCommand c) { consumed++; delegate.consume(c); }
            public void undoLast() { delegate.undoLast(); }
            public void redoLast() { delegate.redoLast(); }
            public boolean canUndo() { return delegate.canUndo(); }
            public boolean canRedo() { return delegate.canRedo(); }
        }
        RecordingProcessor recorder = new RecordingProcessor();
        Program program = new Program(recorder);
        program.add(first, MONDAY);
        program.clear();
        assertEquals(2, recorder.consumed);
        program.undoLast();
        assertEquals(first, program.get(MONDAY));
    }

    @Test
    void exercise18_privateFactoriesAndInterface() throws Exception {
        assertEquals(CommandProcessor.class, Program.class.getDeclaredField("aProcessor").getType());
        assertTrue(java.lang.reflect.Modifier.isPrivate(
                Program.class.getDeclaredMethod("createClearCommand").getModifiers()));
    }
}
