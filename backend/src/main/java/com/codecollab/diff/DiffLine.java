package com.codecollab.diff;

public record DiffLine(int lineNumber, String content, DiffType type) {
    public enum DiffType {
        ADDED, REMOVED, UNCHANGED
    }
}
