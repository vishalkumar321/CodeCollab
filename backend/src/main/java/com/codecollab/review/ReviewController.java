package com.codecollab.review;

import com.codecollab.review.model.ReviewAction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prs")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{prId}/review")
    public ResponseEntity<ReviewAction> submitReview(
            @PathVariable Long prId,
            @RequestBody Map<String, String> body,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ReviewAction.ReviewActionType action = ReviewAction.ReviewActionType.valueOf(
                body.get("action").toUpperCase());
        return ResponseEntity.ok(reviewService.submitReviewAction(prId, userId, action));
    }

    @GetMapping("/{prId}/reviews")
    public ResponseEntity<List<ReviewAction>> getReviews(@PathVariable Long prId) {
        return ResponseEntity.ok(reviewService.getReviewActions(prId));
    }
}
