package com.foodsave.platform.reservations.application;

import java.security.SecureRandom;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.foodsave.platform.offers.application.OfferManagementService;
import com.foodsave.platform.reservations.domain.model.Reservation;
import com.foodsave.platform.reservations.domain.repositories.ReservationRepository;
import com.foodsave.platform.shared.interfaces.rest.ResourceNotFoundException;
import com.foodsave.platform.notifications.application.NotificationService;

@Service
@Transactional(readOnly = true)
public class ReservationService {
    private static final char[] CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private final SecureRandom random = new SecureRandom();
    private final OfferManagementService catalog;
    private final ReservationRepository reservations;
    private final NotificationService notifications;

    public ReservationService(OfferManagementService catalog, ReservationRepository reservations, NotificationService notifications) {
        this.catalog = catalog;
        this.reservations = reservations;
        this.notifications = notifications;
    }

    public List<Reservation> all() { return reservations.findAll(); }
    public List<Reservation> customerReservations(Long customerId) { return reservations.findByCustomer(customerId); }
    public List<Reservation> businessReservations(Long businessId, Long accountId) {
        catalog.requireOwner(businessId, accountId);
        return reservations.findByBusiness(businessId);
    }
    @Transactional
    public int sendPickupReminders(java.time.Duration leadTime) {
        var now = java.time.Instant.now();
        var due = reservations.findPickupRemindersDue(now, now.plus(leadTime));
        int sent = 0;
        for (var reservation : due) {
            if (!notifications.remindersEnabled(reservation.getCustomerUserId()) || notifications.reminderAlreadySent(reservation.getId())) continue;
            var business = catalog.business(reservation.getBusinessId());
            notifications.send(reservation.getCustomerUserId(), reservation.getId(), reservation.getOfferId(), "PICKUP_REMINDER", "notifications.pickup.reminder",
                    "Pickup closes soon at " + business.getAddress() + ". Code: " + reservation.getPickupCode());
            sent++;
        }
        return sent;
    }
    public Reservation get(Long id) { return reservations.findById(id).orElseThrow(() -> new ResourceNotFoundException("reservation.not.found")); }

    @Transactional
    public Reservation reserve(Long offerId, Long customerUserId, int quantity) {
        var offer = catalog.reserveOffer(offerId, quantity);
        var code = createCode();
        var reservation = reservations.save(new Reservation(offer.getId(), offer.getBusinessId(), customerUserId,
                quantity, offer.getOfferPrice(), offer.getOriginalPrice(), code,
                offer.getPickupStartAt(), offer.getPickupEndAt()));
        var business = catalog.business(offer.getBusinessId());
        notifications.send(customerUserId, reservation.getId(), offer.getId(), "RESERVATION_CONFIRMED", "notifications.reservation.confirmed", "Your reservation is confirmed. Pickup code: " + code);
        notifications.send(business.getOwnerAccountId(), reservation.getId(), offer.getId(), "NEW_RESERVATION", "notifications.reservation.new", "A customer reserved " + quantity + " unit(s) of " + offer.getTitle() + ".");
        return reservation;
    }

    @Transactional
    public Reservation confirmPickup(String code, Long accountId) {
        Reservation reservation = reservations.findByPickupCode(code).orElseThrow(() -> new ResourceNotFoundException("reservation.not.found"));
        catalog.requireOwner(reservation.getBusinessId(), accountId);
        reservation.confirmPickup(code);
        notifications.send(reservation.getCustomerUserId(), reservation.getId(), reservation.getOfferId(), "RESERVATION_CONFIRMED", "notifications.pickup.confirmed", "Your pickup has been confirmed.");
        return reservations.save(reservation);
    }

    @Transactional
    public Reservation confirmPickup(Long reservationId, String code, Long accountId) {
        Reservation reservation = get(reservationId);
        catalog.requireOwner(reservation.getBusinessId(), accountId);
        if (!reservation.getPickupCode().equalsIgnoreCase(code == null ? "" : code.trim())) throw new IllegalArgumentException("reservation.code.invalid");
        reservation.confirmPickup(code);
        notifications.send(reservation.getCustomerUserId(), reservation.getId(), reservation.getOfferId(), "RESERVATION_CONFIRMED", "notifications.pickup.confirmed", "Your pickup has been confirmed.");
        return reservations.save(reservation);
    }

    @Transactional
    public Reservation cancel(Long reservationId, Long accountId) {
        Reservation reservation = get(reservationId);
        reservation.cancel(accountId);
        catalog.releaseOffer(reservation.getOfferId(), reservation.getQuantity());
        var business = catalog.business(reservation.getBusinessId());
        notifications.send(business.getOwnerAccountId(), reservation.getId(), reservation.getOfferId(), "RESERVATION_CANCELLED", "notifications.reservation.cancelled", "A customer cancelled a reservation.");
        return reservations.save(reservation);
    }

    private String createCode() {
        var code = new StringBuilder("FS-");
        for (int i = 0; i < 8; i++) code.append(CODE_ALPHABET[random.nextInt(CODE_ALPHABET.length)]);
        return code.toString();
    }
}
