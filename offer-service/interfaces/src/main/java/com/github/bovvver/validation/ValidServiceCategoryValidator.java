package com.github.bovvver.validation;

import com.github.bovvver.vo.ServiceCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

class ValidServiceCategoryValidator implements ConstraintValidator<ValidServiceCategory, Set<String>> {

    @Override
    public boolean isValid(final Set<String> categories, final ConstraintValidatorContext constraintValidatorContext) {

        if(categories == null) {
            return true;
        }
        for (String category : categories) {
            try {
                ServiceCategory.fromString(category);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
}
