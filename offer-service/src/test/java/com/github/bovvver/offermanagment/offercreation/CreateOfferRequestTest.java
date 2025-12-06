package com.github.bovvver.offermanagment.offercreation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateOfferRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private LocationDTO validLocation() {
        return new LocationDTO(52.2297, 21.0122);
    }

    @Test
    void shouldPassValidationWithValidData() {
        var request = new CreateOfferRequest(
                "Plumbing services",
                "Professional plumbing repairs and installations",
                BigDecimal.valueOf(150.0),
                validLocation(),
                Set.of("HOME_SERVICES")
        );

        Set<ConstraintViolation<CreateOfferRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenTitleIsBlank() {
        var request = new CreateOfferRequest(
                " ",
                "Valid description",
                BigDecimal.valueOf(100.0),
                validLocation(),
                Set.of("HOME_SERVICES")
        );

        Set<ConstraintViolation<CreateOfferRequest>> violations = validator.validate(request);
        assertThat(violations).extracting("message").contains("Title cannot be blank");
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        String longDescription = "a".repeat(1001);
        var request = new CreateOfferRequest(
                "Valid title",
                longDescription,
                BigDecimal.valueOf(100.0),
                validLocation(),
                Set.of("HOME_SERVICES")
        );

        Set<ConstraintViolation<CreateOfferRequest>> violations = validator.validate(request);
        assertThat(violations).extracting("message").contains("Description cannot exceed 1000 characters");
    }

    @Test
    void shouldFailWhenSalaryNegative() {
        var request = new CreateOfferRequest(
                "Valid title",
                "Valid description",
                BigDecimal.valueOf(-5.0),
                validLocation(),
                Set.of("HOME_SERVICES")
        );

        Set<ConstraintViolation<CreateOfferRequest>> violations = validator.validate(request);
        assertThat(violations).extracting("message").contains("Salary must be non-negative");
    }

    @Test
    void shouldFailWhenLocationIsInvalid() {
        var request = new CreateOfferRequest(
                "Valid title",
                "Valid description",
                BigDecimal.valueOf(100.0),
                new LocationDTO(-500, 0),
                Set.of("HOME_SERVICES")
        );

        Set<ConstraintViolation<CreateOfferRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("location.latitude");
    }

    @Test
    void shouldFailWhenTooManyServiceCategories() {
        var categories = new HashSet<>(Set.of("HOME_SERVICES", "AUTOMOTIVE", "BEAUTY_WELLNESS", "EDUCATION", "TECH_SUPPORT", "DELIVERY"));
        var request = new CreateOfferRequest(
                "Valid title",
                "Valid description",
                BigDecimal.valueOf(100.0),
                validLocation(),
                categories
        );

        Set<ConstraintViolation<CreateOfferRequest>> violations = validator.validate(request);
        assertThat(violations).extracting("message").contains("A maximum of 5 service categories are allowed");
    }

    @Test
    void shouldFailWhenCategoryInvalid() {
        var request = new CreateOfferRequest(
                "Valid title",
                "Valid description",
                BigDecimal.valueOf(100.0),
                validLocation(),
                Set.of("INVALID_CATEGORY")
        );

        Set<ConstraintViolation<CreateOfferRequest>> violations = validator.validate(request);
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Service category must be one of: HOME_SERVICES, AUTOMOTIVE, BEAUTY_WELLNESS, EDUCATION, TECH_SUPPORT, DELIVERY, CLEANING, REPAIR, GARDENING, PET_SERVICES");
    }

    @Test
    void shouldFailWhenCategoryBlank() {
        var request = new CreateOfferRequest(
                "Valid title",
                "Valid description",
                BigDecimal.valueOf(100.0),
                validLocation(),
                Set.of(" ")
        );

        Set<ConstraintViolation<CreateOfferRequest>> violations = validator.validate(request);
        assertThat(violations).extracting("message").contains("Service category cannot be blank");
    }
}
