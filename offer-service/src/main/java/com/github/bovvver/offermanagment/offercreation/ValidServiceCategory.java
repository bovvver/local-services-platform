package com.github.bovvver.offermanagment.offercreation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidServiceCategoryValidator.class)
public @interface ValidServiceCategory {

    String message() default "Invalid service category";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
