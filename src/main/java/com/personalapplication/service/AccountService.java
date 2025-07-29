package com.personalapplication.service;

import com.personalapplication.domain.Account;
import com.personalapplication.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Get the primary checking account
     * Creates one with zero balance if none exists
     */
    public Account getPrimaryAccount() {
        List<Account> accounts = accountRepository.findAll();

        if (accounts.isEmpty()) {
            // Create default account if none exists
            Account defaultAccount = new Account(BigDecimal.ZERO);
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
        // Delete any existing accounts (single account system)
        accountRepository.deleteAll();

        Account account = new Account(startingBalance);
        if (accountName != null && !accountName.trim().isEmpty()) {
            account.setName(accountName);
        }

        return accountRepository.save(account);
    }
}