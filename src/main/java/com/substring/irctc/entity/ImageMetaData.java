package com.substring.irctc.entity;

import java.time.LocalDateTime;

public class ImageMetaData {

    private String fileName;
    private String fileId;
    private String contentType;
    private long fileSize;
    private LocalDateTime uploadedAt;

    public ImageMetaData() {
    }

    public ImageMetaData(String fileName, String fileId, String contentType, long fileSize, LocalDateTime uploadedAt) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
