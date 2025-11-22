package com.example.healthmonitoring.reminder.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.healthmonitoring.reminder.entity.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserIdOrderByTimeAsc(String userId);
}