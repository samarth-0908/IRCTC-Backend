package com.substring.irctc.service.impl;
import com.substring.irctc.dto.BookingPassengerDto;
import com.substring.irctc.dto.BookingRequest;
import com.substring.irctc.dto.BookingResponse;
import com.substring.irctc.dto.StationDto;
import com.substring.irctc.entity.*;
import com.substring.irctc.exceptions.ResourceNotFoundException;
import com.substring.irctc.repositories.*;
import com.substring.irctc.service.BookingService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class BookingServiceImpl implements BookingService {


    private BookingRepo bookingRepo;
    private BookingPassengerRepo bookingPassengerRepo;
    private UserRepo userRepo;
    private TrainScheduleRepo trainScheduleRepo;
    private TrainRepository trainRepository;
    private StationRepo stationRepo;
    private ModelMapper modelMapper;
    private TrainSeatRepo trainSeatRepo;

    public BookingServiceImpl(BookingRepo bookingRepo, BookingPassengerRepo bookingPassengerRepo, UserRepo userRepo, TrainScheduleRepo trainScheduleRepo, TrainRepository trainRepository, StationRepo stationRepo, ModelMapper modelMapper, TrainSeatRepo trainSeatRepo) {
        this.bookingRepo = bookingRepo;
        this.bookingPassengerRepo = bookingPassengerRepo;
        this.userRepo = userRepo;
        this.trainScheduleRepo = trainScheduleRepo;
        this.trainRepository = trainRepository;
        this.stationRepo = stationRepo;
        this.modelMapper = modelMapper;
        this.trainSeatRepo = trainSeatRepo;
    }

    @Override
    @Transactional
    public synchronized BookingResponse createBooking(BookingRequest bookingRequest) {

        User user = userRepo.findById(bookingRequest.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + bookingRequest.getUserId()));
        TrainSchedule trainSchedule = trainScheduleRepo.findById(bookingRequest.getTrainScheduleId()).orElseThrow(() -> new ResourceNotFoundException("Train Schedule not found with id: " + bookingRequest.getTrainScheduleId()));

        Station sourceStation = stationRepo.findById(bookingRequest.getSourceStationId()).orElseThrow(() -> new ResourceNotFoundException("Source station not found with id: " + bookingRequest.getSourceStationId()));

        Station destinationStation = stationRepo.findById(bookingRequest.getDestinationStationId()).orElseThrow(() -> new ResourceNotFoundException("destination station not found with id: " + bookingRequest.getDestinationStationId()));

        List<TrainSeat> coaches = trainSchedule.getTrainSeats();

        // it will sort coaches based on trainSeatOrder
        coaches.sort((s1,s2)-> s1.getTrainSeatOrder()- s2.getTrainSeatOrder());

        //selected coaches of same type
        List<TrainSeat> selectedCoaches = coaches.stream().filter(trainSeat -> trainSeat.getCoachType() == bookingRequest.getCoachType()).toList();

        //total number of requested seats
        int totalRequestSeat = bookingRequest.getPassengers().size();


        //kis coach se book karenge
        TrainSeat coachToBookSeat = null;
        for(TrainSeat coach: selectedCoaches) {
            if(coach.isSeatAvailable(totalRequestSeat)){

                coachToBookSeat = coach;
                break;
            }
        }

        //coachToBookSeat
        if(coachToBookSeat == null) {
            throw new ResourceNotFoundException("No seats available in this coach");
        }





        //book seats
        Booking booking = new Booking();
        booking.setPnr(UUID.randomUUID().toString());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setDestinationStation(destinationStation);
        booking.setSourceStation(sourceStation);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setJourneyDate(trainSchedule.getRunDate());
        booking.setTrainSchedule(trainSchedule);
        booking.setUser(user);

        //Total Fare
        booking.setTotalFare(new BigDecimal(totalRequestSeat * coachToBookSeat.getPrice()));

        //Payment
        Payment payment = new Payment();
        payment.setAmount(booking.getTotalFare());
        payment.setBooking(booking);
        payment.setCreatedAt(null);
        payment.setPaymentMethod(null);
        payment.setTransactionId(null);
        payment.setPaymentStatus(PaymentStatus.NOT_PAID);

        booking.setPayment(payment);


        //set Passengers

        List<BookingPassenger> bookingPassengers = new ArrayList<>();

        for(BookingPassengerDto bookingPassengerDto: bookingRequest.getPassengers()) {
            BookingPassenger passenger = modelMapper.map(bookingPassengerDto, BookingPassenger.class);
            passenger.setBooking(booking);
            passenger.setTrainSeat(coachToBookSeat);
            passenger.setSeatNumber(coachToBookSeat.getSeatNumberToAssign()+ "");
            coachToBookSeat.setSeatNumberToAssign(coachToBookSeat.getSeatNumberToAssign() + 1);
            coachToBookSeat.setAvailableSeats(coachToBookSeat.getAvailableSeats()-1);

            bookingPassengers.add(passenger);

        }

        booking.setPassengers(bookingPassengers);


        Booking savedBooking = bookingRepo.save(booking);
        trainSeatRepo.save(coachToBookSeat);

        //Going to create response
        BookingResponse bookingResponse = new BookingResponse();

        bookingResponse.setBookingId(savedBooking.getId());
        bookingResponse.setPnr(savedBooking.getPnr());
        bookingResponse.setTotalFare(savedBooking.getTotalFare());
        bookingResponse.setStatus(savedBooking.getBookingStatus());
        bookingResponse.setSourceStation(modelMapper.map(sourceStation, StationDto.class));
        bookingResponse.setDestinationStation(modelMapper.map(destinationStation, StationDto.class));
        bookingResponse.setJourneyDate(trainSchedule.getRunDate());
        bookingResponse.setPaymentStatus(savedBooking.getPayment().getPaymentStatus());

        bookingResponse.setPassengers(
                savedBooking.getPassengers().stream().map(
                        passenger ->{
                            BookingPassengerDto bookingPassengerDto = modelMapper.map(passenger, BookingPassengerDto.class);
                            bookingPassengerDto.setCoachId(passenger.getTrainSeat().getId() + "");
                            return bookingPassengerDto;
                        }
                ).toList()
        );


        TrainRoute sourceRoute = trainSchedule.getTrain().getRoutes().stream().filter(
                route -> route.getStation().getId().equals(sourceStation.getId())).findFirst().get();

        bookingResponse.setDepartureTime(sourceRoute.getDepartureTime());
        bookingResponse.setArrivalTime(sourceRoute.getArrivalTime());



        return bookingResponse;
    }

}
