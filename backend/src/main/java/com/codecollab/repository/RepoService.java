package com.codecollab.repository;

import com.codecollab.repository.model.Repository;
import com.codecollab.user.UserRepository;
import com.codecollab.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepoService {

    private final RepoRepository repoRepository;
    private final UserRepository userRepository;

    @Transactional
    public Repository createRepo(Long ownerId, CreateRepoRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Repository repo = Repository.builder()
                .name(request.name())
                .description(request.description())
                .language(request.language())
                .owner(owner)
                .build();

        return repoRepository.save(repo);
    }

    public List<Repository> getAllRepos() {
        return repoRepository.findAll();
    }

    public Repository getRepoById(Long id) {
        return repoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found: " + id));
    }

    public List<Repository> getReposByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return repoRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }
}
