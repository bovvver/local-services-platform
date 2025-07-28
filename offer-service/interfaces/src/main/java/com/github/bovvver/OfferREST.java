package com.github.bovvver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class OfferREST {

    @GetMapping(path = "/me")
    String me() {
        return "Offer Service";
    }
}
