package com.personalapplication.controller;

import com.personalapplication.domain.ScheduledIncome;
import com.personalapplication.service.ScheduledIncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/income")
@CrossOrigin(origins = "http://localhost:3000")
public class ScheduledIncomeController {

    @Autowired
    private ScheduledIncomeService scheduledIncomeService;

    /**
     * Get all income for a specific month
     * GET /api/income/{year}/{month}
     */
    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<ScheduledIncome>> getIncomeForMonth(
            @PathVariable int year,
            @PathVariable int month) {

        List<ScheduledIncome> income = scheduledIncomeService.getIncomeForMonth(year, month);
        return ResponseEntity.ok(income);
    }

    /**
     * Get a specific income by ID
     * GET /api/income/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduledIncome> getIncome(@PathVariable Long id) {
        ScheduledIncome income = scheduledIncomeService.getIncome(id);
        return ResponseEntity.ok(income);
    }

    /**
     * Create a new scheduled income
     * POST /api/income
     */
    @PostMapping
    public ResponseEntity<ScheduledIncome> createIncome(@RequestBody CreateIncomeRequest request) {
        ScheduledIncome income = scheduledIncomeService.createIncome(
                request.getName(),
                request.getAmount(),
                request.getScheduledDate()
        );
        return ResponseEntity.ok(income);
    }

    /**
     * Update a specific income (click to edit on calendar)
     * PUT /api/income/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduledIncome> updateIncome(
            @PathVariable Long id,
            @RequestBody UpdateIncomeRequest request) {

        ScheduledIncome updated = scheduledIncomeService.updateIncome(
                id,
                request.getName(),
                request.getAmount(),
                request.getScheduledDate()
        );
        return ResponseEntity.ok(updated);
    }

    /**
     * Update just the amount of an income (quick edit)
     * PATCH /api/income/{id}/amount
     */
    @PatchMapping("/{id}/amount")
    public ResponseEntity<ScheduledIncome> updateIncomeAmount(
            @PathVariable Long id,
            @RequestBody UpdateAmountRequest request) {

        ScheduledIncome updated = scheduledIncomeService.updateIncomeAmount(id, request.getAmount());
        return ResponseEntity.ok(updated);
    }

    /**
     * Move an income to a different date (drag and drop)
     * PUT /api/income/{id}/move
     */
    @PutMapping("/{id}/move")
    public ResponseEntity<ScheduledIncome> moveIncome(
            @PathVariable Long id,
            @RequestBody MoveIncomeRequest request) {

        ScheduledIncome moved = scheduledIncomeService.moveIncome(id, request.getNewDate());
        return ResponseEntity.ok(moved);
    }

    /**
     * Delete a specific income
     * DELETE /api/income/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        scheduledIncomeService.deleteIncome(id);
        return ResponseEntity.ok().build();
    }

    // DTO Classes
    public static class CreateIncomeRequest {
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

    public static class UpdateIncomeRequest {
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

    public static class MoveIncomeRequest {
        private LocalDate newDate;

        public LocalDate getNewDate() { return newDate; }
        public void setNewDate(LocalDate newDate) { this.newDate = newDate; }
    }
}