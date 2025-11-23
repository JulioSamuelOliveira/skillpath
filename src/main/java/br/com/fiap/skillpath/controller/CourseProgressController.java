package br.com.fiap.skillpath.controller;

import br.com.fiap.skillpath.dto.CourseProgressDto;
import br.com.fiap.skillpath.model.CourseProgress;
import br.com.fiap.skillpath.model.User;
import br.com.fiap.skillpath.repository.CourseProgressRepository;
import br.com.fiap.skillpath.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/progress")
public class CourseProgressController {

    private final CourseProgressRepository progressRepository;
    private final UserRepository userRepository;

    public CourseProgressController(CourseProgressRepository progressRepository,
                                    UserRepository userRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<CourseProgress>> list(
            @PathVariable Long userId,
            @RequestParam(required = false) String trackId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (trackId == null || trackId.isBlank()) {
            return ResponseEntity.ok(progressRepository.findByUser(user));
        }

        return ResponseEntity.ok(progressRepository.findByUserAndTrackId(user, trackId));
    }

    @PostMapping
    public ResponseEntity<CourseProgress> create(@PathVariable Long userId,
                                                @RequestBody CourseProgressDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CourseProgress cp = new CourseProgress();
        cp.setUser(user);
        cp.setTrackId(dto.trackId());
        cp.setCourseId(dto.courseId());
        cp.setPercent(dto.percent());
        cp.setUpdatedAt(LocalDateTime.now());

        cp = progressRepository.save(cp);
        return ResponseEntity.ok(cp);
    }

    @PutMapping("/{progressId}")
    public ResponseEntity<CourseProgress> update(@PathVariable Long userId,
                                                 @PathVariable Long progressId,
                                                 @RequestBody CourseProgressDto dto) {
        return progressRepository.findById(progressId)
                .map(existing -> {
                    existing.setPercent(dto.percent());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(progressRepository.save(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{progressId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId,
                                       @PathVariable Long progressId) {
        if (!progressRepository.existsById(progressId)) {
            return ResponseEntity.notFound().build();
        }
        progressRepository.deleteById(progressId);
        return ResponseEntity.noContent().build();
    }
}
