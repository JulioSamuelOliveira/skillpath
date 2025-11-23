package br.com.fiap.skillpath.repository;

import br.com.fiap.skillpath.model.CourseProgress;
import br.com.fiap.skillpath.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {

    List<CourseProgress> findByUser(User user);

    List<CourseProgress> findByUserAndTrackId(User user, String trackId);
}
