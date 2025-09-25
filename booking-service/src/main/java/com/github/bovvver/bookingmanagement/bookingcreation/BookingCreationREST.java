package com.github.bovvver.bookingmanagement.bookingcreation;

import com.github.bovvver.contracts.BookOfferCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class BookingCreationREST {

    private final KafkaTemplate<String, BookOfferCommand> kafka;

    @PostMapping("/bookings/dry-run")
    void dryRun(@RequestBody BookOfferCommand cmd) {
        kafka.send("booking.commands", cmd.offerId().toString(), cmd);
    }
}
