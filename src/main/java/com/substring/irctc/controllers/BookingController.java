package com.substring.irctc.controllers;

import com.substring.irctc.dto.BookingRequest;
import com.substring.irctc.dto.BookingResponse;
import com.substring.irctc.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/booking")
public class BookingController {

    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest bookingRequest
    ){
        BookingResponse bookingResponse=  bookingService.createBooking(bookingRequest);

        return new ResponseEntity<>(bookingResponse, HttpStatus.CREATED);
    }


}
