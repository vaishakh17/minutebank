package com.vaishakh.minutebank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeEntryServiceTest {

    @Mock
    TimeEntryRepository repository;

    @InjectMocks
    TimeEntryService service;

    private TimeEntry entry(String category, int minutes, LocalDate date) {
        TimeEntry e = new TimeEntry();
        e.setTitle("test");
        e.setCategory(category);
        e.setMinutes(minutes);
        e.setEntryDate(date);
        e.setOwner("vaishakh");
        return e;
    }

    @Test
    void summary_showsOverdraft_whenMoreThan1440Minutes() {
        LocalDate day = LocalDate.of(2026, 11, 2);
        when(repository.findByOwnerAndEntryDate("vaishakh", day))
                .thenReturn(List.of(entry("Study", 900, day), entry("Phone", 700, day)));

        Map<String, Object> result = service.summary(day, "vaishakh");

        assertEquals(1600, result.get("spent"));
        assertEquals(-160, result.get("balance"));
        assertEquals(Boolean.TRUE, result.get("overdraft"));
    }

    @Test
    void weekly_countsOverdraftDays_andFindsTopCategory() {
        LocalDate monday = LocalDate.of(2026, 11, 2);
        LocalDate tuesday = monday.plusDays(1);
        when(repository.findByOwnerAndEntryDateBetween("vaishakh", monday, monday.plusDays(6)))
                .thenReturn(List.of(entry("Study", 1500, monday), entry("Sleep", 100, tuesday)));

        WeeklyReport report = service.weekly(LocalDate.of(2026, 11, 4), "vaishakh");

        assertEquals(monday, report.weekStart());
        assertEquals(1600, report.totalSpent());
        assertEquals(1, report.overdraftDays());
        assertEquals("Study", report.topCategory());
        assertEquals(7, report.perDay().size());
    }

    @Test
    void add_alwaysCreatesNewEntry_ownedByLoggedInUser() {
        when(repository.save(any(TimeEntry.class))).thenAnswer(inv -> inv.getArgument(0));
        TimeEntryRequest req = new TimeEntryRequest("  Java  ", "Study", 60, LocalDate.of(2026, 11, 2));

        service.add(req, "vaishakh");

        ArgumentCaptor<TimeEntry> captor = ArgumentCaptor.forClass(TimeEntry.class);
        verify(repository).save(captor.capture());
        assertEquals("vaishakh", captor.getValue().getOwner());
        assertEquals("Java", captor.getValue().getTitle());
        assertNull(captor.getValue().getId());
    }
}