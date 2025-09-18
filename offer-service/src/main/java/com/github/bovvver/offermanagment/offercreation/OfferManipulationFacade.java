package com.github.bovvver.offermanagment.offercreation;

import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.Description;
import com.github.bovvver.offermanagment.vo.Salary;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import com.github.bovvver.offermanagment.vo.Title;
import com.github.bovvver.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferManipulationFacade {

    private final CurrentUser currentUser;
    private final OfferRepository offerRepository;

    public Offer createOffer(CreateOfferCommand createOfferCommand) {
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
