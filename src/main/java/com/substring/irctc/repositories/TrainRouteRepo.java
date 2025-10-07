package com.substring.irctc.repositories;

import com.substring.irctc.entity.TrainRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainRouteRepo extends JpaRepository<TrainRoute,Long> {

    // Additional query methods can be defined here if needed


    @Query("SELECT tr FROM TrainRoute tr WHERE tr.train.id = ?1 order by tr.stationOrder")
    List<TrainRoute> findByTrainId(Long TrainId);
}
