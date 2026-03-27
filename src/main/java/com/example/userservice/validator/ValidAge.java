package com.example.userservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeValidator.class)
@Documented
public @interface ValidAge {
    String message() default "Debes ser mayor de 18 años";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
