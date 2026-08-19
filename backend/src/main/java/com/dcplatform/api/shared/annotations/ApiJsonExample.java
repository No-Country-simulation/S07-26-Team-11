package com.dcplatform.api.shared.annotations;

import org.springframework.http.HttpStatus;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ApiJsonExamples.class)
public @interface ApiJsonExample {
	HttpStatus status() default HttpStatus.OK;

	String description() default "Operación exitosa";

	String path();

	String mediaType() default "application/json";

	String summary() default "";
}
