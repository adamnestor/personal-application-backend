package com.personalapplication.controller;

import com.personalapplication.domain.Account;
import com.personalapplication.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * Get the primary account information
     * GET /api/account
     */
    @GetMapping
    public ResponseEntity<Account> getPrimaryAccount() {
        Account account = accountService.getPrimaryAccount();
        return ResponseEntity.ok(account);
    }

    /**
     * Get just the starting balance
     * GET /api/account/starting-balance
     */
    @GetMapping("/starting-balance")
    public ResponseEntity<BigDecimal> getStartingBalance() {
        BigDecimal balance = accountService.getStartingBalance();
        return ResponseEntity.ok(balance);
    }

    /**
     * Update the starting balance
     * PUT /api/account/starting-balance
     */
    @PutMapping("/starting-balance")
    public ResponseEntity<Account> updateStartingBalance(@RequestBody UpdateStartingBalanceRequest request) {
        Account updated = accountService.updateStartingBalance(request.getStartingBalance());
        return ResponseEntity.ok(updated);
    }

    /**
     * Update the account name
     * PUT /api/account/name
     */
    @PutMapping("/name")
    public ResponseEntity<Account> updateAccountName(@RequestBody UpdateAccountNameRequest request) {
        Account updated = accountService.updateAccountName(request.getName());
        return ResponseEntity.ok(updated);
    }

    /**
     * Initialize account (first-time setup)
     * POST /api/account/initialize
     */
    @PostMapping("/initialize")
    public ResponseEntity<Account> initializeAccount(@RequestBody InitializeAccountRequest request) {
        Account account = accountService.initializeAccount(
                request.getStartingBalance(),
                request.getAccountName()
        );
        return ResponseEntity.ok(account);
    }

    // DTO Classes
    public static class UpdateStartingBalanceRequest {
        private BigDecimal startingBalance;

        public BigDecimal getStartingBalance() { return startingBalance; }
        public void setStartingBalance(BigDecimal startingBalance) { this.startingBalance = startingBalance; }
    }

    public static class UpdateAccountNameRequest {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class InitializeAccountRequest {
        private BigDecimal startingBalance;
        private String accountName;

        public BigDecimal getStartingBalance() { return startingBalance; }
        public void setStartingBalance(BigDecimal startingBalance) { this.startingBalance = startingBalance; }

        public String getAccountName() { return accountName; }
        public void setAccountName(String accountName) { this.accountName = accountName; }
    }
}