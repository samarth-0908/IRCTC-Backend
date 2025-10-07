package com.substring.irctc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "train_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//Dibba- Coach
public class TrainSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "train_schedule_id")
    private TrainSchedule trainSchedule;


    @Enumerated(EnumType.STRING)
    private CoachType coachType; // Enum: AC, SLEEPER, GENERAL

    private Integer totalSeats;

    //42-2=40=0
    private Integer availableSeats;

    //nextToAssign + number of seats book
    //1+2=3... >42
    private Integer seatNumberToAssign;

    private double price;

    private Integer trainSeatOrder;


    public boolean isCoachFull() {
        return availableSeats <=0;
    }

    public boolean isSeatAvailable(int seatToBook) {
        return availableSeats>=seatToBook;
    }



}
