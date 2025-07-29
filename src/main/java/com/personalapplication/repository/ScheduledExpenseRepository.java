package com.personalapplication.repository;

import com.personalapplication.domain.ScheduledExpense;
import com.personalapplication.domain.ExpenseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduledExpenseRepository extends JpaRepository<ScheduledExpense, Long> {
    List<ScheduledExpense> findByYearValueAndMonthValueOrderByScheduledDate(int yearValue, int monthValue);
    boolean existsByTemplateAndScheduledDate(ExpenseTemplate template, LocalDate scheduledDate);
    List<ScheduledExpense> findByTemplateId(Long templateId);
}