package com.substring.irctc.service.impl;

import com.substring.irctc.dto.*;
import com.substring.irctc.entity.*;
import com.substring.irctc.exceptions.ResourceNotFoundException;
import com.substring.irctc.repositories.StationRepo;
import com.substring.irctc.repositories.TrainRepository;
import com.substring.irctc.repositories.TrainScheduleRepo;
import com.substring.irctc.service.TrainService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainServiceImpl implements TrainService {

    private TrainRepository trainRepository;

    private StationRepo stationRepo;

    private ModelMapper modelMapper;

    private TrainScheduleRepo trainScheduleRepo;

    @Value("${train.image.folder.path}")
    private String folderPath;


    public TrainServiceImpl(TrainScheduleRepo trainScheduleRepo, ModelMapper modelMapper, StationRepo stationRepo, TrainRepository trainRepository) {
        this.trainScheduleRepo = trainScheduleRepo;
        this.modelMapper = modelMapper;
        this.stationRepo = stationRepo;
        this.trainRepository = trainRepository;
    }

    @Override
    public TrainDTO createTrain(TrainDTO trainDTO) {




        // Step 1: Fetch actual Station entities using IDs
        Station source = stationRepo.findById(trainDTO.getSourceStation().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Source station not found"));

        Station destination = stationRepo.findById(trainDTO.getDestinationStation().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination station not found"));

        // Step 2: Convert DTO to entity
        Train train = modelMapper.map(trainDTO, Train.class);

        // Step 3: Set the fetched entities
        train.setSourceStation(source);
        train.setDestinationStation(destination);

        // Step 4: Save the train
        Train savedTrain = trainRepository.save(train);

        // Step 5: Convert entity back to DTO
        return modelMapper.map(savedTrain, TrainDTO.class);
    }

    @Override
    public PagedResponse<TrainDTO> getAllTrains(int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.trim().toLowerCase().equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Train> trainPage = trainRepository.findAll(pageable);

        Page<TrainDTO> trainDTOPage = trainPage.map(train -> modelMapper.map(train, TrainDTO.class));

        return PagedResponse.fromPage(trainDTOPage);
    }

    @Override
    public TrainDTO getTrainById(Long id) {

        Train train = trainRepository.findById(id).orElseThrow(() -> (new ResourceNotFoundException("station not found with given ID !!")));

        return modelMapper.map(train, TrainDTO.class);
    }

    @Override
    public TrainDTO updateTrain(Long id, TrainDTO dto) {

        Train existingTrain = trainRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Train not found with given ID !!"));

        existingTrain.setName(dto.getName());
        existingTrain.setNumber(dto.getNumber());
        existingTrain.setTotalDistance(dto.getTotalDistance());
        // ✅ Fetch and set source station
        Station source = stationRepo.findById(dto.getSourceStation().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Source Station not found"));
        existingTrain.setSourceStation(source);

        // ✅ Fetch and set destination station
        Station destination = stationRepo.findById(dto.getDestinationStation().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination Station not found"));
        existingTrain.setDestinationStation(destination);

        // ✅ Save updated train
        Train updatedTrain = trainRepository.save(existingTrain);

        return modelMapper.map(updatedTrain, TrainDTO.class);
    }

    @Override
    public void deleteTrain(Long id) {
        Train existingTrain = trainRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Train not found with given ID !!"));

        trainRepository.delete(existingTrain);
    }


    @Override
    public List<AvailableTrainResponse> userTrainSearch(UserTrainSearchRequest request) {

        // Fetch trains from repository with source and destination
        List<Train> matchedTrains = this.trainRepository.findTrainBySourceAndDestination(
                request.getSourceStationId(), request.getDestinationStationId());

// Use stream to filter valid trains directly
        List<AvailableTrainResponse> responseList = matchedTrains.stream()
                .filter(train -> {
                    // Find source and destination station orders in a single loop
                    Integer sourceStationOrder = null;
                    Integer destinationStationOrder = null;

                    for (TrainRoute trainRoute : train.getRoutes()) {
                        if (trainRoute.getStation().getId().equals(request.getSourceStationId())) {
                            sourceStationOrder = trainRoute.getStationOrder();
                        } else if (trainRoute.getStation().getId().equals(request.getDestinationStationId())) {
                            destinationStationOrder = trainRoute.getStationOrder();
                        }

                        // Early exit if both station orders are found
                        if (sourceStationOrder != null && destinationStationOrder != null) {
                            break;
                        }
                    }

                    // Check for station order validity and the schedule date condition
                    boolean validOrder = sourceStationOrder != null && destinationStationOrder != null &&
                            sourceStationOrder < destinationStationOrder;

                    boolean runOnThatDay = train.getSchedules().stream()
                            .anyMatch(schedule -> schedule.getRunDate().equals(request.getJourneyDate()));

                    return validOrder && runOnThatDay;
                })
                .map(train -> {
                    // Find the matching schedule for the journey date
                    TrainSchedule trainSchedule = train.getSchedules().stream()
                            .filter(sch -> sch.getRunDate().equals(request.getJourneyDate()))
                            .findFirst().orElse(null);

                    if (trainSchedule == null) {
                        return null;  // Skip if there's no matching schedule
                    }

                    // Find the source route for the train
                    TrainRoute sourceRoute = train.getRoutes().stream()
                            .filter(route -> route.getStation().getId().equals(request.getSourceStationId()))
                            .findFirst().orElse(null);

                    if (sourceRoute == null) {
                        return null;  // Skip if there's no source route
                    }

                    // Map seat types and prices
                    Map<CoachType, Integer> seatMap = new HashMap<>();
                    Map<CoachType, Double> priceMap = new HashMap<>();

                    trainSchedule.getTrainSeats().forEach(trainSeat -> {
                        seatMap.merge(trainSeat.getCoachType(), trainSeat.getAvailableSeats(), Integer::sum);
                        priceMap.putIfAbsent(trainSeat.getCoachType(), trainSeat.getPrice());
                    });

                    // Build the response for the valid train
                    return AvailableTrainResponse.builder()
                            .trainId(train.getId())
                            .trainNumber(train.getNumber())
                            .trainName(train.getName())
                            .departureTime(sourceRoute.getDepartureTime())
                            .arrivalTime(sourceRoute.getArrivalTime())
                            .seatsAvailable(seatMap)
                            .priceByCoach(priceMap)
                            .scheduleDate(trainSchedule.getRunDate())
                            .trainScheduleId(trainSchedule.getId())
                            .build();
                })
                .filter(Objects::nonNull)  // Remove null responses (invalid trains)
                .collect(Collectors.toList());  // Collect results into a list

// Return the final list of valid available train responses
        return responseList;

    }

    @Override
    public TrainImageResponse upload(MultipartFile file, Long id) throws IOException {



        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found!!"));

        // Check & create directory if not exist
        if (!Files.exists(Paths.get(folderPath))) {
            Files.createDirectories(Paths.get(folderPath));
        }

        // Generate unique file name and path
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fullFilePath = folderPath + uniqueFileName;

        // Save file to disk
        Files.copy(file.getInputStream(), Paths.get(fullFilePath), StandardCopyOption.REPLACE_EXISTING);

        // Create TrainImage entity
        TrainImage trainImage = new TrainImage();
        trainImage.setFileName(uniqueFileName); // only file name (optional)
        trainImage.setFileType(file.getContentType());
        trainImage.setSize(file.getSize());
        trainImage.setTrain(train);

        // Associate with Train
        train.setTrainImage(trainImage);

        // Save train (and image via cascade)
        Train savedTrain = trainRepository.save(train);

        // Return response using your record constructor
        return TrainImageResponse.from(savedTrain.getTrainImage(), "http://localhost:8080", savedTrain.getId());



//        OR
//        Train train = trainRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("train Not found!!"));
//
//        //checking and creating folder
//
//        if(!Files.exists(Paths.get(folderPath))){
//            Files.createDirectories(Paths.get(folderPath));
//        }
//
//        String fullFilePath = folderPath + UUID.randomUUID()+"_" +file.getOriginalFilename();
//        Files.copy(file.getInputStream(), Paths.get(fullFilePath), StandardCopyOption.REPLACE_EXISTING);
//
//        System.out.println("file uploaded");
//
//        TrainImage trainImage = new TrainImage();
//        trainImage.setFileName(fullFilePath);
//        trainImage.setFileType(file.getContentType());
//        trainImage.setSize(file.getSize());
//        trainImage.setTrain(train);
//        train.setTrainImage(trainImage);
//
//        Train savedTrain = trainRepository.save(train);
//
//        return TrainImageResponse.from(savedTrain.getTrainImage(), "http://localhost:8080",savedTrain.getId());




    }

    //serve train image

    @Override
    public TrainImageDataWithResource loadImageByTrainId(Long trainId) throws MalformedURLException {

        Train train = trainRepository.findById(trainId).orElseThrow(() -> new ResourceNotFoundException("Train not found!!"));
        TrainImage trainImage = train.getTrainImage();
        if (trainImage == null) {
            throw new ResourceNotFoundException("Image not found !!");
        }

        Path path = Paths.get(trainImage.getFileName());

        if(!Files.exists(path)){
            throw  new ResourceNotFoundException("Image not found !!");
        }

        UrlResource urlResource = new UrlResource(path.toUri());

        TrainImageDataWithResource trainImageDataWithResource = new TrainImageDataWithResource(trainImage, urlResource);


        return  trainImageDataWithResource;
    }
}
