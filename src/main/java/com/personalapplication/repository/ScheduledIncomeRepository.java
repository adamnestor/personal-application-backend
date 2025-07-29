package com.personalapplication.repository;

import com.personalapplication.domain.ScheduledIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduledIncomeRepository extends JpaRepository<ScheduledIncome, Long> {
    List<ScheduledIncome> findByYearAndMonthValueOrderByScheduledDate(int year, int month);
}