package com.codecollab.repository;

import com.codecollab.repository.model.Repository;
import com.codecollab.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface RepoRepository extends JpaRepository<Repository, Long> {
    List<Repository> findByOwner(User owner);
    List<Repository> findByOwnerOrderByCreatedAtDesc(User owner);
}
