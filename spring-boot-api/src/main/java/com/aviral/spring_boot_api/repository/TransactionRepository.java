package com.aviral.spring_boot_api.repository;

import com.aviral.spring_boot_api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCategoryId(Long categoryId);
}
