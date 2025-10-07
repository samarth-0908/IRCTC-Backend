package com.substring.irctc.service.impl;
import com.substring.irctc.dto.PagedResponse;
import com.substring.irctc.dto.StationDto;
import com.substring.irctc.entity.Station;
import com.substring.irctc.exceptions.ResourceNotFoundException;
import com.substring.irctc.repositories.StationRepo;
import com.substring.irctc.service.StationService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class StationServiceImpl implements StationService {

    private StationRepo stationRepo;

    private ModelMapper modelMapper;

    public StationServiceImpl(StationRepo stationRepo, ModelMapper modelMapper) {
        this.stationRepo = stationRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public StationDto createStation(StationDto stationDto) {
        Station station = modelMapper.map(stationDto, Station.class);
        Station savedStation = stationRepo.save(station);

        return modelMapper.map(savedStation, StationDto.class);

    }

    @Override
    public PagedResponse<StationDto> listStations(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.trim().toLowerCase().equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Station> stationPage = stationRepo.findAll(pageable);

        Page<StationDto> stationDTOPage = stationPage.map(station -> modelMapper.map(station, StationDto.class));
        return PagedResponse.fromPage(stationDTOPage);
    }

    @Override
    public StationDto getById(Long id) {
        Station station = stationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("station not found with given ID !!"));
        return modelMapper.map(station, StationDto.class);


    }

    @Override
    public StationDto update(Long id, StationDto dto) {

        Station station = stationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("station not found with given ID !!"));
        //update details

        station.setName(dto.getName());
        station.setCity(dto.getCity());
        station.setCode(dto.getCode());
        station.setState(dto.getState());

        Station updatedStation = stationRepo.save(station);

        return modelMapper.map(updatedStation, StationDto.class);


    }

    @Override
    public void delete(Long id) {
        Station station = stationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("station not found with given ID !!"));

        stationRepo.delete(station);

    }
}
