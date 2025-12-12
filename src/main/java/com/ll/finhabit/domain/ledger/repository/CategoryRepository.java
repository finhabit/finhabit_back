package com.ll.finhabit.domain.ledger.repository;

import com.ll.finhabit.domain.ledger.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
