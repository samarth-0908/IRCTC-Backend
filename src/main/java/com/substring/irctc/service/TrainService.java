package com.substring.irctc.service;

import com.substring.irctc.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface TrainService {


    TrainDTO createTrain(TrainDTO trainDTO);

    PagedResponse<TrainDTO> getAllTrains(int page, int size, String sortBy, String sortDir);

    TrainDTO getTrainById(Long id);

    TrainDTO updateTrain(Long id, TrainDTO dto);

    void deleteTrain(Long id);

    TrainImageResponse upload(MultipartFile image, Long id) throws IOException;

    TrainImageDataWithResource loadImageByTrainId(Long trainId) throws MalformedURLException;

    //search trains for booking
    List<AvailableTrainResponse> userTrainSearch(UserTrainSearchRequest request);
}
