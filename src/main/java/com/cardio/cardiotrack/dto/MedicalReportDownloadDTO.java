package com.cardio.cardiotrack.dto;

public class MedicalReportDownloadDTO {

    private String fileName;
    private String contentType;
    private byte[] fileData;

    public MedicalReportDownloadDTO(
            String fileName,
            String contentType,
            byte[] fileData) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getFileData() {
        return fileData;
    }
}