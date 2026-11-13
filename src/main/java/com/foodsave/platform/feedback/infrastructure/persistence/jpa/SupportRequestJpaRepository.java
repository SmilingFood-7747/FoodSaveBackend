package com.foodsave.platform.feedback.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.foodsave.platform.feedback.domain.model.SupportRequest;

public interface SupportRequestJpaRepository extends JpaRepository<SupportRequest, Long> {
    List<SupportRequest> findByRequesterAccountIdOrderByCreatedAtDesc(Long accountId);
}
