package br.com.fiap.skillpath.dto;

public record CourseProgressDto(
        Long id,
        Long userId,
        String trackId,
        String courseId,
        Integer percent
) {}
