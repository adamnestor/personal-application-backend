package com.personalapplication.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expense_templates")
public class ExpenseTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

    // For monthly: day of month (1-31)
    // For weekly: day of week (1-7, Monday=1)
    // For bi-weekly: starting date
    private Integer dayOfMonth;
    private Integer dayOfWeek;
    private LocalDate biWeeklyStartDate;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public ExpenseTemplate() {}

    public ExpenseTemplate(String name, BigDecimal amount, RecurrenceType recurrenceType) {
        this.name = name;
        this.amount = amount;
        this.recurrenceType = recurrenceType;
    }

    public ExpenseTemplate(String name, BigDecimal amount, RecurrenceType recurrenceType, User user) {
        this.name = name;
        this.amount = amount;
        this.recurrenceType = recurrenceType;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}