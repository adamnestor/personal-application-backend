package com.personalapplication.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name = "Checking Account";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal startingBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Account() {}

    public Account(BigDecimal startingBalance) {
        this.startingBalance = startingBalance;
    }

    public Account(BigDecimal startingBalance, User user) {
        this.startingBalance = startingBalance;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getStartingBalance() { return startingBalance; }
    public void setStartingBalance(BigDecimal startingBalance) { this.startingBalance = startingBalance; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}