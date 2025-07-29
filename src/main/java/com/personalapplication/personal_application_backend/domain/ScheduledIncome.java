package com.personalapplication.personal_application_backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "scheduled_income")
public class ScheduledIncome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month; // 1-12

    // Could add IncomeTemplate later for recurring income

    // Constructors
    public ScheduledIncome() {}

    public ScheduledIncome(String name, BigDecimal amount, LocalDate scheduledDate) {
        this.name = name;
        this.amount = amount;
        this.scheduledDate = scheduledDate;
        this.year = scheduledDate.getYear();
        this.month = scheduledDate.getMonthValue();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
        this.year = scheduledDate.getYear();
        this.month = scheduledDate.getMonthValue();
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
}