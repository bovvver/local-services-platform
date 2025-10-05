package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class BookingDraftRepositoryImpl implements BookingDraftRepository {
    private final SqlBookingDraftRepository repository;

    @Override
    public void save(final BookingDraft draft) {
        repository.save(BookingDraftMapper.toEntity(draft));
    }

    @Override
    public void delete(final BookingId bookingId) {
        repository.deleteByBookingId(bookingId.value());
    }

    @Override
    public Salary findSalaryByBookingId(final BookingId bookingId) {
        Double salaryValue = repository.findSalaryByBookingId(bookingId.value());
        return Salary.of(salaryValue);
    }
}
