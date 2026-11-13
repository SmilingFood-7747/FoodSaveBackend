package com.foodsave.platform.notifications.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import com.foodsave.platform.notifications.domain.model.NotificationPreferences;

public interface NotificationPreferencesJpaRepository extends JpaRepository<NotificationPreferences, Long> {}
