package com.foodsave.platform.iam.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordServiceTest {
    private final PasswordService passwords = new PasswordService();

    @Test
    void hashesPasswordsAndVerifiesTheOriginalValue() {
        String encoded = passwords.hash("FoodSave123!");

        assertNotEquals("FoodSave123!", encoded);
        assertTrue(passwords.matches("FoodSave123!", encoded));
        assertFalse(passwords.matches("WrongPassword!", encoded));
    }

    @Test
    void usesDifferentSaltsForTheSamePassword() {
        String first = passwords.hash("FoodSave123!");
        String second = passwords.hash("FoodSave123!");

        assertNotEquals(first, second);
        assertTrue(passwords.matches("FoodSave123!", first));
        assertTrue(passwords.matches("FoodSave123!", second));
    }
}
