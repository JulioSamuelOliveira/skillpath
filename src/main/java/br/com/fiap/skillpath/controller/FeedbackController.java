package br.com.fiap.skillpath.controller;

import br.com.fiap.skillpath.dto.FeedbackInput;
import br.com.fiap.skillpath.model.Feedback;
import br.com.fiap.skillpath.model.User;
import br.com.fiap.skillpath.repository.FeedbackRepository;
import br.com.fiap.skillpath.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackController(FeedbackRepository feedbackRepository,
                              UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> list(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.ok(feedbackRepository.findAll());
        }
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(feedbackRepository.findByUser(user)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping
    public ResponseEntity<Feedback> create(@RequestBody FeedbackInput input) {
        User user = userRepository.findById(input.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Feedback fb = new Feedback();
        fb.setUser(user);
        fb.setRating(input.rating());
        fb.setTitle(input.title());
        fb.setMessage(input.message());

        fb = feedbackRepository.save(fb);
        return ResponseEntity.ok(fb);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> update(@PathVariable Long id,
                                           @RequestBody FeedbackInput input) {
        return feedbackRepository.findById(id)
                .map(existing -> {
                    existing.setRating(input.rating());
                    existing.setTitle(input.title());
                    existing.setMessage(input.message());
                    return ResponseEntity.ok(feedbackRepository.save(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!feedbackRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        feedbackRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
