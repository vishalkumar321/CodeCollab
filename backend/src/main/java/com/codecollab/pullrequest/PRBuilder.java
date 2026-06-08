package com.codecollab.pullrequest;

import com.codecollab.pullrequest.model.PRFile;
import com.codecollab.pullrequest.model.PRReviewer;
import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.pullrequest.model.PullRequest.PRStatus;
import com.codecollab.repository.model.Repository;
import com.codecollab.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * DESIGN PATTERN: Builder
 * Fluent builder for constructing PullRequest entities with validation.
 */
public class PRBuilder {

    private String title;
    private String description;
    private PRStatus status = PRStatus.DRAFT;
    private Repository repository;
    private User author;
    private final List<PRFile> files = new ArrayList<>();
    private final List<PRReviewer> reviewers = new ArrayList<>();

    public PRBuilder title(String title) {
        this.title = title;
        return this;
    }

    public PRBuilder description(String description) {
        this.description = description;
        return this;
    }

    public PRBuilder status(PRStatus status) {
        this.status = status;
        return this;
    }

    public PRBuilder repository(Repository repository) {
        this.repository = repository;
        return this;
    }

    public PRBuilder author(User author) {
        this.author = author;
        return this;
    }

    public PRBuilder addFile(String filename, String baseContent, String changedContent) {
        PRFile file = new PRFile();
        file.setFilename(filename);
        file.setBaseContent(baseContent);
        file.setChangedContent(changedContent);
        this.files.add(file);
        return this;
    }

    public PullRequest build() {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("PR title is required");
        if (repository == null) throw new IllegalArgumentException("Repository is required");
        if (author == null) throw new IllegalArgumentException("Author is required");

        PullRequest pr = new PullRequest();
        pr.setTitle(title);
        pr.setDescription(description);
        pr.setStatus(status);
        pr.setRepository(repository);
        pr.setAuthor(author);
        return pr;
    }

    public List<PRFile> getFiles() { return files; }
}
