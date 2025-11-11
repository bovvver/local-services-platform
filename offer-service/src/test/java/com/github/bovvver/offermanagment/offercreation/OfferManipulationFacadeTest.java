package com.github.bovvver.offermanagment.offercreation;

import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferWriteRepository;
import com.github.bovvver.shared.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OfferManipulationFacadeTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private OfferWriteRepository offerWriteRepository;

    @InjectMocks
    private OfferManipulationFacade offerManipulationFacade;

    @Test
    void shouldCreateOffer() {
        CreateOfferCommand command = validCreateOfferCommand();
        offerManipulationFacade.createOffer(command);
        verify(offerWriteRepository).save(any(Offer.class));
    }

    private CreateOfferCommand validCreateOfferCommand() {
        return new CreateOfferCommand(
                "Plumbing services",
                "Professional plumbing repairs and installations",
                150.0,
                null,
                Set.of("HOME_SERVICES")
        );
    }
}
