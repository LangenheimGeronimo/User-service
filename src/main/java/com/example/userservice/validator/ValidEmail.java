package com.example.userservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
    String message() default "El formato del email no es válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

