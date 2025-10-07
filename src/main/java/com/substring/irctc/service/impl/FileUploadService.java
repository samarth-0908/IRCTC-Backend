package com.substring.irctc.service.impl;


import com.substring.irctc.entity.ImageMetaData;
import com.substring.irctc.helper.Helper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService implements com.substring.irctc.service.FileUploadService {

    @Value("${file.upload.folder}")
    private String folder;

    @Override
    public ImageMetaData upload(MultipartFile file) throws IOException {
        //process the file
        String originalFilename = file.getOriginalFilename();
        System.out.println(originalFilename);

        InputStream inputStream = file.getInputStream();



        //create folder if not exists
        if(!Files.exists(Paths.get(folder))){
            System.out.println("creating folder");
            Files.createDirectories(Paths.get(folder));
        }

        String fileNameWithPath = Helper.getFileName(folder,file.getOriginalFilename());



        //lets upload the file

        Files.copy(file.getInputStream(),Paths.get(fileNameWithPath), StandardCopyOption.REPLACE_EXISTING);

        ImageMetaData metaData = new ImageMetaData();
        metaData.setFileId(UUID.randomUUID().toString());
        metaData.setFileName(fileNameWithPath);
        metaData.setFileSize(file.getSize());
        metaData.setContentType(file.getContentType());

        return metaData;
    }
}
