package com.substring.irctc.service;

import com.substring.irctc.dto.TrainRouteDto;

import java.util.List;

public interface TrainRouteService {

    TrainRouteDto addRoute(TrainRouteDto dto);

    //get train routes by id
    List<TrainRouteDto> getRoutesByTrain(Long trainId);

    TrainRouteDto updateRoute(Long id, TrainRouteDto dto);

    void deleteRoute(Long id);
}
