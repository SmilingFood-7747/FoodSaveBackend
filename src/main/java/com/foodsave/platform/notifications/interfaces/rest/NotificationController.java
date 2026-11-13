package com.foodsave.platform.notifications.interfaces.rest;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.foodsave.platform.iam.infrastructure.security.BearerTokenFilter.FoodSavePrincipal;
import com.foodsave.platform.notifications.application.NotificationService;

@RestController
@RequestMapping({"/notifications", "/api/notifications", "/api/v1/notifications"})
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service = service; }
    @GetMapping public List<NotificationResource> mine(@AuthenticationPrincipal FoodSavePrincipal actor) { return service.mine(actor.accountId()).stream().map(NotificationResource::from).toList(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public NotificationResource send(@AuthenticationPrincipal FoodSavePrincipal actor, @Valid @RequestBody SendNotificationRequest request) {
        if (!"ADMIN".equals(actor.role())) throw new org.springframework.security.access.AccessDeniedException("Administrator role required");
        var notification = service.send(request.recipientAccountId(), request.reservationId(), request.offerId(), request.type(), request.titleKey(), request.body());
        if (notification == null) throw new com.foodsave.platform.shared.interfaces.rest.ResourceNotFoundException("account.not.found");
        return NotificationResource.from(notification);
    }
    @PatchMapping("/{id}/read") public void markRead(@AuthenticationPrincipal FoodSavePrincipal actor, @PathVariable Long id) { service.markRead(actor.accountId(), id); }
    @GetMapping("/preferences") public PreferencesResource preferences(@AuthenticationPrincipal FoodSavePrincipal actor) { return PreferencesResource.from(service.getPreferences(actor.accountId())); }
    @PutMapping("/preferences") public PreferencesResource update(@AuthenticationPrincipal FoodSavePrincipal actor, @RequestBody UpdatePreferencesRequest request) {
        return PreferencesResource.from(service.updatePreferences(actor.accountId(), request.emailEnabled(), request.pushEnabled(), request.remindersEnabled(), request.nearbyOffersEnabled()));
    }
}
