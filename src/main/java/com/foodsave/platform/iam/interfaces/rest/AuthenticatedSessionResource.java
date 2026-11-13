package com.foodsave.platform.iam.interfaces.rest;

public record AuthenticatedSessionResource(String accessToken, String tokenType, AccountResource account) {}
