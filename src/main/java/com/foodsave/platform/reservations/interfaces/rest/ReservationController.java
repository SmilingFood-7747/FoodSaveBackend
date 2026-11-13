package com.foodsave.platform.reservations.interfaces.rest;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.foodsave.platform.reservations.application.ReservationService;
import com.foodsave.platform.iam.infrastructure.security.BearerTokenFilter.FoodSavePrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping({"/reservations", "/api/reservations", "/api/v1/reservations"})
public class ReservationController {
    private final ReservationService reservations;

    public ReservationController(ReservationService reservations) { this.reservations = reservations; }

    @GetMapping
    @Operation(summary = "List reservations")
    public List<ReservationResource> all(@AuthenticationPrincipal FoodSavePrincipal actor) {
        return reservations.customerReservations(actor.accountId()).stream().map(ReservationResource::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a reservation")
    public ReservationResource get(@PathVariable Long id, @AuthenticationPrincipal FoodSavePrincipal actor) {
        var reservation = reservations.get(id);
        if (!reservation.getCustomerUserId().equals(actor.accountId()) && !"ADMIN".equals(actor.role()))
            throw new com.foodsave.platform.shared.interfaces.rest.ResourceNotFoundException("reservation.not.found");
        return ReservationResource.from(reservation);
    }

    @PostMapping
    @Operation(summary = "Reserve an offer")
    public ResponseEntity<ReservationResource> reserve(@Valid @RequestBody ReservationRequest request, @AuthenticationPrincipal FoodSavePrincipal actor) {
        var reservation = reservations.reserve(request.offerId(), actor.accountId(), request.quantity());
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(ReservationResource.from(reservation));
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm pickup by code")
    public ReservationResource confirm(@PathVariable Long id, @Valid @RequestBody PickupConfirmationRequest request,
                                       @AuthenticationPrincipal FoodSavePrincipal actor) {
        return ReservationResource.from(reservations.confirmPickup(id, request.code(), actor.accountId()));
    }

    @org.springframework.web.bind.annotation.PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an active reservation")
    public ReservationResource cancel(@PathVariable Long id, @AuthenticationPrincipal FoodSavePrincipal actor) {
        return ReservationResource.from(reservations.cancel(id, actor.accountId()));
    }

    @GetMapping("/business/{businessId}")
    @Operation(summary = "List reservations for an owned business")
    public List<ReservationResource> byBusiness(@PathVariable Long businessId, @AuthenticationPrincipal FoodSavePrincipal actor) {
        return reservations.businessReservations(businessId, actor.accountId()).stream().map(ReservationResource::from).toList();
    }
}
