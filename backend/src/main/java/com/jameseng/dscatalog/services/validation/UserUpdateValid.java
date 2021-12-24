//CÓDIO PARA GERAR UMA ANOTATION PROPRIA
package com.jameseng.dscatalog.services.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = UserUpdateValidator.class) //UserUpdateValidator = classe que implementa a anotation
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)

//ANOTATTION = @UserUpdateValid
public @interface UserUpdateValid {
	String message() default "Validation error";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
