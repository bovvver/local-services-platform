package com.github.bovvver;

import com.github.bovvver.commands.CreateOfferCommand;
import com.github.bovvver.vo.Description;
import com.github.bovvver.vo.Salary;
import com.github.bovvver.vo.ServiceCategory;
import com.github.bovvver.vo.Title;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferManipulationFacade {

    private final CurrentUser currentUser;
    private final OfferRepository offerRepository;

    Offer createOffer(CreateOfferCommand createOfferCommand) {
        Offer createdOffer = Offer.create(
                Title.of(createOfferCommand.title()),
                Description.of(createOfferCommand.description()),
                currentUser.getId(),
                createOfferCommand.location(),
                createOfferCommand.serviceCategories().stream().map(ServiceCategory::fromString).collect(Collectors.toSet()),
                Salary.of(createOfferCommand.salary())
        );
        return offerRepository.save(createdOffer);
    }
}
