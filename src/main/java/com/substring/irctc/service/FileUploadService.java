package com.substring.irctc.service;

import com.substring.irctc.dto.TrainRouteDto;
import com.substring.irctc.entity.ImageMetaData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploadService {

     ImageMetaData upload(MultipartFile file) throws IOException;

}
