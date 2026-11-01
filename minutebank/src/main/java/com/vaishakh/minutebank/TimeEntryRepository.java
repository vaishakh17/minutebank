package com.vaishakh.minutebank;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
	List<TimeEntry> findByOwnerAndEntryDate(String owner, LocalDate entryDate);
	List<TimeEntry> findByOwnerAndEntryDateBetween(String owner, LocalDate start, LocalDate end);
	}