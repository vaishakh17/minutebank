package com.vaishakh.minutebank;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record TimeEntryRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must be 100 characters or less")
        String title,

        @NotBlank(message = "Category is required")
        String category,

        @Min(value = 1, message = "Minutes must be at least 1")
        @Max(value = 1440, message = "One entry cannot exceed 1440 minutes")
        int minutes,

        @NotNull(message = "Date is required")
        LocalDate entryDate) {
}