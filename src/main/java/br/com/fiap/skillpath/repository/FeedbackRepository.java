package br.com.fiap.skillpath.repository;

import br.com.fiap.skillpath.model.Feedback;
import br.com.fiap.skillpath.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByUser(User user);
}
