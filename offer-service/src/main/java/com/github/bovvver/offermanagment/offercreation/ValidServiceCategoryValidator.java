package com.github.bovvver.offermanagment.offercreation;

import com.github.bovvver.offermanagment.vo.ServiceCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidServiceCategoryValidator implements ConstraintValidator<ValidServiceCategory, Set<String>> {

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
