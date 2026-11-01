package com.vaishakh.minutebank;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

@Entity
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be 100 characters or less")
    private String title;
    
    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must be 50 characters or less")
    private String category;
    
    
    @Min(value = 1, message = "Minutes must be at least 1")
    @Max(value = 1440, message = "One entry cannot exceed 1440 minutes")
    private int minutes;
    
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Entry date cannot be in the future")
    private LocalDate entryDate;
    
    @JsonIgnore
    private String owner;
    
    
    public String getOwner() 
    { 
    	return owner; 
    }
    public void setOwner(String owner) 
    { 
    	this.owner = owner; 
    	}

    public Long getId() 
    { 
    	return id;
    	}
    public String getTitle() 
    { 
    	return title; 
    	}
    public void setTitle(String title) 
    { 
    	this.title = title; 
    	}
    public String getCategory() 
    { 
    	return category; 
    	}
    public void setCategory(String category) 
    { 
    	this.category = category; 
    }
    public int getMinutes() 
    { 
    	return minutes; 
    	}
    public void setMinutes(int minutes) 
    { 
    	this.minutes = minutes; 
    	}
    public LocalDate getEntryDate() 
    { 
    	return entryDate; 
    }
    public void setEntryDate(LocalDate entryDate) 
    { 
    	this.entryDate = entryDate; 
    	}
}
