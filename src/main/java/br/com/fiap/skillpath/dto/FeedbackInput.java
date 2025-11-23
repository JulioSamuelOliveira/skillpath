package br.com.fiap.skillpath.dto;

public record FeedbackInput(
    Long userId,
    Integer rating,
    String title,
    String message
) {}
