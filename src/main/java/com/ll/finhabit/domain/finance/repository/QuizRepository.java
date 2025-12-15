package com.ll.finhabit.domain.finance.repository;

import com.ll.finhabit.domain.finance.entity.Quiz;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByQuizId(Integer quizId);
}
