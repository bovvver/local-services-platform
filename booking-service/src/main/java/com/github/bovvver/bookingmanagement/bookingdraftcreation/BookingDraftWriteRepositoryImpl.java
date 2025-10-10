package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class BookingDraftWriteRepositoryImpl implements BookingDraftWriteRepository {
    private final SqlBookingDraftRepository repository;

    @Override
    public void save(final BookingDraft draft) {
        repository.save(BookingDraftMapper.toEntity(draft));
    }

    @Override
    public void delete(final BookingId bookingId) {
        repository.deleteByBookingId(bookingId.value());
    }
}
