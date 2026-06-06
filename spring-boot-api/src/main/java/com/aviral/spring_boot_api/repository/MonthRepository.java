package com.aviral.spring_boot_api.repository;

import com.aviral.spring_boot_api.model.Month;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MonthRepository extends JpaRepository<Month, Long> {
    Optional<Month> findByMonthName(String monthName);
}