package com.foodsave.platform.iam.infrastructure.persistence.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.foodsave.platform.iam.domain.model.Account;

public interface AccountJpaRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
