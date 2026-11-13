package com.foodsave.platform.notifications.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.foodsave.platform.notifications.domain.model.Notification;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientAccountIdOrderByCreatedAtDesc(Long accountId);
    boolean existsByReservationIdAndType(Long reservationId, String type);
}
