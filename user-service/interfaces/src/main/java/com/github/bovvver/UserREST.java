package com.github.bovvver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserREST {

    private static final String ME_ENDPOINT = "/me";

    @GetMapping(path = ME_ENDPOINT)
    String me() {
        return "User Service";
    }
}
