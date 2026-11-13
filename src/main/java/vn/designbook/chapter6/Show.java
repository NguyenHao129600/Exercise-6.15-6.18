package vn.designbook.chapter6;

/** Mỗi chương trình biểu diễn phải có khả năng sao chép độc lập. */
public interface Show {
    String description();
    int runningTime();
    Show copy();
}
