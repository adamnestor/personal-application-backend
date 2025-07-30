package com.personalapplication.service;

import com.personalapplication.domain.Account;
import com.personalapplication.domain.User;
import com.personalapplication.repository.AccountRepository;
import com.personalapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get the current authenticated user
     */
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Get the primary checking account for the current user
     * Creates one with zero balance if none exists
     */
    public Account getPrimaryAccount() {
        User currentUser = getCurrentUser();
        List<Account> accounts = accountRepository.findByUserId(currentUser.getId());

        if (accounts.isEmpty()) {
            // Create default account if none exists
            Account defaultAccount = new Account(BigDecimal.ZERO, currentUser);
            return accountRepository.save(defaultAccount);
        }

        return accounts.get(0);
    }

    /**
     * Update the starting balance of the primary account
     */
    public Account updateStartingBalance(BigDecimal newStartingBalance) {
        Account account = getPrimaryAccount();
        account.setStartingBalance(newStartingBalance);
        return accountRepository.save(account);
    }

    /**
     * Get current starting balance
     */
    public BigDecimal getStartingBalance() {
        return getPrimaryAccount().getStartingBalance();
    }

    /**
     * Update account name
     */
    public Account updateAccountName(String newName) {
        Account account = getPrimaryAccount();
        account.setName(newName);
        return accountRepository.save(account);
    }

    /**
     * Initialize account with starting balance (for first-time setup)
     */
    public Account initializeAccount(BigDecimal startingBalance, String accountName) {
        User currentUser = getCurrentUser();

        // Delete any existing accounts for this user (single account system)
        List<Account> existingAccounts = accountRepository.findByUserId(currentUser.getId());
        accountRepository.deleteAll(existingAccounts);

        Account account = new Account(startingBalance, currentUser);
        if (accountName != null && !accountName.trim().isEmpty()) {
            account.setName(accountName);
        }

        return accountRepository.save(account);
    }
}