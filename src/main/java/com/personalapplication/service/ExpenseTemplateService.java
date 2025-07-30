package com.personalapplication.service;

import com.personalapplication.domain.ExpenseTemplate;
import com.personalapplication.domain.ScheduledExpense;
import com.personalapplication.domain.User;
import com.personalapplication.repository.ExpenseTemplateRepository;
import com.personalapplication.repository.ScheduledExpenseRepository;
import com.personalapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseTemplateService {

    @Autowired
    private ExpenseTemplateRepository expenseTemplateRepository;

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
     * Get all active expense templates for the current user (for left panel display)
     */
    public List<ExpenseTemplate> getAllActiveTemplates() {
        User currentUser = getCurrentUser();
        return expenseTemplateRepository.findByUserIdAndActiveTrue(currentUser.getId());
    }

    /**
     * Create a new expense template for the current user
     */
    public ExpenseTemplate createTemplate(ExpenseTemplate template) {
        User currentUser = getCurrentUser();
        template.setUser(currentUser);
        return expenseTemplateRepository.save(template);
    }

    /**
     * Update an existing template
     * @param templateId - the template to update
     * @param updatedTemplate - new template data
     * @param updateFutureOnly - if true, only affects future instances; if false, updates past instances too
     */
    public ExpenseTemplate updateTemplate(Long templateId, ExpenseTemplate updatedTemplate, boolean updateFutureOnly) {
        User currentUser = getCurrentUser();
        ExpenseTemplate existingTemplate = expenseTemplateRepository.findByUserIdAndId(currentUser.getId(), templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        // Update the template
        existingTemplate.setName(updatedTemplate.getName());
        existingTemplate.setAmount(updatedTemplate.getAmount());
        existingTemplate.setRecurrenceType(updatedTemplate.getRecurrenceType());
        existingTemplate.setDayOfMonth(updatedTemplate.getDayOfMonth());
        existingTemplate.setDayOfWeek(updatedTemplate.getDayOfWeek());
        existingTemplate.setBiWeeklyStartDate(updatedTemplate.getBiWeeklyStartDate());

        ExpenseTemplate savedTemplate = expenseTemplateRepository.save(existingTemplate);

        // If updateFutureOnly is false, update existing scheduled expenses
        if (!updateFutureOnly) {
            List<ScheduledExpense> existingExpenses = scheduledExpenseRepository.findByUserIdAndTemplateId(currentUser.getId(), templateId);
            LocalDate today = LocalDate.now();

            for (ScheduledExpense expense : existingExpenses) {
                // Only update future or today's expenses
                if (!expense.getScheduledDate().isBefore(today)) {
                    expense.setName(savedTemplate.getName());
                    expense.setAmount(savedTemplate.getAmount());
                    scheduledExpenseRepository.save(expense);
                }
            }
        }

        return savedTemplate;
    }

    /**
     * Delete a template (mark as inactive)
     * @param templateId - template to delete
     * @param deleteFutureInstances - if true, also delete future scheduled instances
     */
    public void deleteTemplate(Long templateId, boolean deleteFutureInstances) {
        User currentUser = getCurrentUser();
        ExpenseTemplate template = expenseTemplateRepository.findByUserIdAndId(currentUser.getId(), templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        // Mark template as inactive
        template.setActive(false);
        expenseTemplateRepository.save(template);

        if (deleteFutureInstances) {
            List<ScheduledExpense> futureExpenses = scheduledExpenseRepository.findByUserIdAndTemplateId(currentUser.getId(), templateId);
            LocalDate today = LocalDate.now();

            // Delete future instances
            for (ScheduledExpense expense : futureExpenses) {
                if (!expense.getScheduledDate().isBefore(today)) {
                    scheduledExpenseRepository.delete(expense);
                }
            }
        }
    }

    /**
     * Get a specific template by ID for the current user
     */
    public ExpenseTemplate getTemplate(Long templateId) {
        User currentUser = getCurrentUser();
        return expenseTemplateRepository.findByUserIdAndId(currentUser.getId(), templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
    }

    /**
     * Check if a template has scheduled instances for a given month
     */
    public boolean hasInstancesInMonth(Long templateId, int year, int month) {
        User currentUser = getCurrentUser();
        List<ScheduledExpense> expenses = scheduledExpenseRepository.findByUserIdAndTemplateId(currentUser.getId(), templateId);
        return expenses.stream()
                .anyMatch(e -> e.getYearValue() == year && e.getMonthValue() == month);
    }
}