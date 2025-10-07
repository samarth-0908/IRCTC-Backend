package com.substring.irctc.exceptions;

import com.substring.irctc.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.rmi.MarshalledObject;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //pure project ke liye: hai
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchException(NoSuchElementException exception){

        ErrorResponse response = new ErrorResponse("Train not found !! "+ exception.getMessage(), "404",false);

        ResponseEntity<ErrorResponse> responseResponseEntity = new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        return responseResponseEntity;

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception){

        ErrorResponse response = new ErrorResponse(exception.getMessage(), "404",false);

        ResponseEntity<ErrorResponse> responseResponseEntity = new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        return responseResponseEntity;

    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException (SQLIntegrityConstraintViolationException exception) {


        String message = exception.getMessage().contains("Duplicate entry") ? "You are trying to provide the fields that are already in database": exception.getMessage();
        ErrorResponse response = new ErrorResponse(message, "400",false);

        ResponseEntity<ErrorResponse> responseResponseEntity = new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        return responseResponseEntity;
    }


    //handle method argument not  valid exception

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){

        Map<String ,String> errorResponse = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach(error ->{
            String errorMessage = error.getDefaultMessage();
            String field = ((FieldError)error).getField(); //typecasted error into fielderror so that we can use methods of fielderror since here we want the field of error
            errorResponse.put(field, errorMessage);
        });


        ResponseEntity<Map<String,String>> error = new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        return error;

    }


}
