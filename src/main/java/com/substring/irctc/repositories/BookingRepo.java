package com.substring.irctc.repositories;

import com.substring.irctc.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<Booking,Long> {
}
