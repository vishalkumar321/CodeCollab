package com.codecollab.comment;

import com.codecollab.comment.model.Comment;
import com.codecollab.pullrequest.PRFileRepository;
import com.codecollab.pullrequest.PRRepository;
import com.codecollab.pullrequest.model.PRFile;
import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.user.UserRepository;
import com.codecollab.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PRRepository prRepository;
    private final PRFileRepository prFileRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment addComment(Long prId, Long authorId, AddCommentRequest request) {
        PullRequest pr = prRepository.findById(prId)
                .orElseThrow(() -> new IllegalArgumentException("PR not found"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PRFile file = null;
        if (request.fileId() != null) {
            file = prFileRepository.findById(request.fileId())
                    .orElseThrow(() -> new IllegalArgumentException("File not found"));
        }

        Comment comment = Comment.builder()
                .pullRequest(pr)
                .file(file)
                .author(author)
                .lineNumber(request.lineNumber())
                .content(request.content())
                .parentCommentId(request.parentCommentId())
                .isResolved(false)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getComments(Long prId) {
        return commentRepository.findByPullRequestIdOrderByCreatedAtAsc(prId);
    }

    @Transactional
    public Comment toggleResolved(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment.setIsResolved(!comment.getIsResolved());
        return commentRepository.save(comment);
    }
}
