package com.codecollab.pullrequest;

import com.codecollab.pullrequest.model.PRFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PRFileRepository extends JpaRepository<PRFile, Long> {
    List<PRFile> findByPullRequestId(Long prId);
}
