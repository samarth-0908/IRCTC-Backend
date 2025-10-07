package com.substring.irctc.dto;

import com.substring.irctc.entity.CoachType;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainSeatDto {

    private Long id;
    private Long trainScheduleId;
    private CoachType coachType;
    private Integer totalSeats;
    private Integer availableSeats;
    private Double price;
    private Integer seatNumberToAssign;
    private Integer trainSeatOrder;
}
