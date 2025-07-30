package com.personalapplication.service;

import com.personalapplication.domain.ScheduledExpense;
import com.personalapplication.domain.User;
import com.personalapplication.repository.ScheduledExpenseRepository;
import com.personalapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledExpenseService {

    @Autowired
    private ScheduledExpenseRepository scheduledExpenseRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get the current authenticated user
     */
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Create a new one-time scheduled expense for the current user
     */
    public ScheduledExpense createExpense(String name, BigDecimal amount, LocalDate scheduledDate) {
        User currentUser = getCurrentUser();
        ScheduledExpense expense = new ScheduledExpense(name, amount, scheduledDate, currentUser);
        return scheduledExpenseRepository.save(expense);
    }

    /**
     * Update a specific scheduled expense instance (click to edit on calendar)
     */
    public ScheduledExpense updateExpense(Long expenseId, String name, BigDecimal amount, LocalDate scheduledDate) {
        User currentUser = getCurrentUser();
        ScheduledExpense expense = scheduledExpenseRepository.findByUserIdAndId(currentUser.getId(), expenseId)
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
        User currentUser = getCurrentUser();
        ScheduledExpense expense = scheduledExpenseRepository.findByUserIdAndId(currentUser.getId(), expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        scheduledExpenseRepository.delete(expense);
    }

    /**
     * Get a specific expense by ID for the current user
     */
    public ScheduledExpense getExpense(Long expenseId) {
        User currentUser = getCurrentUser();
        return scheduledExpenseRepository.findByUserIdAndId(currentUser.getId(), expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    /**
     * Get expenses for a month for the current user
     */
    public List<ScheduledExpense> getExpensesForMonth(int year, int month) {
        User currentUser = getCurrentUser();
        return scheduledExpenseRepository.findByUserIdAndYearValueAndMonthValueOrderByScheduledDate(
                currentUser.getId(), year, month);
    }

    /**
     * Move an expense to a different date (drag and drop)
     */
    public ScheduledExpense moveExpense(Long expenseId, LocalDate newDate) {
        User currentUser = getCurrentUser();
        ScheduledExpense expense = scheduledExpenseRepository.findByUserIdAndId(currentUser.getId(), expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setScheduledDate(newDate);
        return scheduledExpenseRepository.save(expense);
    }

    /**
     * Update just the amount of an expense (for quick edits like extra mortgage payment)
     */
    public ScheduledExpense updateExpenseAmount(Long expenseId, BigDecimal newAmount) {
        User currentUser = getCurrentUser();
        ScheduledExpense expense = scheduledExpenseRepository.findByUserIdAndId(currentUser.getId(), expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setAmount(newAmount);
        return scheduledExpenseRepository.save(expense);
    }
}