package com.personalapplication.repository;

import com.personalapplication.domain.ScheduledIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledIncomeRepository extends JpaRepository<ScheduledIncome, Long> {
    List<ScheduledIncome> findByUserIdAndYearValueAndMonthValueOrderByScheduledDate(Long userId, int yearValue, int monthValue);
    Optional<ScheduledIncome> findByUserIdAndId(Long userId, Long incomeId);
}