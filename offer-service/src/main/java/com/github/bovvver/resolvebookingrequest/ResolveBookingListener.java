package com.github.bovvver.resolvebookingrequest;

import com.github.bovvver.contracts.BookOfferCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class ResolveBookingListener {

    @KafkaListener(topics = "booking.commands", groupId = "offer-service")
    void onCommand(BookOfferCommand cmd) {
        System.out.println("Offer-Service got: " + cmd);
    }
}
