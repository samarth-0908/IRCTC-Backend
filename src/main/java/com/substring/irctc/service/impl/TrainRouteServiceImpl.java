package com.substring.irctc.service.impl;

import com.substring.irctc.dto.TrainRouteDto;
import com.substring.irctc.entity.Station;
import com.substring.irctc.entity.Train;
import com.substring.irctc.entity.TrainRoute;
import com.substring.irctc.exceptions.ResourceNotFoundException;
import com.substring.irctc.repositories.StationRepo;
import com.substring.irctc.repositories.TrainRepository;
import com.substring.irctc.repositories.TrainRouteRepo;
import com.substring.irctc.service.TrainRouteService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainRouteServiceImpl implements TrainRouteService {

    private TrainRepository trainRepository;
    private StationRepo stationRepo;
    private TrainRouteRepo trainRouteRepo;
    private ModelMapper modelMapper;

    public TrainRouteServiceImpl(TrainRepository trainRepository, StationRepo stationRepo, TrainRouteRepo trainRouteRepo, ModelMapper modelMapper) {
        this.trainRepository = trainRepository;
        this.stationRepo = stationRepo;
        this.trainRouteRepo = trainRouteRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public TrainRouteDto addRoute(TrainRouteDto dto) {

        Train train = trainRepository.findById(dto.getTrain().getId()).orElseThrow(() -> new ResourceNotFoundException("Train not found with ID: " + dto.getTrain().getId()));
        Station station = stationRepo.findById(dto.getStation().getId()).orElseThrow(() -> new ResourceNotFoundException("Station not found with ID: " + dto.getStation().getId()));

        //Convert dto to entity
        TrainRoute trainRoute = modelMapper.map(dto, TrainRoute.class);
        trainRoute.setTrain(train);
        trainRoute.setStation(station);

        //save the train route entity
        TrainRoute savedTrainRoute = trainRouteRepo.save(trainRoute);

        //convert saved entity back to dto
        TrainRouteDto savedTrainRouteDto = modelMapper.map(savedTrainRoute, TrainRouteDto.class);

        return savedTrainRouteDto;

    }

    @Override
    public List<TrainRouteDto> getRoutesByTrain(Long trainId) {
        Train train = trainRepository.findById(trainId).orElseThrow(() -> new ResourceNotFoundException("Train not found with ID: " + trainId));
        List<TrainRoute> trainRoutes = trainRouteRepo.findByTrainId(trainId);
        List<TrainRouteDto> trainRouteDtos = trainRoutes.stream().map(trainRoute -> modelMapper.map(trainRoute, TrainRouteDto.class)).toList();


        return trainRouteDtos;
    }

    @Override
    public TrainRouteDto updateRoute(Long id, TrainRouteDto dto) {

    TrainRoute existingRoute = trainRouteRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Train route not found with ID:" + id));

        Train train = trainRepository.findById(dto.getTrain().getId()).orElseThrow(() -> new ResourceNotFoundException("Train not found with ID: " + dto.getTrain().getId()));
        Station station = stationRepo.findById(dto.getStation().getId()).orElseThrow(() -> new ResourceNotFoundException("Station not found with ID: " + dto.getStation().getId()));

        //update the existing route with new values

        existingRoute.setStation(station);
        existingRoute.setTrain(train);
        existingRoute.setStationOrder(dto.getStationOrder());
        existingRoute.setArrivalTime(dto.getArrivalTime());
        existingRoute.setDepartureTime(dto.getDepartureTime());
        existingRoute.setHaltMinutes(dto.getHaltMinutes());
        existingRoute.setDistanceFromSource(dto.getDistanceFromSource());
        
        //save the updated route
        TrainRoute updatedRoute = trainRouteRepo.save(existingRoute);

        //convert updated entity back to dto
        TrainRouteDto updatedRouteDto = modelMapper.map(updatedRoute, TrainRouteDto.class);
        return  updatedRouteDto;

    }

    @Override
    public void deleteRoute(Long id) {
        TrainRoute existingRoute = trainRouteRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Train route not found with ID:" + id));
        //delete the route
        trainRouteRepo.delete(existingRoute);


    }
}
