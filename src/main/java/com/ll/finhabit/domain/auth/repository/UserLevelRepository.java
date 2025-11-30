package com.ll.finhabit.domain.auth.repository;

import com.ll.finhabit.domain.auth.entity.UserLevel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {

    List<UserLevel> findByUserId(Long userId);
}
