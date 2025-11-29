package com.ll.finhabit.domain.auth.repository;

import com.ll.finhabit.domain.auth.entity.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {

    List<UserLevel> findByUserId(Long userId);
}
