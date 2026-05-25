package com.example.userservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true; 
        }

        // Se calcula la diferencia entre la fecha de nacimiento y hoy
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }
}