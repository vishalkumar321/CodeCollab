package com.codecollab.repository;

import com.codecollab.repository.model.Repository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repos")
@RequiredArgsConstructor
public class RepoController {

    private final RepoService repoService;

    @PostMapping
    public ResponseEntity<Repository> createRepo(
            @Valid @RequestBody CreateRepoRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return ResponseEntity.ok(repoService.createRepo(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<Repository>> getAllRepos() {
        return ResponseEntity.ok(repoService.getAllRepos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Repository> getRepoById(@PathVariable Long id) {
        return ResponseEntity.ok(repoService.getRepoById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Repository>> getMyRepos(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return ResponseEntity.ok(repoService.getReposByOwner(userId));
    }
}
