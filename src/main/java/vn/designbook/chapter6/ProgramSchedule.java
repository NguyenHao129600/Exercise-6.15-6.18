package vn.designbook.chapter6;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Chỉ quản lý dữ liệu của lịch biểu diễn; KHÔNG lưu ngăn xếp Undo/Redo.
 * Các phương thức chỉnh sửa được giới hạn trong package để chỉ mã nghiệp vụ dùng.
 */
final class ProgramSchedule {
    private final EnumMap<Program.Day, Show> shows = new EnumMap<>(Program.Day.class);

    ProgramSchedule() {
        clear();
    }

    Show get(Program.Day day) {
        return shows.get(Objects.requireNonNull(day, "day")).copy();
    }

    void set(Program.Day day, Show show) {
        shows.put(Objects.requireNonNull(day, "day"), Objects.requireNonNull(show, "show").copy());
    }

    void clear() {
        for (Program.Day day : Program.Day.values()) {
            shows.put(day, new NullShow());
        }
    }

    EnumMap<Program.Day, Show> snapshot() {
        EnumMap<Program.Day, Show> copied = new EnumMap<>(Program.Day.class);
        for (Map.Entry<Program.Day, Show> entry : shows.entrySet()) {
            copied.put(entry.getKey(), entry.getValue().copy());
        }
        return copied;
    }

    void restore(Map<Program.Day, Show> snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        if (snapshot.size() != Program.Day.values().length) {
            throw new IllegalArgumentException("Bản lưu phải có đủ 7 ngày");
        }
        for (Program.Day day : Program.Day.values()) {
            if (!snapshot.containsKey(day) || snapshot.get(day) == null) {
                throw new IllegalArgumentException("Bản lưu thiếu ngày " + day);
            }
        }
        for (Program.Day day : Program.Day.values()) {
            shows.put(day, snapshot.get(day).copy());
        }
    }

    String describe() {
        StringBuilder result = new StringBuilder();
        for (Program.Day day : Program.Day.values()) {
            result.append(String.format("%-10s : %s%n", day, shows.get(day).description()));
        }
        return result.toString();
    }
}
