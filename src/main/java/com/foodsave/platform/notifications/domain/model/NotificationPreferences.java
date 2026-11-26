package com.foodsave.platform.notifications.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {
    @Id @Column(name = "account_id") private Long accountId;
    @Column(name = "email_enabled", nullable = false) private boolean emailEnabled = true;
    @Column(name = "push_enabled", nullable = false) private boolean pushEnabled = true;
    @Column(name = "reminders_enabled", nullable = false) private boolean remindersEnabled = true;
    @Column(name = "nearby_offers_enabled", nullable = false) private boolean nearbyOffersEnabled = true;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "notification_preferred_categories", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "category", nullable = false, length = 40)
    private Set<String> preferredCategories = new LinkedHashSet<>();
    protected NotificationPreferences() {}
    public NotificationPreferences(Long accountId) { this.accountId = accountId; }
    public void update(boolean email, boolean push, boolean reminders, boolean nearby) {
        emailEnabled = email; pushEnabled = push; remindersEnabled = reminders; nearbyOffersEnabled = nearby;
    }
    public void updatePreferredCategories(Set<String> categories) { preferredCategories = new LinkedHashSet<>(categories); }
    public Long getAccountId() { return accountId; } public boolean isEmailEnabled() { return emailEnabled; }
    public boolean isPushEnabled() { return pushEnabled; } public boolean isRemindersEnabled() { return remindersEnabled; }
    public boolean isNearbyOffersEnabled() { return nearbyOffersEnabled; }
    public Set<String> getPreferredCategories() { return Set.copyOf(preferredCategories); }
}
