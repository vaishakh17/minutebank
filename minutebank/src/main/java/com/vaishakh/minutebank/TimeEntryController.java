package com.vaishakh.minutebank;

import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import java.security.Principal;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TimeEntryController {

    private final TimeEntryService service;

    public TimeEntryController(TimeEntryService service) 
    {
        this.service = service;
    }
    @PostMapping("/entries")
    public TimeEntry add(@Valid @RequestBody TimeEntryRequest req, Principal principal) {
        return service.add(req, principal.getName());
    }


@GetMapping("/entries")
public List<TimeEntry> list(@RequestParam LocalDate date, Principal principal) {
    return service.getByDate(date, principal.getName());
}
@GetMapping("/summary")
public Map<String, Object> summary(@RequestParam LocalDate date, Principal principal) {
    return service.summary(date, principal.getName());
}
@GetMapping("/weekly")
public WeeklyReport weekly(@RequestParam LocalDate date, Principal principal) {
    return service.weekly(date, principal.getName());
}
}