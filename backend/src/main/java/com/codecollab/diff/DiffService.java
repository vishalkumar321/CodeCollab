package com.codecollab.diff;

import com.codecollab.pullrequest.PRFileRepository;
import com.codecollab.pullrequest.model.PRFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiffService {

    private final PRFileRepository prFileRepository;
    private final LineLevelDiffStrategy diffStrategy; // Injecting concrete strategy

    public List<DiffLine> getDiff(Long fileId) {
        PRFile file = prFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));
        return diffStrategy.computeDiff(file.getBaseContent(), file.getChangedContent());
    }
}
