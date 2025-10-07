package com.substring.irctc.repositories;

import com.substring.irctc.entity.BookingPassenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingPassengerRepo extends JpaRepository<BookingPassenger,Long> {
}
