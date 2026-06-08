package com.codecollab.autoreview.rules;

import com.codecollab.autoreview.AutoReviewHandler;
import com.codecollab.autoreview.model.AutoReviewResult;
import com.codecollab.pullrequest.model.PRFile;
import com.codecollab.pullrequest.model.PullRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Rule 3 — BannedKeywordRule (Chain of Responsibility)
 * Scans changed_content for console.log, TODO, FIXME, and hardcoded password patterns.
 * Severity: ERROR
 */
@Component
public class BannedKeywordRule extends AutoReviewHandler {

    private static final List<Pattern> BANNED_PATTERNS = List.of(
            Pattern.compile("console\\.log\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bTODO\\b"),
            Pattern.compile("\\bFIXME\\b"),
            Pattern.compile("(?i)(password|passwd|pwd)\\s*=\\s*[\"'][^\"']{3,}[\"']"),
            Pattern.compile("(?i)(secret|api_key|apikey)\\s*=\\s*[\"'][^\"']{3,}[\"']")
    );

    private static final List<String> PATTERN_NAMES = List.of(
            "console.log statement",
            "TODO comment",
            "FIXME comment",
            "Hardcoded password",
            "Hardcoded secret/API key"
    );

    @Override
    protected void applyRule(PullRequest pr, List<AutoReviewResult> results) {
        for (PRFile file : pr.getFiles()) {
            if (file.getChangedContent() == null) continue;

            for (int i = 0; i < BANNED_PATTERNS.size(); i++) {
                Pattern pattern = BANNED_PATTERNS.get(i);
                String patternName = PATTERN_NAMES.get(i);
                if (pattern.matcher(file.getChangedContent()).find()) {
                    results.add(buildResult(pr,
                            "BannedKeywordRule",
                            AutoReviewResult.Severity.ERROR,
                            String.format("File '%s' contains a banned pattern: %s",
                                    file.getFilename(), patternName)
                    ));
                }
            }
        }
    }
}
