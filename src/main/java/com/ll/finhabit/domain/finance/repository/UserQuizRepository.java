package com.ll.finhabit.domain.finance.repository;

import com.ll.finhabit.domain.finance.entity.UserQuiz;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuizRepository extends JpaRepository<UserQuiz, Long> {
    Optional<UserQuiz> findByUserIdAndAttemptedDate(Long userId, LocalDate attemptedDate);

    List<UserQuiz> findByUserIdOrderByAttemptedDateDesc(Long userId);
}
