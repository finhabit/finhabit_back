package com.ll.finhabit.domain.auth.repository;

import com.ll.finhabit.domain.auth.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(Long userId);

    Optional<User> findByNickname(String nickname);
}
