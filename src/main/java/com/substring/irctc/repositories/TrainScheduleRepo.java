package com.substring.irctc.repositories;

import com.substring.irctc.entity.Train;
import com.substring.irctc.entity.TrainSchedule;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainScheduleRepo extends JpaRepository<TrainSchedule,Long> {

    @Query("SELECT ts FROM TrainSchedule ts where ts.train.id = ?1")
    List<TrainSchedule> findByTrainId(Long trainId);

    @Query("SELECT ts FROM TrainSchedule ts where ts.train.id = ?1 AND ts.runDate = ?2")
   Optional<TrainSchedule> findByTrainIdAndRunDate(Long trainId, LocalDateTime runDate);
}
