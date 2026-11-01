package com.vaishakh.minutebank;

import java.time.LocalDate;
import java.util.Map;

public record WeeklyReport(
        LocalDate weekStart,
        LocalDate weekEnd,
        int totalSpent,
        int averagePerDay,
        String topCategory,
        int overdraftDays,
        Map<LocalDate, Integer> perDay,
        Map<String, Integer> perCategory) {
}