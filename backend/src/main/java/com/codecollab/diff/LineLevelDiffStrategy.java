package com.codecollab.diff;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * DESIGN PATTERN: Strategy — Concrete Implementation
 * Line-level diff using LCS (Longest Common Subsequence) algorithm.
 * Each line is tagged as ADDED, REMOVED, or UNCHANGED.
 */
@Component
public class LineLevelDiffStrategy implements DiffStrategy {

    @Override
    public List<DiffLine> computeDiff(String baseContent, String changedContent) {
        String[] baseLines = baseContent == null ? new String[0] : baseContent.split("\n", -1);
        String[] changedLines = changedContent == null ? new String[0] : changedContent.split("\n", -1);

        // Compute LCS table
        int[][] lcs = computeLCS(baseLines, changedLines);

        // Backtrack to generate diff
        List<DiffLine> result = new ArrayList<>();
        backtrack(lcs, baseLines, changedLines, baseLines.length, changedLines.length, result);

        // Assign sequential line numbers
        int lineNum = 1;
        for (int i = 0; i < result.size(); i++) {
            DiffLine dl = result.get(i);
            result.set(i, new DiffLine(lineNum++, dl.content(), dl.type()));
        }

        return result;
    }

    private int[][] computeLCS(String[] a, String[] b) {
        int m = a.length, n = b.length;
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a[i - 1].equals(b[j - 1])) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp;
    }

    private void backtrack(int[][] lcs, String[] base, String[] changed,
                           int i, int j, List<DiffLine> result) {
        if (i == 0 && j == 0) return;

        if (i == 0) {
            backtrack(lcs, base, changed, i, j - 1, result);
            result.add(new DiffLine(0, changed[j - 1], DiffLine.DiffType.ADDED));
        } else if (j == 0) {
            backtrack(lcs, base, changed, i - 1, j, result);
            result.add(new DiffLine(0, base[i - 1], DiffLine.DiffType.REMOVED));
        } else if (base[i - 1].equals(changed[j - 1])) {
            backtrack(lcs, base, changed, i - 1, j - 1, result);
            result.add(new DiffLine(0, base[i - 1], DiffLine.DiffType.UNCHANGED));
        } else if (lcs[i - 1][j] >= lcs[i][j - 1]) {
            backtrack(lcs, base, changed, i - 1, j, result);
            result.add(new DiffLine(0, base[i - 1], DiffLine.DiffType.REMOVED));
        } else {
            backtrack(lcs, base, changed, i, j - 1, result);
            result.add(new DiffLine(0, changed[j - 1], DiffLine.DiffType.ADDED));
        }
    }
}
