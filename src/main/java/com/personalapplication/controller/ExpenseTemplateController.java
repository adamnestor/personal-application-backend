package com.personalapplication.controller;

import com.personalapplication.domain.ExpenseTemplate;
import com.personalapplication.domain.RecurrenceType;
import com.personalapplication.service.ExpenseTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "http://localhost:3000")
public class ExpenseTemplateController {

    @Autowired
    private ExpenseTemplateService expenseTemplateService;

    /**
     * Get all active expense templates (for left panel display)
     * GET /api/templates
     */
    @GetMapping
    public ResponseEntity<List<ExpenseTemplate>> getAllTemplates() {
        List<ExpenseTemplate> templates = expenseTemplateService.getAllActiveTemplates();
        return ResponseEntity.ok(templates);
    }

    /**
     * Get a specific template by ID
     * GET /api/templates/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseTemplate> getTemplate(@PathVariable Long id) {
        ExpenseTemplate template = expenseTemplateService.getTemplate(id);
        return ResponseEntity.ok(template);
    }

    /**
     * Create a new expense template
     * POST /api/templates
     */
    @PostMapping
    public ResponseEntity<ExpenseTemplate> createTemplate(@RequestBody CreateTemplateRequest request) {
        ExpenseTemplate template = new ExpenseTemplate(
                request.getName(),
                request.getAmount(),
                request.getRecurrenceType()
        );

        // Set recurrence-specific fields
        template.setDayOfMonth(request.getDayOfMonth());
        template.setDayOfWeek(request.getDayOfWeek());
        template.setBiWeeklyStartDate(request.getBiWeeklyStartDate());

        ExpenseTemplate created = expenseTemplateService.createTemplate(template);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing template
     * PUT /api/templates/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseTemplate> updateTemplate(
            @PathVariable Long id,
            @RequestBody UpdateTemplateRequest request) {

        ExpenseTemplate updatedTemplate = new ExpenseTemplate(
                request.getName(),
                request.getAmount(),
                request.getRecurrenceType()
        );

        updatedTemplate.setDayOfMonth(request.getDayOfMonth());
        updatedTemplate.setDayOfWeek(request.getDayOfWeek());
        updatedTemplate.setBiWeeklyStartDate(request.getBiWeeklyStartDate());

        ExpenseTemplate updated = expenseTemplateService.updateTemplate(
                id,
                updatedTemplate,
                request.isUpdateFutureOnly()
        );

        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a template
     * DELETE /api/templates/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean deleteFutureInstances) {

        expenseTemplateService.deleteTemplate(id, deleteFutureInstances);
        return ResponseEntity.ok().build();
    }

    /**
     * Check if template has instances in a specific month
     * GET /api/templates/{id}/has-instances/{year}/{month}
     */
    @GetMapping("/{id}/has-instances/{year}/{month}")
    public ResponseEntity<Boolean> hasInstancesInMonth(
            @PathVariable Long id,
            @PathVariable int year,
            @PathVariable int month) {

        boolean hasInstances = expenseTemplateService.hasInstancesInMonth(id, year, month);
        return ResponseEntity.ok(hasInstances);
    }

    // DTO Classes
    public static class CreateTemplateRequest {
        private String name;
        private BigDecimal amount;
        private RecurrenceType recurrenceType;
        private Integer dayOfMonth;
        private Integer dayOfWeek;
        private LocalDate biWeeklyStartDate;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public RecurrenceType getRecurrenceType() { return recurrenceType; }
        public void setRecurrenceType(RecurrenceType recurrenceType) { this.recurrenceType = recurrenceType; }

        public Integer getDayOfMonth() { return dayOfMonth; }
        public void setDayOfMonth(Integer dayOfMonth) { this.dayOfMonth = dayOfMonth; }

        public Integer getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

        public LocalDate getBiWeeklyStartDate() { return biWeeklyStartDate; }
        public void setBiWeeklyStartDate(LocalDate biWeeklyStartDate) { this.biWeeklyStartDate = biWeeklyStartDate; }
    }

    public static class UpdateTemplateRequest {
        private String name;
        private BigDecimal amount;
        private RecurrenceType recurrenceType;
        private Integer dayOfMonth;
        private Integer dayOfWeek;
        private LocalDate biWeeklyStartDate;
        private boolean updateFutureOnly = true; // Default to only future instances

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public RecurrenceType getRecurrenceType() { return recurrenceType; }
        public void setRecurrenceType(RecurrenceType recurrenceType) { this.recurrenceType = recurrenceType; }

        public Integer getDayOfMonth() { return dayOfMonth; }
        public void setDayOfMonth(Integer dayOfMonth) { this.dayOfMonth = dayOfMonth; }

        public Integer getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

        public LocalDate getBiWeeklyStartDate() { return biWeeklyStartDate; }
        public void setBiWeeklyStartDate(LocalDate biWeeklyStartDate) { this.biWeeklyStartDate = biWeeklyStartDate; }

        public boolean isUpdateFutureOnly() { return updateFutureOnly; }
        public void setUpdateFutureOnly(boolean updateFutureOnly) { this.updateFutureOnly = updateFutureOnly; }
    }
}