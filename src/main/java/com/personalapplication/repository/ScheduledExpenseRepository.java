package com.personalapplication.repository;

import com.personalapplication.domain.ScheduledExpense;
import com.personalapplication.domain.ExpenseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledExpenseRepository extends JpaRepository<ScheduledExpense, Long> {
    List<ScheduledExpense> findByUserIdAndYearValueAndMonthValueOrderByScheduledDate(Long userId, int yearValue, int monthValue);
    boolean existsByUserIdAndTemplateAndScheduledDate(Long userId, ExpenseTemplate template, LocalDate scheduledDate);
    List<ScheduledExpense> findByUserIdAndTemplateId(Long userId, Long templateId);
    Optional<ScheduledExpense> findByUserIdAndId(Long userId, Long expenseId);
}