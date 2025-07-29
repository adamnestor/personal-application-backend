package com.personalapplication.service;

import com.personalapplication.domain.ExpenseTemplate;
import com.personalapplication.domain.ScheduledExpense;
import com.personalapplication.repository.ExpenseTemplateRepository;
import com.personalapplication.repository.ScheduledExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseTemplateService {

    @Autowired
    private ExpenseTemplateRepository expenseTemplateRepository;

    @Autowired
    private ScheduledExpenseRepository scheduledExpenseRepository;

    /**
     * Get all active expense templates (for left panel display)
     */
    public List<ExpenseTemplate> getAllActiveTemplates() {
        return expenseTemplateRepository.findByActiveTrue();
    }

    /**
     * Create a new expense template
     */
    public ExpenseTemplate createTemplate(ExpenseTemplate template) {
        return expenseTemplateRepository.save(template);
    }

    /**
     * Update an existing template
     * @param templateId - the template to update
     * @param updatedTemplate - new template data
     * @param updateFutureOnly - if true, only affects future instances; if false, updates past instances too
     */
    public ExpenseTemplate updateTemplate(Long templateId, ExpenseTemplate updatedTemplate, boolean updateFutureOnly) {
        ExpenseTemplate existingTemplate = expenseTemplateRepository.findById(templateId)
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
            List<ScheduledExpense> existingExpenses = scheduledExpenseRepository.findByTemplateId(templateId);
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
        ExpenseTemplate template = expenseTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        // Mark template as inactive
        template.setActive(false);
        expenseTemplateRepository.save(template);

        if (deleteFutureInstances) {
            List<ScheduledExpense> futureExpenses = scheduledExpenseRepository.findByTemplateId(templateId);
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
     * Get a specific template by ID
     */
    public ExpenseTemplate getTemplate(Long templateId) {
        return expenseTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
    }

    /**
     * Check if a template has scheduled instances for a given month
     */
    public boolean hasInstancesInMonth(Long templateId, int year, int month) {
        List<ScheduledExpense> expenses = scheduledExpenseRepository.findByTemplateId(templateId);
        return expenses.stream()
                .anyMatch(e -> e.getYearValue() == year && e.getMonthValue() == month);
    }
}