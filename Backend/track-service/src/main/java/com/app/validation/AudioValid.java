package com.app.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.app.validation.validator.AudioValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AudioValidator.class)
public @interface AudioValid {
	String message() default "Invalid audio file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
