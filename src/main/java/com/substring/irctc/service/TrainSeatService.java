package com.substring.irctc.service;

import com.substring.irctc.dto.TrainRouteDto;
import com.substring.irctc.dto.TrainSeatDto;
import com.substring.irctc.entity.TrainSeat;

import java.util.List;

public interface TrainSeatService {


    TrainSeatDto createSeatInfo(TrainSeatDto trainSeatDto);
    List<TrainSeatDto> getSeatInfoByTrainScheduleId(Long trainScheduleId);
    void deleteSeatInfo(Long seatId);
    TrainSeatDto updateSeatInfo(Long trainSeatId, TrainSeatDto trainSeatDto);
    public List<Integer> bookSeat(int seatToBook, Long seatId);
}
