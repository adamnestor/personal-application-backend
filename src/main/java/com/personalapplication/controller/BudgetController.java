package com.personalapplication.controller;

import com.personalapplication.domain.ScheduledExpense;
import com.personalapplication.domain.ScheduledIncome;
import com.personalapplication.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "http://localhost:3000") // For React dev server
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    /**
     * Get complete monthly budget data
     * GET /api/budget/month/{year}/{month}
     */
    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<MonthlyBudgetResponse> getMonthlyBudget(
            @PathVariable int year,
            @PathVariable int month) {

        // Generate expenses from templates if needed
        budgetService.generateMonthlyExpenses(year, month);

        // Get all data for the month
        List<ScheduledExpense> expenses = budgetService.getMonthlyExpenses(year, month);
        List<ScheduledIncome> income = budgetService.getMonthlyIncome(year, month);
        Map<LocalDate, BigDecimal> dailyBalances = budgetService.calculateDailyBalances(year, month);

        MonthlyBudgetResponse response = new MonthlyBudgetResponse(expenses, income, dailyBalances);
        return ResponseEntity.ok(response);
    }

    /**
     * Get only daily balances for a month (for calendar display)
     * GET /api/budget/balances/{year}/{month}
     */
    @GetMapping("/balances/{year}/{month}")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getDailyBalances(
            @PathVariable int year,
            @PathVariable int month) {

        Map<LocalDate, BigDecimal> balances = budgetService.calculateDailyBalances(year, month);
        return ResponseEntity.ok(balances);
    }

    /**
     * Move an expense to a different date (drag and drop on calendar)
     * PUT /api/budget/expense/{id}/move
     */
    @PutMapping("/expense/{id}/move")
    public ResponseEntity<ScheduledExpense> moveExpense(
            @PathVariable Long id,
            @RequestBody MoveTransactionRequest request) {

        ScheduledExpense moved = budgetService.moveExpense(id, request.getNewDate());
        return ResponseEntity.ok(moved);
    }

    /**
     * Move an income to a different date (drag and drop on calendar)
     * PUT /api/budget/income/{id}/move
     */
    @PutMapping("/income/{id}/move")
    public ResponseEntity<ScheduledIncome> moveIncome(
            @PathVariable Long id,
            @RequestBody MoveTransactionRequest request) {

        ScheduledIncome moved = budgetService.moveIncome(id, request.getNewDate());
        return ResponseEntity.ok(moved);
    }

    /**
     * Create expense instance from template (drag from left panel)
     * POST /api/budget/expense/from-template
     */
    @PostMapping("/expense/from-template")
    public ResponseEntity<ScheduledExpense> createExpenseFromTemplate(
            @RequestBody CreateFromTemplateRequest request) {

        ScheduledExpense expense = budgetService.createExpenseFromTemplate(
                request.getTemplateId(),
                request.getScheduledDate()
        );
        return ResponseEntity.ok(expense);
    }

    // DTO Classes
    public static class MonthlyBudgetResponse {
        private List<ScheduledExpense> expenses;
        private List<ScheduledIncome> income;
        private Map<LocalDate, BigDecimal> dailyBalances;

        public MonthlyBudgetResponse(List<ScheduledExpense> expenses, List<ScheduledIncome> income,
                                     Map<LocalDate, BigDecimal> dailyBalances) {
            this.expenses = expenses;
            this.income = income;
            this.dailyBalances = dailyBalances;
        }

        // Getters and setters
        public List<ScheduledExpense> getExpenses() { return expenses; }
        public void setExpenses(List<ScheduledExpense> expenses) { this.expenses = expenses; }

        public List<ScheduledIncome> getIncome() { return income; }
        public void setIncome(List<ScheduledIncome> income) { this.income = income; }

        public Map<LocalDate, BigDecimal> getDailyBalances() { return dailyBalances; }
        public void setDailyBalances(Map<LocalDate, BigDecimal> dailyBalances) { this.dailyBalances = dailyBalances; }
    }

    public static class MoveTransactionRequest {
        private LocalDate newDate;

        public LocalDate getNewDate() { return newDate; }
        public void setNewDate(LocalDate newDate) { this.newDate = newDate; }
    }

    public static class CreateFromTemplateRequest {
        private Long templateId;
        private LocalDate scheduledDate;

        public Long getTemplateId() { return templateId; }
        public void setTemplateId(Long templateId) { this.templateId = templateId; }

        public LocalDate getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    }
}