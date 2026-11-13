package com.foodsave.platform.reservations.application;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PickupReminderScheduler {
    private final ReservationService reservations;
    private final Duration leadTime;
    public PickupReminderScheduler(ReservationService reservations, @Value("${foodsave.notifications.reminder-lead-minutes:60}") long leadMinutes) {
        this.reservations = reservations; this.leadTime = Duration.ofMinutes(leadMinutes);
    }
    @Scheduled(fixedDelayString = "${foodsave.notifications.reminder-check-ms:60000}")
    public void sendDueReminders() { reservations.sendPickupReminders(leadTime); }
}
