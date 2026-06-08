package com.codecollab.diff;

import java.util.List;

/**
 * DESIGN PATTERN: Strategy
 * Interface for diff computation algorithms.
 * Implementations can vary (line-level, word-level, character-level).
 */
public interface DiffStrategy {
    List<DiffLine> computeDiff(String baseContent, String changedContent);
}
