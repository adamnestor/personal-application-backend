package com.personalapplication.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "income_year", nullable = false)
    private int yearValue;

    @Column(name = "income_month", nullable = false)
    private int monthValue; // 1-12

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Could add IncomeTemplate later for recurring income

    // Constructors
    public ScheduledIncome() {}

    public ScheduledIncome(String name, BigDecimal amount, LocalDate scheduledDate) {
        this.name = name;
        this.amount = amount;
        this.scheduledDate = scheduledDate;
        this.yearValue = scheduledDate.getYear();
        this.monthValue = scheduledDate.getMonthValue();
    }

    public ScheduledIncome(String name, BigDecimal amount, LocalDate scheduledDate, User user) {
        this.name = name;
        this.amount = amount;
        this.scheduledDate = scheduledDate;
        this.yearValue = scheduledDate.getYear();
        this.monthValue = scheduledDate.getMonthValue();
        this.user = user;
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
        this.yearValue = scheduledDate.getYear();
        this.monthValue = scheduledDate.getMonthValue();
    }

    public int getYearValue() { return yearValue; }
    public void setYearValue(int yearValue) { this.yearValue = yearValue; }

    public int getMonthValue() { return monthValue; }
    public void setMonthValue(int monthValue) { this.monthValue = monthValue; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}