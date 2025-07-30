package com.personalapplication.service;

import com.personalapplication.domain.ScheduledIncome;
import com.personalapplication.domain.User;
import com.personalapplication.repository.ScheduledIncomeRepository;
import com.personalapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledIncomeService {

    @Autowired
    private ScheduledIncomeRepository scheduledIncomeRepository;

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
     * Create a new scheduled income for the current user
     */
    public ScheduledIncome createIncome(String name, BigDecimal amount, LocalDate scheduledDate) {
        User currentUser = getCurrentUser();
        ScheduledIncome income = new ScheduledIncome(name, amount, scheduledDate, currentUser);
        return scheduledIncomeRepository.save(income);
    }

    /**
     * Update a specific scheduled income instance (click to edit on calendar)
     */
    public ScheduledIncome updateIncome(Long incomeId, String name, BigDecimal amount, LocalDate scheduledDate) {
        User currentUser = getCurrentUser();
        ScheduledIncome income = scheduledIncomeRepository.findByUserIdAndId(currentUser.getId(), incomeId)
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
        User currentUser = getCurrentUser();
        ScheduledIncome income = scheduledIncomeRepository.findByUserIdAndId(currentUser.getId(), incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        scheduledIncomeRepository.delete(income);
    }

    /**
     * Get a specific income by ID for the current user
     */
    public ScheduledIncome getIncome(Long incomeId) {
        User currentUser = getCurrentUser();
        return scheduledIncomeRepository.findByUserIdAndId(currentUser.getId(), incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
    }

    /**
     * Get income for a month for the current user
     */
    public List<ScheduledIncome> getIncomeForMonth(int year, int month) {
        User currentUser = getCurrentUser();
        return scheduledIncomeRepository.findByUserIdAndYearValueAndMonthValueOrderByScheduledDate(
                currentUser.getId(), year, month);
    }

    /**
     * Move an income to a different date (drag and drop)
     */
    public ScheduledIncome moveIncome(Long incomeId, LocalDate newDate) {
        User currentUser = getCurrentUser();
        ScheduledIncome income = scheduledIncomeRepository.findByUserIdAndId(currentUser.getId(), incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        income.setScheduledDate(newDate);
        return scheduledIncomeRepository.save(income);
    }

    /**
     * Update just the amount of an income (for quick edits)
     */
    public ScheduledIncome updateIncomeAmount(Long incomeId, BigDecimal newAmount) {
        User currentUser = getCurrentUser();
        ScheduledIncome income = scheduledIncomeRepository.findByUserIdAndId(currentUser.getId(), incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        income.setAmount(newAmount);
        return scheduledIncomeRepository.save(income);
    }
}