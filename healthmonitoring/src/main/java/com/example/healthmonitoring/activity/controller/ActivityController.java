package com.example.healthmonitoring.activity.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.healthmonitoring.activity.entity.ActivityLog;
import com.example.healthmonitoring.activity.repo.ActivityLogRepository;
import com.example.healthmonitoring.activity.service.ActivityService;

@RestController
@RequestMapping("/api/v1/activity")
public class ActivityController {

    public static class CreateReq {
        public String name;
        public double met;
        public int minutes;
        public double weightKg;
    }

    public static class TodayOutRes {
        public int totalKcal;
        public TodayOutRes(int v) {
            totalKcal = v;
        }
    }

    private final ActivityLogRepository repo;
    private final ActivityService svc;

    public ActivityController(ActivityLogRepository repo, ActivityService svc) {
        this.repo = repo;
        this.svc = svc;
    }

    private String uid(Authentication a) {
        return a.getName();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityLog add(@RequestBody CreateReq req, Authentication a) {
        // Validate input
        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (req.name == null || req.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity name is required");
        }
        if (req.met <= 0) {
            throw new IllegalArgumentException("MET value must be positive");
        }
        if (req.minutes <= 0) {
            throw new IllegalArgumentException("Minutes must be positive");
        }
        if (req.weightKg <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }

        var log = new ActivityLog();
        log.setUserId(uid(a));
        log.setName(req.name);
        log.setMet(req.met);
        log.setMinutes(req.minutes);
        log.setWeightKgAtTime(req.weightKg);
        log.setTotalKcal(svc.calcKcal(req.met, req.weightKg, req.minutes));
        return repo.save(log);
    }

    @GetMapping("/today/total")
    public TodayOutRes todayTotal(Authentication a) {
        var d = LocalDate.now();
        var s = d.atStartOfDay();
        var e = d.plusDays(1).atStartOfDay().minusNanos(1);
        int total = repo.sumKcalByUserAndRange(uid(a), s, e);
        return new TodayOutRes(total);
    }

    @GetMapping("/history")
    public List<ActivityLog> history(
            Authentication a,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var s = from.atStartOfDay();
        var e = to.plusDays(1).atStartOfDay().minusNanos(1);
        return repo.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(uid(a), s, e);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication a) {
        var m = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity log not found"));
        if (!m.getUserId().equals(uid(a))) {
            throw new RuntimeException("Forbidden");
        }
        repo.deleteById(id);
    }
}