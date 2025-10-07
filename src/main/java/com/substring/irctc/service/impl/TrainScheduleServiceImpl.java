package com.substring.irctc.service.impl;

import com.substring.irctc.dto.TrainRouteDto;
import com.substring.irctc.dto.TrainScheduleDto;
import com.substring.irctc.entity.Train;
import com.substring.irctc.entity.TrainSchedule;
import com.substring.irctc.exceptions.ResourceNotFoundException;
import com.substring.irctc.repositories.TrainRepository;
import com.substring.irctc.repositories.TrainRouteRepo;
import com.substring.irctc.repositories.TrainScheduleRepo;
import com.substring.irctc.service.TrainScheduleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainScheduleServiceImpl implements TrainScheduleService {


    private TrainScheduleRepo trainScheduleRepo;

    private TrainRepository trainRepository;

    private ModelMapper modelMapper;

    public TrainScheduleServiceImpl(TrainScheduleRepo trainScheduleRepo, TrainRepository trainRepository, ModelMapper modelMapper) {
        this.trainScheduleRepo = trainScheduleRepo;
        this.trainRepository = trainRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public TrainScheduleDto createSchedule(TrainScheduleDto trainScheduleDto) {

        Train train = trainRepository.findById(trainScheduleDto.getTrainId()).orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + trainScheduleDto.getTrainId()));

        TrainSchedule trainSchedule = modelMapper.map(trainScheduleDto, TrainSchedule.class);

        trainSchedule.setTrain(train);

        TrainSchedule savedSchedule = trainScheduleRepo.save(trainSchedule);

        return modelMapper.map(savedSchedule, TrainScheduleDto.class);


    }

    @Override
    public List<TrainScheduleDto> getTrainSchedulesByTrainId(Long trainId) {

        List<TrainSchedule> trainSchedules = trainScheduleRepo.findByTrainId(trainId);

        return trainSchedules.stream().map(trainSchedule -> modelMapper.map(trainSchedule, TrainScheduleDto.class)).toList();
    }

    @Override
    public void deleteTrainSchedule(Long trainScheduleId) {

        TrainSchedule trainSchedule = trainScheduleRepo.findById(trainScheduleId).orElseThrow(() ->new ResourceNotFoundException("Train Schedule not found with id: " + trainScheduleId));
        trainScheduleRepo.delete(trainSchedule);
    }

    @Override
    public TrainScheduleDto updateTrainSchedule(Long trainScheduleId, TrainScheduleDto trainScheduleDto) {


        TrainSchedule trainSchedule = trainScheduleRepo.findById(trainScheduleId).orElseThrow(() ->new ResourceNotFoundException("Train Schedule not found with id: " + trainScheduleId));

        //Update the train Schedule fields

        Train train = trainRepository.findById(trainScheduleDto.getTrainId()).orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + trainScheduleDto.getTrainId()));

        trainSchedule.setTrain(train);
        trainSchedule.setAvailableSeats(trainScheduleDto.getAvailableSeats());
        trainSchedule.setRunDate(trainScheduleDto.getRunDate());

        TrainSchedule updatedSchedule = trainScheduleRepo.save(trainSchedule);
        return modelMapper.map(updatedSchedule, TrainScheduleDto.class);


    }
}
