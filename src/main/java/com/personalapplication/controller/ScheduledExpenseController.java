package com.personalapplication.controller;

import com.personalapplication.domain.ScheduledExpense;
import com.personalapplication.service.ScheduledExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:3000")
public class ScheduledExpenseController {

    @Autowired
    private ScheduledExpenseService scheduledExpenseService;

    /**
     * Get all expenses for a specific month
     * GET /api/expenses/{year}/{month}
     */
    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<ScheduledExpense>> getExpensesForMonth(
            @PathVariable int year,
            @PathVariable int month) {

        List<ScheduledExpense> expenses = scheduledExpenseService.getExpensesForMonth(year, month);
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get a specific expense by ID
     * GET /api/expenses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduledExpense> getExpense(@PathVariable Long id) {
        ScheduledExpense expense = scheduledExpenseService.getExpense(id);
        return ResponseEntity.ok(expense);
    }

    /**
     * Create a new one-time expense
     * POST /api/expenses
     */
    @PostMapping
    public ResponseEntity<ScheduledExpense> createExpense(@RequestBody CreateExpenseRequest request) {
        ScheduledExpense expense = scheduledExpenseService.createExpense(
                request.getName(),
                request.getAmount(),
                request.getScheduledDate()
        );
        return ResponseEntity.ok(expense);
    }

    /**
     * Update a specific expense (click to edit on calendar)
     * PUT /api/expenses/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduledExpense> updateExpense(
            @PathVariable Long id,
            @RequestBody UpdateExpenseRequest request) {

        ScheduledExpense updated = scheduledExpenseService.updateExpense(
                id,
                request.getName(),
                request.getAmount(),
                request.getScheduledDate()
        );
        return ResponseEntity.ok(updated);
    }

    /**
     * Update just the amount of an expense (quick edit)
     * PATCH /api/expenses/{id}/amount
     */
    @PatchMapping("/{id}/amount")
    public ResponseEntity<ScheduledExpense> updateExpenseAmount(
            @PathVariable Long id,
            @RequestBody UpdateAmountRequest request) {

        ScheduledExpense updated = scheduledExpenseService.updateExpenseAmount(id, request.getAmount());
        return ResponseEntity.ok(updated);
    }

    /**
     * Move an expense to a different date (drag and drop)
     * PUT /api/expenses/{id}/move
     */
    @PutMapping("/{id}/move")
    public ResponseEntity<ScheduledExpense> moveExpense(
            @PathVariable Long id,
            @RequestBody MoveExpenseRequest request) {

        ScheduledExpense moved = scheduledExpenseService.moveExpense(id, request.getNewDate());
        return ResponseEntity.ok(moved);
    }

    /**
     * Delete a specific expense
     * DELETE /api/expenses/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        scheduledExpenseService.deleteExpense(id);
        return ResponseEntity.ok().build();
    }

    // DTO Classes
    public static class CreateExpenseRequest {
        private String name;
        private BigDecimal amount;
        private LocalDate scheduledDate;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public LocalDate getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    }

    public static class UpdateExpenseRequest {
        private String name;
        private BigDecimal amount;
        private LocalDate scheduledDate;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public LocalDate getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    }

    public static class UpdateAmountRequest {
        private BigDecimal amount;

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public static class MoveExpenseRequest {
        private LocalDate newDate;

        public LocalDate getNewDate() { return newDate; }
        public void setNewDate(LocalDate newDate) { this.newDate = newDate; }
    }
}