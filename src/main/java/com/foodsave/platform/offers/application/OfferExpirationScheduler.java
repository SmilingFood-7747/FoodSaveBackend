package com.foodsave.platform.offers.application;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OfferExpirationScheduler {
    private final OfferManagementService catalog;
    public OfferExpirationScheduler(OfferManagementService catalog) { this.catalog = catalog; }
    @Scheduled(fixedDelayString = "${foodsave.offers.expiration-check-ms:60000}")
    public void expireDueOffers() { catalog.expireDueOffers(); }
}
