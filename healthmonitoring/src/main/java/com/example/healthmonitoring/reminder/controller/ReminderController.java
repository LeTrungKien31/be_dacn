package com.example.healthmonitoring.reminder.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.healthmonitoring.reminder.entity.Reminder;
import com.example.healthmonitoring.reminder.repo.ReminderRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderRepository repo;

    private String getUserId(Authentication auth) {
        return auth.getName();
    }

    @GetMapping
    public List<Reminder> getAll(Authentication auth) {
        return repo.findByUserIdOrderByTimeAsc(getUserId(auth));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reminder create(@Valid @RequestBody CreateReq req, Authentication auth) {
        Reminder r = new Reminder();
        r.setUserId(getUserId(auth));
        r.setTime(req.getTime());
        r.setDaysOfWeek(req.getDaysOfWeek());
        r.setEnabled(true);
        return repo.save(r);
    }

    @PatchMapping("/{id}/toggle")
    public Reminder toggle(@PathVariable Long id, Authentication auth) {
        Reminder r = repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        if (!r.getUserId().equals(getUserId(auth))) {
            throw new RuntimeException("Forbidden");
        }
        r.setEnabled(!r.isEnabled());
        return repo.save(r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        Reminder r = repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        if (!r.getUserId().equals(getUserId(auth))) {
            throw new RuntimeException("Forbidden");
        }
        repo.deleteById(id);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReq {
        @NotBlank(message = "Time is required (HH:mm format)")
        private String time; // "07:20"
        
        private String daysOfWeek; // "1,2,3,4,5,6,7" hoặc "2,4,6"
    }
}