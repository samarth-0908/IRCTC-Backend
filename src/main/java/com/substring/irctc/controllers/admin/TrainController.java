package com.substring.irctc.controllers.admin;

import com.substring.irctc.config.AppConstants;
import com.substring.irctc.dto.PagedResponse;
import com.substring.irctc.dto.TrainDTO;
import com.substring.irctc.dto.TrainImageDataWithResource;
import com.substring.irctc.dto.TrainImageResponse;
import com.substring.irctc.entity.TrainImage;
import com.substring.irctc.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController("adminTrainController")
@RequestMapping("/admin/trains")
public class TrainController {


    private TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    //create
    @Operation(
            summary = "create train",
            description = "This api create new trains"
    )
    @PostMapping
    public ResponseEntity<TrainDTO> createTrain(@RequestBody TrainDTO trainDTO) {

//        System.out.println(trainDTO.getNumber());
//        System.out.println(trainDTO.getName());
//        System.out.println(trainDTO.getSourceStation().getId());

        TrainDTO dto = trainService.createTrain(trainDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);

    }

    //list
    @Operation(
            summary = "get all trains",
            description = "This api get all trains"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request successful"),
            @ApiResponse(responseCode = "404", description = "train not found")
    })
    @GetMapping
    public PagedResponse<TrainDTO> getAllTrains(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir
    ) {
        PagedResponse<TrainDTO> trainDto = trainService.    getAllTrains(page, size, sortBy, sortDir);
        return trainDto;
    }

    //get detail
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request successful"),
            @ApiResponse(responseCode = "404", description = "train not found"),
            @ApiResponse(responseCode = "500", description = "server error")
    })
    @Operation(
            summary = "get train by id",
            description = "This api get train by id"
    )
    @GetMapping("/{id}")
    public  ResponseEntity<TrainDTO> getTrainById( @Parameter(description = "Id of train to get details") @PathVariable Long id) {
       TrainDTO dto =  trainService.getTrainById(id);
       return new ResponseEntity<>(dto,HttpStatus.OK);
    }

    //update train
    @Operation(
            summary = "update train",
            description = "This api update train"
    )
    @PutMapping("/{id}")
    public ResponseEntity<TrainDTO> updateTrain(
            @PathVariable Long id,
            @RequestBody TrainDTO dto
    ) {
        TrainDTO trainDTO = trainService.updateTrain(id, dto);
        return new ResponseEntity<>(trainDTO,HttpStatus.OK);
    }

    //delete train
    @Operation(
            summary = "delete train",
            description = "This api delete train"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(
            @PathVariable Long id
    ){
        trainService.deleteTrain(id);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //upload train Image

    @PostMapping("/upload/{id}")
    public TrainImageResponse upload(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) throws IOException {

        return trainService.upload(image,id);
    }

    //serve train image
    @GetMapping("/{trainId}/image")
    public ResponseEntity<Resource> serveTrainImage(
            @PathVariable("trainId") Long trainId
    ) throws MalformedURLException {

        TrainImageDataWithResource trainimageDataWithResource = trainService.loadImageByTrainId(trainId);

        TrainImage trainImage = trainimageDataWithResource.trainImage();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(trainImage.getFileType())) //giving browser what kind of content it is
                .body(trainimageDataWithResource.resource());



    }




}
