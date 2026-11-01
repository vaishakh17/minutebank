package com.vaishakh.minutebank;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;

@Service
public class TimeEntryService {

    private static final int DAILY_MINUTES = 1440;
    private final TimeEntryRepository repository;

    public TimeEntryService(TimeEntryRepository repository) 
    {
        this.repository = repository;
    }

    public TimeEntry add(TimeEntryRequest req, String username) {
        TimeEntry entry = new TimeEntry();
        entry.setTitle(req.title().trim());
        entry.setCategory(req.category());
        entry.setMinutes(req.minutes());
        entry.setEntryDate(req.entryDate());
        entry.setOwner(username);
        return repository.save(entry);
    }

    public List<TimeEntry> getByDate(LocalDate date, String username) {
        return repository.findByOwnerAndEntryDate(username, date);
    }

    public Map<String, Object> summary(LocalDate date, String username) {
        int spent = repository.findByOwnerAndEntryDate(username, date)
                .stream()
                .mapToInt(TimeEntry::getMinutes)
                .sum();
        int balance = DAILY_MINUTES - spent;
        return Map.of("date", date, "spent", spent, "balance", balance, "overdraft", balance < 0);
    }
    public WeeklyReport weekly(LocalDate anyDayInWeek, String username) {
        LocalDate start = anyDayInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = start.plusDays(6);

        List<TimeEntry> entries = repository.findByOwnerAndEntryDateBetween(username, start, end);

        Map<LocalDate, Integer> perDay = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            perDay.put(start.plusDays(i), 0);
        }

        Map<String, Integer> perCategory = new LinkedHashMap<>();
        for (TimeEntry e : entries) {
            perDay.merge(e.getEntryDate(), e.getMinutes(), Integer::sum);
            perCategory.merge(e.getCategory(), e.getMinutes(), Integer::sum);
        }

        int total = perDay.values().stream().mapToInt(Integer::intValue).sum();
        int overdraftDays = (int) perDay.values().stream().filter(m -> m > DAILY_MINUTES).count();
        String top = perCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");

        return new WeeklyReport(start, end, total, total / 7, top, overdraftDays, perDay, perCategory);
    }
}