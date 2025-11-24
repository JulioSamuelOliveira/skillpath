package br.com.fiap.skillpath.service;

import br.com.fiap.skillpath.dto.AiRecommendationRequest;
import br.com.fiap.skillpath.dto.AiRecommendationResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiUrl;
    private final String apiToken;

    public AiService(RestTemplate restTemplate,
                     ObjectMapper objectMapper,
                     @Value("${ai.hf.api-url}") String apiUrl,
                     @Value("${ai.hf.api-token}") String apiToken) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
    }

    public AiRecommendationResponse generateStudyPlan(AiRecommendationRequest request, String userName, String userEmail) {
        String prompt = buildPrompt(request, userName, userEmail);

        // Corpo de requisição esperado pela Hugging Face Inference API (text-generation)
        Map<String, Object> body = Map.of(
                "inputs", prompt,
                "parameters", Map.of(
                        "max_new_tokens", 400,
                        "temperature", 0.7
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            // Fallback simples em caso de erro
            return new AiRecommendationResponse("Não foi possível gerar um plano com a IA neste momento.");
        }

        try {
            // Resposta típica da HF text-generation: uma lista de objetos com "generated_text"
            List<Map<String, Object>> list = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            if (list == null || list.isEmpty()) {
                return new AiRecommendationResponse("A IA não retornou nenhum texto.");
            }

            Object generated = list.get(0).get("generated_text");
            String generatedText = generated != null
                    ? generated.toString()
                    : "A IA não retornou nenhum texto.";

            return new AiRecommendationResponse(generatedText);
        } catch (Exception e) {
            return new AiRecommendationResponse("Erro ao interpretar a resposta da IA.");
        }
    }

    private String buildPrompt(AiRecommendationRequest request, String userName, String userEmail) {
        String targetArea = request.targetArea() != null ? request.targetArea() : "Tecnologia";
        String level = request.experienceLevel() != null ? request.experienceLevel() : "iniciante";
        Integer hours = request.hoursPerWeek() != null ? request.hoursPerWeek() : 5;

        return """
                Você é um orientador de carreira e estudos.
                Crie um plano de estudo inicial para transição de carreira.

                Dados do usuário:
                - Nome: %s
                - E-mail: %s
                - Área alvo: %s
                - Nível de experiência: %s
                - Horas disponíveis por semana: %d

                Regras:
                - Escreva em português claro, direto e motivador.
                - Organize em tópicos com semanas (Semana 1, Semana 2, etc.).
                - Para cada semana, sugira 2 a 4 temas de estudo.
                - Foque em fundamentos importantes para alguém em transição de carreira.
                - No final, inclua uma seção "Próximos passos" com dicas práticas.

                Responda apenas com o plano, sem explicação adicional.
                """.formatted(userName, userEmail, targetArea, level, hours);
    }
}
