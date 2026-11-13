package com.foodsave.platform.offers.interfaces.rest;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.foodsave.platform.offers.application.OfferManagementService;
import com.foodsave.platform.offers.domain.model.Offer;
import com.foodsave.platform.iam.infrastructure.security.BearerTokenFilter.FoodSavePrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping
public class OfferController {
    private final OfferManagementService catalog;

    public OfferController(OfferManagementService catalog) { this.catalog = catalog; }

    @GetMapping({"/offers", "/api/v1/offers"})
    @Operation(summary = "List offers")
    public List<OfferResource> offers() {
        return catalog.offers().stream().map(OfferResource::from).toList();
    }

    @GetMapping({"/offers/{id}", "/api/v1/offers/{id}"})
    @Operation(summary = "Get an offer")
    public OfferResource offer(@PathVariable Long id) {
        return OfferResource.from(catalog.offer(id));
    }

    @PostMapping({"/offers", "/api/v1/offers"})
    @Operation(summary = "Create an offer")
    public ResponseEntity<OfferResource> createOffer(@Valid @RequestBody OfferRequest request, @AuthenticationPrincipal FoodSavePrincipal actor) {
        requireBusinessOwner(actor);
        catalog.requireOwner(request.businessId(), actor.accountId());
        var offer = catalog.createOffer(toOffer(request));
        return ResponseEntity.created(URI.create("/offers/" + offer.getId())).body(OfferResource.from(offer));
    }

    @PutMapping({"/offers/{id}", "/api/v1/offers/{id}"})
    @Operation(summary = "Replace an offer")
    public OfferResource replaceOffer(@PathVariable Long id, @Valid @RequestBody OfferRequest request, @AuthenticationPrincipal FoodSavePrincipal actor) {
        requireBusinessOwner(actor);
        catalog.requireOwner(catalog.offer(id).getBusinessId(), actor.accountId());
        catalog.requireOwner(request.businessId(), actor.accountId());
        return OfferResource.from(catalog.updateOffer(id, toOffer(request)));
    }

    @DeleteMapping({"/offers/{id}", "/api/v1/offers/{id}"})
    @Operation(summary = "Delete an offer")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id, @AuthenticationPrincipal FoodSavePrincipal actor) {
        requireBusinessOwner(actor);
        catalog.requireOwner(catalog.offer(id).getBusinessId(), actor.accountId());
        catalog.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }

    @org.springframework.web.bind.annotation.PatchMapping({"/offers/{id}/pause", "/api/v1/offers/{id}/pause"})
    @Operation(summary = "Pause an offer")
    public OfferResource pauseOffer(@PathVariable Long id, @AuthenticationPrincipal FoodSavePrincipal actor) {
        requireBusinessOwner(actor);
        return OfferResource.from(catalog.pauseOffer(id, actor.accountId()));
    }

    private void requireBusinessOwner(FoodSavePrincipal actor) {
        if (actor == null || !"BUSINESS_OWNER".equals(actor.role())) throw new org.springframework.security.access.AccessDeniedException("Business owner role required");
    }

    private Offer toOffer(OfferRequest request) {
        return new Offer(request.businessId(), request.category(), request.title(), request.description(),
                request.allergens(), request.originalPrice(), request.offerPrice(), request.initialUnits(),
                request.pickupStartAt(), request.pickupEndAt(), request.expiresAt(), request.image(), request.plusExclusive());
    }
}
