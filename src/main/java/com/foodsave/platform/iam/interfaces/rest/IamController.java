package com.foodsave.platform.iam.interfaces.rest;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.foodsave.platform.iam.application.IamService;
import com.foodsave.platform.iam.infrastructure.security.BearerTokenFilter.FoodSavePrincipal;

@RestController
@RequestMapping
public class IamController {
    private final IamService iam;
    public IamController(IamService iam) { this.iam = iam; }

    @PostMapping({"/auth/signup", "/api/v1/auth/signup"}) @ResponseStatus(HttpStatus.CREATED)
    public AccountResource signUp(@Valid @RequestBody SignUpRequest request) { return AccountResource.from(iam.register(request)); }

    @PostMapping({"/auth/signin", "/api/v1/auth/signin"})
    public AuthenticatedSessionResource signIn(@Valid @RequestBody SignInRequest request) {
        var account = iam.authenticate(request.email(), request.password());
        return new AuthenticatedSessionResource(iam.token(account), "Bearer", AccountResource.from(account));
    }

    @GetMapping({"/auth/users/{id}/profile", "/api/users/{id}/profile", "/api/v1/users/{id}/profile"})
    public AccountResource profile(@PathVariable Long id, @AuthenticationPrincipal FoodSavePrincipal actor) {
        requireSelfOrAdmin(id, actor);
        return AccountResource.from(iam.get(id));
    }

    @PatchMapping({"/auth/users/{id}/profile", "/api/users/{id}/profile", "/api/v1/users/{id}/profile"})
    public AccountResource update(@PathVariable Long id, @Valid @RequestBody UpdateProfileRequest request, @AuthenticationPrincipal FoodSavePrincipal actor) {
        requireSelfOrAdmin(id, actor);
        return AccountResource.from(iam.update(id, request));
    }

    private void requireSelfOrAdmin(Long id, FoodSavePrincipal actor) {
        if (actor == null || (!id.equals(actor.accountId()) && !"ADMIN".equals(actor.role())))
            throw new org.springframework.security.access.AccessDeniedException("Account access denied");
    }
}
