package br.com.fiap.skillpath.controller;

import br.com.fiap.skillpath.dto.AiRecommendationRequest;
import br.com.fiap.skillpath.dto.AiRecommendationResponse;
import br.com.fiap.skillpath.model.User;
import br.com.fiap.skillpath.repository.UserRepository;
import br.com.fiap.skillpath.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;
    private final UserRepository userRepository;

    public AiController(AiService aiService, UserRepository userRepository) {
        this.aiService = aiService;
        this.userRepository = userRepository;
    }

    @PostMapping("/recommendations")
    public ResponseEntity<AiRecommendationResponse> generateRecommendations(
            @RequestBody AiRecommendationRequest request
    ) {
        // Pega usuário logado pelo JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));

        AiRecommendationResponse response =
                aiService.generateStudyPlan(request, user.getName(), user.getEmail());

        return ResponseEntity.ok(response);
    }
}
