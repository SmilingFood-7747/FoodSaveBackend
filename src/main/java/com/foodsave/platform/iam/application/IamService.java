package com.foodsave.platform.iam.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.foodsave.platform.iam.domain.model.Account;
import com.foodsave.platform.iam.domain.model.AccountRole;
import com.foodsave.platform.iam.infrastructure.persistence.jpa.AccountJpaRepository;
import com.foodsave.platform.iam.interfaces.rest.SignUpRequest;
import com.foodsave.platform.iam.interfaces.rest.UpdateProfileRequest;
import com.foodsave.platform.shared.interfaces.rest.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
public class IamService {
    private final AccountJpaRepository accounts;
    private final PasswordService passwords;
    private final TokenService tokens;

    public IamService(AccountJpaRepository accounts, PasswordService passwords, TokenService tokens) {
        this.accounts = accounts; this.passwords = passwords; this.tokens = tokens;
    }

    @Transactional
    public Account register(SignUpRequest request) {
        if (accounts.existsByEmailIgnoreCase(request.email())) throw new IllegalArgumentException("account.email.exists");
        AccountRole role = request.role() == null ? AccountRole.CUSTOMER : request.role();
        if (role == AccountRole.ADMIN) throw new IllegalArgumentException("account.role.invalid");
        return accounts.save(new Account(request.fullName(), request.email(), request.phone(), role, passwords.hash(request.password())));
    }

    public Account authenticate(String email, String password) {
        Account account = accounts.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("auth.credentials.invalid"));
        if (!account.isActive() || !passwords.matches(password, account.getPasswordHash())) throw new IllegalArgumentException("auth.credentials.invalid");
        return account;
    }

    public Account get(Long id) { return accounts.findById(id).orElseThrow(() -> new ResourceNotFoundException("account.not.found")); }
    public String token(Account account) { return tokens.issue(account); }

    @Transactional
    public Account update(Long id, UpdateProfileRequest request) {
        Account account = get(id);
        account.updateProfile(request.fullName(), request.phone());
        return account;
    }
}
