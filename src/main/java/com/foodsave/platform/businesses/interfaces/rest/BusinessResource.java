package com.foodsave.platform.businesses.interfaces.rest;

import com.foodsave.platform.businesses.domain.model.Business;

public record BusinessResource(Long id, Long ownerAccountId, String name, String address,
                               String district, double latitude, double longitude,
                               String contactPhone, String pickupConditions,
                               boolean isActive, boolean plusPartner) {
    public static BusinessResource from(Business business) {
        return new BusinessResource(business.getId(), business.getOwnerAccountId(), business.getName(),
                business.getAddress(), business.getDistrict(), business.getLatitude(), business.getLongitude(),
                business.getContactPhone(), business.getPickupConditions(), business.isActive(), business.isPlusPartner());
    }
}
