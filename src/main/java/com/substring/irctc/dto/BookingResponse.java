package com.substring.irctc.dto;

import com.substring.irctc.entity.BookingStatus;
import com.substring.irctc.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.BeanInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private Long bookingId;
    private LocalDate JourneyDate;
    private LocalTime departureTime;
    private LocalTime ArrivalTime;
    private StationDto sourceStation;
    private StationDto destinationStation;
    private String pnr;
    private BigDecimal totalFare;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private List<BookingPassengerDto> passengers;
}
