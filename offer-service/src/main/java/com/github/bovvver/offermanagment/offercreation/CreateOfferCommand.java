package com.github.bovvver.offermanagment.offercreation;

import com.github.bovvver.offermanagment.vo.Location;

import java.util.Set;

public record CreateOfferCommand (
        String title,
        String description,
        double salary,
        Location location,
        Set<String> serviceCategories
) {
}
