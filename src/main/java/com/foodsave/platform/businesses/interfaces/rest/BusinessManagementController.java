package com.foodsave.platform.businesses.interfaces.rest;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import com.foodsave.platform.iam.infrastructure.security.BearerTokenFilter.FoodSavePrincipal;
import com.foodsave.platform.businesses.application.BusinessManagementService;
import com.foodsave.platform.offers.application.OfferManagementService;
import com.foodsave.platform.reservations.application.ReservationService;

@RestController
@RequestMapping({"/businesses", "/api/businesses", "/api/v1/businesses"})
public class BusinessManagementController {
    private final OfferManagementService offers;
    private final BusinessManagementService businesses;
    private final ReservationService reservations;
    public BusinessManagementController(OfferManagementService offers, BusinessManagementService businesses, ReservationService reservations) { this.offers = offers; this.businesses = businesses; this.reservations = reservations; }

    @GetMapping
    public List<BusinessResource> allPublic() { return businesses.businesses().stream().map(BusinessResource::from).toList(); }

    @GetMapping("/{id}")
    public BusinessResource publicProfile(@PathVariable Long id) { return BusinessResource.from(businesses.business(id)); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public BusinessResource register(@AuthenticationPrincipal FoodSavePrincipal actor, @Valid @RequestBody BusinessRegistrationRequest request) {
        requireBusinessOwner(actor);
        var business = new com.foodsave.platform.businesses.domain.model.Business(actor.accountId(), request.name(), request.address(), request.district(), request.latitude(), request.longitude(), request.contactPhone(), request.pickupConditions(), false);
        return BusinessResource.from(businesses.registerBusiness(actor.accountId(), business));
    }

    @GetMapping("/mine")
    public List<BusinessResource> mine(@AuthenticationPrincipal FoodSavePrincipal actor) {
        requireBusinessOwner(actor);
        return businesses.businessesOwnedBy(actor.accountId()).stream().map(BusinessResource::from).toList();
    }

    @PutMapping("/{id}/profile")
    public BusinessResource update(@PathVariable Long id, @AuthenticationPrincipal FoodSavePrincipal actor,
                                   @Valid @RequestBody BusinessProfileRequest request) {
        requireBusinessOwner(actor);
        return BusinessResource.from(businesses.updateBusiness(id, actor.accountId(), request.name(), request.address(), request.district(), request.latitude(), request.longitude(), request.contactPhone(), request.pickupConditions()));
    }

    @GetMapping("/{id}/summary")
    public BusinessSummaryResource summary(@PathVariable Long id, @AuthenticationPrincipal FoodSavePrincipal actor,
                                           @RequestParam(required = false) java.time.Instant from,
                                           @RequestParam(required = false) java.time.Instant to) {
        requireBusinessOwner(actor);
        businesses.requireOwner(id, actor.accountId());
        if (from != null && to != null && from.isAfter(to)) throw new IllegalArgumentException("business.summary.period.invalid");
        var rows = reservations.businessReservations(id, actor.accountId()).stream()
                .filter(r -> from == null || !r.getCreatedAt().isBefore(from))
                .filter(r -> to == null || !r.getCreatedAt().isAfter(to)).toList();
        long collected = rows.stream().filter(r -> "COLLECTED".equals(r.getStatus())).mapToLong(r -> r.getQuantity()).sum();
        long cancelled = rows.stream().filter(r -> "CANCELLED".equals(r.getStatus())).count();
        BigDecimal sales = rows.stream().filter(r -> "COLLECTED".equals(r.getStatus()))
                .map(r -> r.getUnitPrice().multiply(BigDecimal.valueOf(r.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new BusinessSummaryResource(id, offers.offersForBusiness(id).size(), rows.size(), collected, cancelled, sales);
    }

    private void requireBusinessOwner(FoodSavePrincipal actor) {
        if (actor == null || !"BUSINESS_OWNER".equals(actor.role())) throw new org.springframework.security.access.AccessDeniedException("Business owner role required");
    }

    public record BusinessSummaryResource(Long businessId, int publishedOffers, int reservations, long collectedUnits, long cancelledReservations, BigDecimal sales) {}
}
