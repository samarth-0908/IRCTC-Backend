package com.substring.irctc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Station {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;        // e.g., NDLS, LKO, BSB
    private String name;        // e.g., New Delhi, Lucknow, Banaras
    private String city;        // City where station is located
    private String state;


//    private int platforms;      // Number of platforms
//    private boolean junction;   // Is this a junction station?


}
