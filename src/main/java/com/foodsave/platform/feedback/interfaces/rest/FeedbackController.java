package com.foodsave.platform.feedback.interfaces.rest;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.foodsave.platform.iam.infrastructure.security.BearerTokenFilter.FoodSavePrincipal;
import com.foodsave.platform.feedback.application.FeedbackService;

@RestController
@RequestMapping({"/feedback", "/api/feedback", "/api/v1/feedback"})
public class FeedbackController {
    private final FeedbackService service;
    public FeedbackController(FeedbackService service) { this.service = service; }
    @GetMapping({"/reviews", ""}) public List<FeedbackResource.ReviewView> reviews() { return service.reviews().stream().map(FeedbackResource.ReviewView::from).toList(); }
    @PostMapping({"/reviews", ""}) @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResource.ReviewView submit(@AuthenticationPrincipal FoodSavePrincipal actor, @Valid @RequestBody SubmitReviewRequest request) {
        return FeedbackResource.ReviewView.from(service.submitReview(actor.accountId(), request.reservationId(), request.rating(), request.comment()));
    }
    @GetMapping("/requests") public List<FeedbackResource.SupportView> requests(@AuthenticationPrincipal FoodSavePrincipal actor) { return service.myRequests(actor.accountId()).stream().map(FeedbackResource.SupportView::from).toList(); }
    @PostMapping("/requests") @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResource.SupportView open(@AuthenticationPrincipal FoodSavePrincipal actor, @Valid @RequestBody OpenSupportRequest request) {
        return FeedbackResource.SupportView.from(service.openRequest(actor.accountId(), request.reservationId(), request.subject(), request.description()));
    }
}
