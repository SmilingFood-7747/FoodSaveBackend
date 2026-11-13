package com.foodsave.platform.businesses.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.foodsave.platform.businesses.domain.model.Business;
import com.foodsave.platform.businesses.domain.repositories.BusinessRepository;
import com.foodsave.platform.shared.interfaces.rest.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
public class BusinessManagementService {
    private final BusinessRepository businesses;
    public BusinessManagementService(BusinessRepository businesses) { this.businesses = businesses; }
    public List<Business> businesses() { return businesses.findAll(); }
    public List<Business> businessesOwnedBy(Long accountId) { return businesses.findByOwner(accountId); }
    public Business business(Long id) { return businesses.findById(id).orElseThrow(() -> new ResourceNotFoundException("business.not.found")); }
    public void requireOwner(Long businessId, Long accountId) {
        if (!business(businessId).getOwnerAccountId().equals(accountId)) throw new ResourceNotFoundException("business.not.found");
    }
    @Transactional public Business registerBusiness(Long ownerAccountId, Business business) { return businesses.save(business); }
    @Transactional public Business updateBusiness(Long id, Long ownerAccountId, String name, String address, String district,
                                                  double latitude, double longitude, String phone, String pickupConditions) {
        Business current = business(id);
        requireOwner(id, ownerAccountId);
        current.updateProfile(name, address, district, latitude, longitude, phone, pickupConditions);
        return businesses.save(current);
    }
}
