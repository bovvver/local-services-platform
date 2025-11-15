package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingDraftMapperTest {

    @Test
    void mapBookingDraftToEntitySuccessfully() {
        BookingDraft draft = new BookingDraft(
                BookingId.of(UUID.randomUUID()),
                OfferId.of(UUID.randomUUID()),
                UserId.of(UUID.randomUUID()),
                Salary.of(500.0),
                LocalDateTime.now()
        );

        BookingDraftEntity entity = BookingDraftMapper.toEntity(draft);

        assertEquals(draft.getBookingId().value(), entity.getBookingId());
        assertEquals(draft.getOfferId().value(), entity.getOfferId());
        assertEquals(draft.getUserId().value(), entity.getUserId());
        assertEquals(draft.getSalary().value(), entity.getSalary());
        assertEquals(draft.getCreatedAt(), entity.getCreatedAt());
    }
}
