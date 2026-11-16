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
import java.util.List;
import jakarta.validation.Valid;
import com.foodsave.platform.iam.application.IamService;
import com.foodsave.platform.iam.infrastructure.security.BearerTokenFilter.FoodSavePrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping
public class IamController {
    private final IamService iam;
    public IamController(IamService iam) { this.iam = iam; }

    @PostMapping({"/auth/signup", "/api/v1/auth/signup"}) @ResponseStatus(HttpStatus.CREATED)
    public AccountResource signUp(@Valid @RequestBody SignUpRequest request) { return AccountResource.from(iam.register(request)); }

    @PostMapping({"/auth/signin", "/api/v1/auth/signin"})
    @Operation(summary = "Sign in", description = "Use the email and password registered in FoodSave. Example values are placeholders. The response includes the account profile and access token.", responses = {
            @ApiResponse(responseCode = "200", description = "Signed in successfully"),
            @ApiResponse(responseCode = "400", description = "Email or password is incorrect")
    })
    public AuthenticatedSessionResource signIn(@Valid @RequestBody SignInRequest request) {
        var account = iam.authenticate(request.email(), request.password());
        return new AuthenticatedSessionResource(iam.token(account), "Bearer", AccountResource.from(account));
    }

    @GetMapping({"/auth/users/me/profile", "/api/users/me/profile", "/api/v1/users/me/profile"})
    @Operation(summary = "Get my account profile", description = "Returns the profile for the signed-in account.", responses = {
            @ApiResponse(responseCode = "200", description = "Account profile"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AccountResource myProfile(@AuthenticationPrincipal FoodSavePrincipal actor) {
        return AccountResource.from(iam.get(actor.accountId()));
    }

    @GetMapping({"/users", "/api/users", "/api/v1/users"})
    @Operation(summary = "List accounts", description = "Returns account profiles without password fields. Requires an ADMIN bearer token.")
    @SecurityRequirement(name = "bearerAuth")
    public List<AccountResource> accounts(@AuthenticationPrincipal FoodSavePrincipal actor) {
        requireAdmin(actor);
        return iam.listAccounts().stream().map(AccountResource::from).toList();
    }

    @GetMapping({"/auth/users/{id}/profile", "/api/users/{id}/profile", "/api/v1/users/{id}/profile"})
    @Operation(summary = "Get an account profile")
    @SecurityRequirement(name = "bearerAuth")
    public AccountResource profile(@PathVariable Long id, @AuthenticationPrincipal FoodSavePrincipal actor) {
        requireSelfOrAdmin(id, actor);
        return AccountResource.from(iam.get(id));
    }

    @PatchMapping({"/auth/users/{id}/profile", "/api/users/{id}/profile", "/api/v1/users/{id}/profile"})
    @Operation(summary = "Update an account profile")
    @SecurityRequirement(name = "bearerAuth")
    public AccountResource update(@PathVariable Long id, @Valid @RequestBody UpdateProfileRequest request, @AuthenticationPrincipal FoodSavePrincipal actor) {
        requireSelfOrAdmin(id, actor);
        return AccountResource.from(iam.update(id, request));
    }

    private void requireSelfOrAdmin(Long id, FoodSavePrincipal actor) {
        if (actor == null || (!id.equals(actor.accountId()) && !"ADMIN".equals(actor.role())))
            throw new org.springframework.security.access.AccessDeniedException("Account access denied");
    }

    private void requireAdmin(FoodSavePrincipal actor) {
        if (actor == null || !"ADMIN".equals(actor.role()))
            throw new org.springframework.security.access.AccessDeniedException("Admin access required");
    }

}
