package com.substring.irctc.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import org.modelmapper.internal.bytebuddy.dynamic.loading.InjectionClassLoader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Stack;

@Entity
@Table(name = "train_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate runDate;

    @ManyToOne
    @JoinColumn(name = "train_id")
    private Train train;

    private Integer availableSeats;


    @OneToMany(mappedBy = "trainSchedule")
    private List<TrainSeat> trainSeats;
    //sice ek train has multliple coaches i.e Dibbe

    @OneToMany(mappedBy = "trainSchedule")
    private List<Booking> bookings;


    //kitni seat ki type

    //booking


}
