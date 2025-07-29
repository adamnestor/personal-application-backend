package com.personalapplication.service;

import com.personalapplication.domain.ScheduledExpense;
import com.personalapplication.repository.ScheduledExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledExpenseService {

    @Autowired
    private ScheduledExpenseRepository scheduledExpenseRepository;

    /**
     * Create a new one-time scheduled expense
     */
    public ScheduledExpense createExpense(String name, BigDecimal amount, LocalDate scheduledDate) {
        ScheduledExpense expense = new ScheduledExpense(name, amount, scheduledDate);
        return scheduledExpenseRepository.save(expense);
    }

    /**
     * Update a specific scheduled expense instance (click to edit on calendar)
     */
    public ScheduledExpense updateExpense(Long expenseId, String name, BigDecimal amount, LocalDate scheduledDate) {
        ScheduledExpense expense = scheduledExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setName(name);
        expense.setAmount(amount);
        expense.setScheduledDate(scheduledDate);

        return scheduledExpenseRepository.save(expense);
    }

    /**
     * Delete a specific scheduled expense
     */
    public void deleteExpense(Long expenseId) {
        ScheduledExpense expense = scheduledExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        scheduledExpenseRepository.delete(expense);
    }

    /**
     * Get a specific expense by ID
     */
    public ScheduledExpense getExpense(Long expenseId) {
        return scheduledExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    /**
     * Get all expenses for a specific month
     */
    public List<ScheduledExpense> getExpensesForMonth(int year, int month) {
        return scheduledExpenseRepository.findByYearAndMonthOrderByScheduledDate(year, month);
    }

    /**
     * Move an expense to a different date (drag and drop)
     */
    public ScheduledExpense moveExpense(Long expenseId, LocalDate newDate) {
        ScheduledExpense expense = scheduledExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setScheduledDate(newDate);
        return scheduledExpenseRepository.save(expense);
    }

    /**
     * Update just the amount of an expense (for quick edits like extra mortgage payment)
     */
    public ScheduledExpense updateExpenseAmount(Long expenseId, BigDecimal newAmount) {
        ScheduledExpense expense = scheduledExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setAmount(newAmount);
        return scheduledExpenseRepository.save(expense);
    }
}