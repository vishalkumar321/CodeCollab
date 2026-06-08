package com.codecollab.comment;

import com.codecollab.comment.model.Comment;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/prs/{prId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long prId,
            @RequestBody AddCommentRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return ResponseEntity.ok(commentService.addComment(prId, userId, request));
    }

    @GetMapping("/prs/{prId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long prId) {
        return ResponseEntity.ok(commentService.getComments(prId));
    }

    @PatchMapping("/comments/{id}/resolve")
    public ResponseEntity<Comment> toggleResolved(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.toggleResolved(id));
    }
}
