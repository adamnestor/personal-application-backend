package com.personalapplication.service;

import com.personalapplication.domain.*;
import com.personalapplication.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class BudgetService {

    @Autowired
    private ScheduledExpenseRepository scheduledExpenseRepository;

    @Autowired
    private ScheduledIncomeRepository scheduledIncomeRepository;

    @Autowired
    private ExpenseTemplateRepository expenseTemplateRepository;

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
     * Generate scheduled expenses for a given month from active templates for the current user
     */
    public void generateMonthlyExpenses(int year, int month) {
        User currentUser = getCurrentUser();
        List<ExpenseTemplate> activeTemplates = expenseTemplateRepository.findByUserIdAndActiveTrue(currentUser.getId());
        YearMonth yearMonth = YearMonth.of(year, month);

        for (ExpenseTemplate template : activeTemplates) {
            List<LocalDate> dates = calculateDatesForMonth(template, yearMonth);

            for (LocalDate date : dates) {
                // Check if expense already exists for this template and date
                boolean exists = scheduledExpenseRepository.existsByUserIdAndTemplateAndScheduledDate(
                        currentUser.getId(), template, date);
                if (!exists) {
                    ScheduledExpense expense = new ScheduledExpense(
                            template.getName(),
                            template.getAmount(),
                            date,
                            currentUser
                    );
                    expense.setTemplate(template);
                    scheduledExpenseRepository.save(expense);
                }
            }
        }
    }

    /**
     * Calculate which dates a template should occur in a given month
     */
    private List<LocalDate> calculateDatesForMonth(ExpenseTemplate template, YearMonth yearMonth) {
        List<LocalDate> dates = new ArrayList<>();

        switch (template.getRecurrenceType()) {
            case MONTHLY:
                if (template.getDayOfMonth() != null) {
                    try {
                        LocalDate date = yearMonth.atDay(template.getDayOfMonth());
                        dates.add(date);
                    } catch (Exception e) {
                        // Handle invalid dates like Feb 31st - use last day of month
                        dates.add(yearMonth.atEndOfMonth());
                    }
                }
                break;

            case WEEKLY:
                if (template.getDayOfWeek() != null) {
                    LocalDate start = yearMonth.atDay(1);
                    LocalDate end = yearMonth.atEndOfMonth();

                    // Find first occurrence of the day in the month
                    LocalDate current = start;
                    while (current.getDayOfWeek().getValue() != template.getDayOfWeek()) {
                        current = current.plusDays(1);
                        if (current.isAfter(end)) break;
                    }

                    // Add all occurrences in the month
                    while (!current.isAfter(end)) {
                        dates.add(current);
                        current = current.plusWeeks(1);
                    }
                }
                break;

            case BI_WEEKLY:
                if (template.getBiWeeklyStartDate() != null) {
                    LocalDate start = yearMonth.atDay(1);
                    LocalDate end = yearMonth.atEndOfMonth();
                    LocalDate current = template.getBiWeeklyStartDate();

                    // Find dates that fall within this month
                    while (current.isBefore(start)) {
                        current = current.plusWeeks(2);
                    }

                    while (!current.isAfter(end)) {
                        dates.add(current);
                        current = current.plusWeeks(2);
                    }
                }
                break;

            case ONE_TIME:
                // One-time expenses don't auto-generate
                break;
        }

        return dates;
    }

    /**
     * Get all scheduled expenses for a month for the current user
     */
    public List<ScheduledExpense> getMonthlyExpenses(int year, int month) {
        User currentUser = getCurrentUser();
        return scheduledExpenseRepository.findByUserIdAndYearValueAndMonthValueOrderByScheduledDate(
                currentUser.getId(), year, month);
    }

    /**
     * Get all scheduled income for a month for the current user
     */
    public List<ScheduledIncome> getMonthlyIncome(int year, int month) {
        User currentUser = getCurrentUser();
        return scheduledIncomeRepository.findByUserIdAndYearValueAndMonthValueOrderByScheduledDate(
                currentUser.getId(), year, month);
    }

    /**
     * Calculate daily running balances for a month for the current user
     * Starting balance = ending balance of previous month
     */
    public Map<LocalDate, BigDecimal> calculateDailyBalances(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Get starting balance for this month
        BigDecimal runningBalance = getStartingBalanceForMonth(year, month);

        // Get all transactions for the month
        List<ScheduledExpense> expenses = getMonthlyExpenses(year, month);
        List<ScheduledIncome> income = getMonthlyIncome(year, month);

        Map<LocalDate, BigDecimal> dailyBalances = new LinkedHashMap<>();

        // Calculate day by day
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            // Add income for this date
            BigDecimal dailyIncome = income.stream()
                    .filter(i -> i.getScheduledDate().equals(currentDate))
                    .map(ScheduledIncome::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Subtract expenses for this date
            BigDecimal dailyExpenses = expenses.stream()
                    .filter(e -> e.getScheduledDate().equals(currentDate))
                    .map(ScheduledExpense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            runningBalance = runningBalance.add(dailyIncome).subtract(dailyExpenses);
            dailyBalances.put(currentDate, runningBalance);
        }

        return dailyBalances;
    }

    /**
     * Get the starting balance for a given month for the current user
     * For the first month ever, use account starting balance
     * Otherwise, use the ending balance of the previous month
     */
    private BigDecimal getStartingBalanceForMonth(int year, int month) {
        User currentUser = getCurrentUser();

        // Get the account for current user (assuming single account for now)
        List<Account> accounts = accountRepository.findByUserId(currentUser.getId());
        Account account = accounts.stream().findFirst()
                .orElse(new Account(BigDecimal.ZERO, currentUser));

        // If this is the first month we have data for, use account starting balance
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        LocalDate previousMonth = firstOfMonth.minusMonths(1);

        // Check if we have any data before this month for this user
        boolean hasDataBefore = scheduledExpenseRepository.findByUserIdAndYearValueAndMonthValueOrderByScheduledDate(
                currentUser.getId(), previousMonth.getYear(), previousMonth.getMonthValue()).size() > 0 ||
                scheduledIncomeRepository.findByUserIdAndYearValueAndMonthValueOrderByScheduledDate(
                        currentUser.getId(), previousMonth.getYear(), previousMonth.getMonthValue()).size() > 0;

        if (!hasDataBefore) {
            return account.getStartingBalance();
        }

        // Calculate previous month's ending balance
        Map<LocalDate, BigDecimal> previousBalances = calculateDailyBalances(
                previousMonth.getYear(),
                previousMonth.getMonthValue()
        );

        if (previousBalances.isEmpty()) {
            return account.getStartingBalance();
        }

        // Return the last day's balance from previous month
        LocalDate lastDayOfPreviousMonth = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());
        return previousBalances.get(lastDayOfPreviousMonth);
    }

    /**
     * Move a scheduled expense to a different date (drag and drop on calendar)
     */
    public ScheduledExpense moveExpense(Long expenseId, LocalDate newDate) {
        User currentUser = getCurrentUser();
        ScheduledExpense expense = scheduledExpenseRepository.findByUserIdAndId(currentUser.getId(), expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setScheduledDate(newDate);
        return scheduledExpenseRepository.save(expense);
    }

    /**
     * Move a scheduled income to a different date
     */
    public ScheduledIncome moveIncome(Long incomeId, LocalDate newDate) {
        User currentUser = getCurrentUser();
        ScheduledIncome income = scheduledIncomeRepository.findByUserIdAndId(currentUser.getId(), incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        income.setScheduledDate(newDate);
        return scheduledIncomeRepository.save(income);
    }

    /**
     * Create a new expense instance from a template (drag from left panel)
     */
    public ScheduledExpense createExpenseFromTemplate(Long templateId, LocalDate scheduledDate) {
        User currentUser = getCurrentUser();
        ExpenseTemplate template = expenseTemplateRepository.findByUserIdAndId(currentUser.getId(), templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        ScheduledExpense expense = new ScheduledExpense(
                template.getName(),
                template.getAmount(),
                scheduledDate,
                currentUser
        );
        expense.setTemplate(template);

        return scheduledExpenseRepository.save(expense);
    }
}