package com.foodsave.platform.businesses.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.foodsave.platform.businesses.domain.model.Business;
import com.foodsave.platform.businesses.domain.repositories.BusinessRepository;

@Repository
public class BusinessRepositoryAdapter implements BusinessRepository {
    private final BusinessJpaRepository repository;

    public BusinessRepositoryAdapter(BusinessJpaRepository repository) { this.repository = repository; }
    public List<Business> findAll() { return repository.findAll(); }
    public Optional<Business> findById(Long id) { return repository.findById(id); }
    public List<Business> findByOwner(Long accountId) { return repository.findByOwnerAccountId(accountId); }
    public Business save(Business business) { return repository.save(business); }
}
