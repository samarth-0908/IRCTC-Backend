package com.substring.irctc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.awt.print.Book;

@Entity
@Table(name = "booking_passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String name;

    private Integer age;

    private String gender;

    private String seatNumber;

    @ManyToOne
    private TrainSeat trainSeat;
}
