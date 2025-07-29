package com.personalapplication.repository;

import com.personalapplication.domain.ExpenseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseTemplateRepository extends JpaRepository<ExpenseTemplate, Long> {
    List<ExpenseTemplate> findByUserIdAndActiveTrue(Long userId);
    List<ExpenseTemplate> findByUserId(Long userId);
    Optional<ExpenseTemplate> findByUserIdAndId(Long userId, Long templateId);
}