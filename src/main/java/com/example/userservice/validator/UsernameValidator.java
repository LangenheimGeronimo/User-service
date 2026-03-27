package com.example.userservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false; 
        }

        if (value.length() < 2 || value.length() > 20) {
            return false;
        }

        return value.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$");
    }
}