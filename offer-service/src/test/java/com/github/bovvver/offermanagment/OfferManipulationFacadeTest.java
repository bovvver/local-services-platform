package com.github.bovvver.offermanagment;

import com.github.bovvver.offermanagment.offercreation.CreateOfferCommand;
import com.github.bovvver.offermanagment.offercreation.OfferManipulationFacade;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import com.github.bovvver.offermanagment.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OfferManipulationFacadeTest {

    @Test
    void createsOfferSuccessfully() {
        CurrentUser currentUser = mock(CurrentUser.class);
        OfferWriteRepository offerWriteRepository = mock(OfferWriteRepository.class);
        OfferManipulationFacade facade = new OfferManipulationFacade(currentUser, offerWriteRepository);

        UUID userId = UUID.randomUUID();
        when(currentUser.getId()).thenReturn(UserId.of(userId));

        CreateOfferCommand command = new CreateOfferCommand(
                "Sample Title",
                "Sample Description",
                5000.0,
                Location.of(52.2297, 21.0122),
                Set.of("HOME_SERVICES", "CLEANING")
        );

        Offer savedOffer = mock(Offer.class);
        when(offerWriteRepository.save(any(Offer.class))).thenReturn(savedOffer);

        Offer result = facade.createOffer(command);

        ArgumentCaptor<Offer> offerCaptor = ArgumentCaptor.forClass(Offer.class);
        verify(offerWriteRepository).save(offerCaptor.capture());

        Offer capturedOffer = offerCaptor.getValue();
        assertThat(capturedOffer.getTitle().value()).isEqualTo(command.title());
        assertThat(capturedOffer.getDescription().value()).isEqualTo(command.description());
        assertThat(capturedOffer.getAuthorId().value()).isEqualTo(userId);
        assertThat(capturedOffer.getLocation()).isEqualTo(command.location());
        assertThat(capturedOffer.getServiceCategories())
                .containsExactlyInAnyOrderElementsOf(command.serviceCategories().stream().map(ServiceCategory::fromString).toList());
        assertThat(capturedOffer.getSalary().value()).isEqualTo(command.salary());
        assertThat(result).isEqualTo(savedOffer);
    }

    @Test
    void throwsExceptionWhenServiceCategoryIsInvalid() {
        CurrentUser currentUser = mock(CurrentUser.class);
        OfferWriteRepository offerWriteRepository = mock(OfferWriteRepository.class);
        OfferManipulationFacade facade = new OfferManipulationFacade(currentUser, offerWriteRepository);

        CreateOfferCommand command = new CreateOfferCommand(
                "Sample Title",
                "Sample Description",
                5000.0,
                Location.of(52.2297, 21.0122),
                Set.of("INVALID_CATEGORY")
        );

        assertThatThrownBy(() -> facade.createOffer(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid service category: INVALID_CATEGORY");
    }

    @Test
    void throwsExceptionWhenTitleIsNull() {
        CurrentUser currentUser = mock(CurrentUser.class);
        OfferWriteRepository offerWriteRepository = mock(OfferWriteRepository.class);
        OfferManipulationFacade facade = new OfferManipulationFacade(currentUser, offerWriteRepository);

        CreateOfferCommand command = new CreateOfferCommand(
                null,
                "Sample Description",
                5000.0,
                Location.of(52.2297, 21.0122),
                Set.of("HOME_SERVICES")
        );

        assertThatThrownBy(() -> facade.createOffer(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Title cannot be null or blank");
    }

    @Test
    void throwsExceptionWhenSalaryIsNegative() {
        CurrentUser currentUser = mock(CurrentUser.class);
        OfferWriteRepository offerWriteRepository = mock(OfferWriteRepository.class);
        OfferManipulationFacade facade = new OfferManipulationFacade(currentUser, offerWriteRepository);

        CreateOfferCommand command = new CreateOfferCommand(
                "Sample Title",
                "Sample Description",
                -5000.0,
                Location.of(52.2297, 21.0122),
                Set.of("HOME_SERVICES")
        );

        assertThatThrownBy(() -> facade.createOffer(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Salary cannot be negative");
    }
}