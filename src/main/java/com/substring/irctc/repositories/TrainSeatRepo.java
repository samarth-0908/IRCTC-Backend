package com.substring.irctc.repositories;

import com.substring.irctc.entity.TrainSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainSeatRepo extends JpaRepository<TrainSeat,Long> {

    @Query("SELECT ts FROM TrainSeat ts WHERE trainSchedule.id = ?1 order by ts.trainSeatOrder")
    List<TrainSeat> findByTrainScheduleId(Long trainScheduleId);
}
