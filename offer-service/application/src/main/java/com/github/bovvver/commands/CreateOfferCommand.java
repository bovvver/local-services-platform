package com.github.bovvver.commands;

import com.github.bovvver.vo.Location;

import java.util.Set;

public record CreateOfferCommand (
        String title,
        String description,
        double salary,
        Location location,
        Set<String> serviceCategories
) {
}
