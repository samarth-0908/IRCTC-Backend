package com.substring.irctc.service.impl;

import com.substring.irctc.dto.TrainSeatDto;
import com.substring.irctc.entity.TrainSchedule;
import com.substring.irctc.entity.TrainSeat;
import com.substring.irctc.exceptions.ResourceNotFoundException;
import com.substring.irctc.repositories.TrainRepository;
import com.substring.irctc.repositories.TrainScheduleRepo;
import com.substring.irctc.repositories.TrainSeatRepo;
import com.substring.irctc.service.TrainSeatService;
import jakarta.transaction.Transactional;
import lombok.Synchronized;
import org.hibernate.annotations.Synchronize;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrainSeatServiceImpl implements TrainSeatService {

    private TrainSeatRepo trainSeatRepo;
    private TrainScheduleRepo trainScheduleRepo;
    private ModelMapper modelMapper;

    public TrainSeatServiceImpl(TrainSeatRepo trainSeatRepo, TrainScheduleRepo trainScheduleRepo, ModelMapper modelMapper) {
        this.trainSeatRepo = trainSeatRepo;
        this.trainScheduleRepo = trainScheduleRepo;
        this.modelMapper = modelMapper;
    }



    @Override
    public TrainSeatDto createSeatInfo(TrainSeatDto trainSeatDto) {
        TrainSchedule trainSchedule = trainScheduleRepo.findById(trainSeatDto.getTrainScheduleId()).orElseThrow(() -> new ResourceNotFoundException("Train schedule not found with this id:" + trainSeatDto.getTrainScheduleId()));
        TrainSeat trainSeat = modelMapper.map(trainSeatDto, TrainSeat.class);
        trainSeat.setTrainSchedule(trainSchedule);

        TrainSeat savedTrainSeat = trainSeatRepo.save(trainSeat);
        return modelMapper.map(savedTrainSeat,TrainSeatDto.class);

    }

    @Override
    public List<TrainSeatDto> getSeatInfoByTrainScheduleId(Long trainScheduleId) {
        List<TrainSeat> trainSeats = trainSeatRepo.findByTrainScheduleId(trainScheduleId);
        return trainSeats.stream().map(trainSeat -> modelMapper.map(trainSeat,TrainSeatDto.class)).toList();
    }

    @Override
    public void deleteSeatInfo(Long seatId) {

        TrainSeat trainSeat = trainSeatRepo.findById(seatId).orElseThrow(() -> new ResourceNotFoundException("Train seat not found with id: " + seatId));
        trainSeatRepo.delete(trainSeat);
    }

    @Override
    public TrainSeatDto updateSeatInfo(Long trainSeatId, TrainSeatDto trainSeatDto) {
        TrainSeat trainSeat = trainSeatRepo.findById(trainSeatId).orElseThrow(() -> new ResourceNotFoundException("Train seat not found with id: " + trainSeatId));

        TrainSchedule trainSchedule = trainScheduleRepo.findById(trainSeatDto.getTrainScheduleId()).orElseThrow(() -> new ResourceNotFoundException("Train Schedule not found with id: " + trainSeatDto.getTrainScheduleId()));
        trainSeat.setTrainSchedule(trainSchedule);
        trainSeat.setCoachType(trainSeatDto.getCoachType());
        trainSeat.setAvailableSeats(trainSeatDto.getAvailableSeats());
        trainSeat.setPrice(trainSeatDto.getPrice());
        trainSeat.setTotalSeats(trainSeatDto.getTotalSeats());
        trainSeat.setSeatNumberToAssign(trainSeatDto.getSeatNumberToAssign());
        trainSeat.setTrainSeatOrder(trainSeatDto.getTrainSeatOrder());

        TrainSeat updatedTrainSeat = trainSeatRepo.save(trainSeat);
        return modelMapper.map(updatedTrainSeat,TrainSeatDto.class);
    }

//    @Override
//    public TrainSeatDto updateSeatInfo(Long trainSeatId, TrainSeatDto trainSeatDto) {
//        // 1. Find existing TrainSeat
//        TrainSeat trainSeat = trainSeatRepo.findById(trainSeatId)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Train seat not found with id: " + trainSeatId));
//
//        // 2. Fetch TrainSchedule for the given ID in DTO
//        TrainSchedule trainSchedule = trainScheduleRepo.findById(trainSeatDto.getTrainScheduleId())
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Train Schedule not found with id: " + trainSeatDto.getTrainScheduleId()));
//
//        // 3. Map all matching fields from DTO → existing entity
//        modelMapper.map(trainSeatDto, trainSeat);
//
//        // 4. Set the TrainSchedule separately (ModelMapper won't fetch it for you)
//        trainSeat.setTrainSchedule(trainSchedule);
//
//        // 5. Save and return
//        TrainSeat updatedTrainSeat = trainSeatRepo.save(trainSeat);
//        return modelMapper.map(updatedTrainSeat, TrainSeatDto.class);
//    }



    @Synchronized
    public List<Integer> bookSeat(int seatToBook, Long seatId) {

        TrainSeat trainSeat = trainSeatRepo.findById(seatId).orElseThrow(() -> new ResourceNotFoundException("Train coach not found with id: " + seatId));

        if(trainSeat.isSeatAvailable(seatToBook)){

            trainSeat.setAvailableSeats(trainSeat.getAvailableSeats() - seatToBook);
            List<Integer> bookedSeats = new ArrayList<>();
            for(int i=1; i<=seatToBook; i++) {
                bookedSeats.add(trainSeat.getSeatNumberToAssign());
                trainSeat.setSeatNumberToAssign(trainSeat.getSeatNumberToAssign() + 1);
            }

            trainSeatRepo.save(trainSeat);
            return bookedSeats;

        }
        else {
            throw new IllegalStateException("No seats available in this coach");
        }
    }
}
