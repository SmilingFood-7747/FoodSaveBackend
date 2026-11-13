package com.foodsave.platform.iam.interfaces.rest;

import com.foodsave.platform.iam.domain.model.Account;
import com.foodsave.platform.iam.domain.model.AccountRole;

public record AccountResource(Long id, String fullName, String email, String phone, AccountRole role) {
    public static AccountResource from(Account account) {
        return new AccountResource(account.getId(), account.getFullName(), account.getEmail(), account.getPhone(), account.getRole());
    }
}
