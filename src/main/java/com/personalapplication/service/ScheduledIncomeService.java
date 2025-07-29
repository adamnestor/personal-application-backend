package com.personalapplication.service;

import com.personalapplication.domain.ScheduledIncome;
import com.personalapplication.repository.ScheduledIncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledIncomeService {

    @Autowired
    private ScheduledIncomeRepository scheduledIncomeRepository;

    /**
     * Create a new scheduled income
     */
    public ScheduledIncome createIncome(String name, BigDecimal amount, LocalDate scheduledDate) {
        ScheduledIncome income = new ScheduledIncome(name, amount, scheduledDate);
        return scheduledIncomeRepository.save(income);
    }

    /**
     * Update a specific scheduled income instance (click to edit on calendar)
     */
    public ScheduledIncome updateIncome(Long incomeId, String name, BigDecimal amount, LocalDate scheduledDate) {
        ScheduledIncome income = scheduledIncomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        income.setName(name);
        income.setAmount(amount);
        income.setScheduledDate(scheduledDate);

        return scheduledIncomeRepository.save(income);
    }

    /**
     * Delete a specific scheduled income
     */
    public void deleteIncome(Long incomeId) {
        ScheduledIncome income = scheduledIncomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        scheduledIncomeRepository.delete(income);
    }

    /**
     * Get a specific income by ID
     */
    public ScheduledIncome getIncome(Long incomeId) {
        return scheduledIncomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
    }

    /**
     * Get all income for a specific month
     */
    public List<ScheduledIncome> getIncomeForMonth(int year, int month) {
        return scheduledIncomeRepository.findByYearAndMonthValueOrderByScheduledDate(year, month);
    }

    /**
     * Move an income to a different date (drag and drop)
     */
    public ScheduledIncome moveIncome(Long incomeId, LocalDate newDate) {
        ScheduledIncome income = scheduledIncomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        income.setScheduledDate(newDate);
        return scheduledIncomeRepository.save(income);
    }

    /**
     * Update just the amount of an income (for quick edits)
     */
    public ScheduledIncome updateIncomeAmount(Long incomeId, BigDecimal newAmount) {
        ScheduledIncome income = scheduledIncomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        income.setAmount(newAmount);
        return scheduledIncomeRepository.save(income);
    }
}