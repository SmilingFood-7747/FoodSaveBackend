package com.foodsave.platform.offers.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.foodsave.platform.offers.domain.model.Offer;
import com.foodsave.platform.offers.domain.repositories.OfferRepository;
import com.foodsave.platform.shared.interfaces.rest.ResourceNotFoundException;
import com.foodsave.platform.notifications.application.NotificationService;
import com.foodsave.platform.reservations.infrastructure.persistence.jpa.ReservationJpaRepository;
import com.foodsave.platform.businesses.application.BusinessManagementService;

@Service
@Transactional(readOnly = true)
public class OfferManagementService {
    private final BusinessManagementService businesses;
    private final OfferRepository offers;
    private final ReservationJpaRepository reservations;
    private final NotificationService notifications;

    public OfferManagementService(BusinessManagementService businesses, OfferRepository offers, ReservationJpaRepository reservations, NotificationService notifications) {
        this.businesses = businesses;
        this.offers = offers;
        this.reservations = reservations;
        this.notifications = notifications;
    }

    public List<Offer> offers() { return offers.findActiveAt(java.time.Instant.now()); }
    public List<Offer> offersForBusiness(Long businessId) { return offers.findByBusinessId(businessId); }
    public Offer offer(Long id) { return offers.findById(id).orElseThrow(() -> new ResourceNotFoundException("offer.not.found")); }

    public void requireOwner(Long businessId, Long accountId) { businesses.requireOwner(businessId, accountId); }

    @Transactional
    public Offer createOffer(Offer offer) {
        businesses.business(offer.getBusinessId());
        return offers.save(offer);
    }

    @Transactional
    public Offer updateOffer(Long id, Offer replacement) {
        var current = offer(id);
        businesses.business(replacement.getBusinessId());
        var affected = reservations.findByOfferIdAndStatus(id, "ACTIVE");
        boolean contractChanged = !current.getTitle().equals(replacement.getTitle())
                || !current.getDescription().equals(replacement.getDescription())
                || current.getOfferPrice().compareTo(replacement.getOfferPrice()) != 0
                || !current.getPickupStartAt().equals(replacement.getPickupStartAt())
                || !current.getPickupEndAt().equals(replacement.getPickupEndAt());
        current.replaceWith(replacement);
        Offer saved = offers.save(current);
        if (contractChanged) notifyAffected(affected, "OFFER_CHANGED", "notifications.offer.changed", "An offer you reserved has been updated.");
        return saved;
    }

    @Transactional
    public void deleteOffer(Long id) {
        if (!reservations.findByOfferIdAndStatus(id, "ACTIVE").isEmpty()) throw new IllegalArgumentException("offer.has.reservations");
        offer(id);
        offers.deleteById(id);
    }

    @Transactional
    public Offer pauseOffer(Long id, Long accountId) {
        Offer offer = offer(id);
        requireOwner(offer.getBusinessId(), accountId);
        offer.pause();
        Offer paused = offers.save(offer);
        notifyAffected(reservations.findByOfferIdAndStatus(id, "ACTIVE"), "OFFER_CHANGED", "notifications.offer.paused", "An offer you reserved has been paused. Contact the business for details.");
        return paused;
    }

    public com.foodsave.platform.businesses.domain.model.Business business(Long id) { return businesses.business(id); }

    @Transactional
    public int expireDueOffers() {
        var due = offers.findExpiredAt(java.time.Instant.now());
        due.forEach(Offer::expire);
        return due.size();
    }

    private void notifyAffected(java.util.List<com.foodsave.platform.reservations.domain.model.Reservation> affected,
                                String type, String titleKey, String body) {
        for (var reservation : affected) notifications.send(reservation.getCustomerUserId(), reservation.getId(), reservation.getOfferId(), type, titleKey, body);
    }

    @Transactional
    public Offer reserveOffer(Long id, int quantity) {
        var offer = offers.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("offer.not.found"));
        offer.reserveUnits(quantity);
        return offers.save(offer);
    }

    @Transactional
    public Offer releaseOffer(Long id, int quantity) {
        var offer = offers.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("offer.not.found"));
        offer.releaseUnits(quantity);
        return offers.save(offer);
    }
}
