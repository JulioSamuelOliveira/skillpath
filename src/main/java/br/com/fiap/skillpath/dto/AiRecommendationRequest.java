package br.com.fiap.skillpath.dto;

public record AiRecommendationRequest(
        String targetArea,        // Ex: "Tecnologia", "Contabilidade"
        String experienceLevel,   // Ex: "iniciante", "intermediário"
        Integer hoursPerWeek      // Ex: 5, 10
) {
}
