package com.foodsave.platform.notifications.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.foodsave.platform.notifications.domain.model.NotificationPreferences;
import com.foodsave.platform.notifications.infrastructure.persistence.jpa.NotificationJpaRepository;
import com.foodsave.platform.notifications.infrastructure.persistence.jpa.NotificationPreferencesJpaRepository;
import com.foodsave.platform.iam.infrastructure.persistence.jpa.AccountJpaRepository;
import com.foodsave.platform.shared.interfaces.rest.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationJpaRepository notifications;
    private final NotificationPreferencesJpaRepository preferences;
    private final AccountJpaRepository accounts;
    public NotificationService(NotificationJpaRepository notifications, NotificationPreferencesJpaRepository preferences, AccountJpaRepository accounts) { this.notifications = notifications; this.preferences = preferences; this.accounts = accounts; }
    public List<com.foodsave.platform.notifications.domain.model.Notification> mine(Long accountId) { return notifications.findByRecipientAccountIdOrderByCreatedAtDesc(accountId); }
    @Transactional public com.foodsave.platform.notifications.domain.model.Notification send(Long recipientAccountId, Long reservationId, Long offerId, String type, String titleKey, String body) {
        if (!accounts.existsById(recipientAccountId)) return null;
        return notifications.save(new com.foodsave.platform.notifications.domain.model.Notification(recipientAccountId, reservationId, offerId, null, null, type, titleKey, body));
    }
    public NotificationPreferences getPreferences(Long accountId) { return preferences.findById(accountId).orElseGet(() -> new NotificationPreferences(accountId)); }
    public boolean remindersEnabled(Long accountId) { return getPreferences(accountId).isRemindersEnabled(); }
    public boolean reminderAlreadySent(Long reservationId) { return notifications.existsByReservationIdAndType(reservationId, "PICKUP_REMINDER"); }
    @Transactional public NotificationPreferences updatePreferences(Long accountId, boolean email, boolean push, boolean reminders, boolean nearby) {
        var prefs = preferences.findById(accountId).orElseGet(() -> new NotificationPreferences(accountId));
        prefs.update(email, push, reminders, nearby); return preferences.save(prefs);
    }
    @Transactional public void markRead(Long accountId, Long notificationId) {
        var notification = notifications.findById(notificationId).orElseThrow(() -> new ResourceNotFoundException("notification.not.found"));
        if (!notification.getRecipientAccountId().equals(accountId)) throw new ResourceNotFoundException("notification.not.found");
        notification.markRead();
    }
}
