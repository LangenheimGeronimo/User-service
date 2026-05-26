package com.example.userservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
@Documented
public @interface ValidName {
    String message() default "El formato del nombre no es válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}