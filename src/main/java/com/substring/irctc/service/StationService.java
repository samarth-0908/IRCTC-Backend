package com.substring.irctc.service;

import com.substring.irctc.dto.PagedResponse;
import com.substring.irctc.dto.StationDto;
import com.substring.irctc.repositories.StationRepo;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

public interface StationService {


    StationDto createStation(StationDto stationDto);


    PagedResponse<StationDto> listStations(int page, int size, String sortBy, String sortDir);


    StationDto getById(Long id);

    StationDto update(Long id, StationDto dto);

    void delete(Long id);
}
