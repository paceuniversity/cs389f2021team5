package com.example.covid_onetool;

import org.litepal.crud.DataSupport;

//document类型对象
public class document {
    private String fileType;
    private String fileName;
    private String fileTime;
    private String fileStatus;

    public document(String type, String name, String time, String status){
        this.fileType = type;
        this.fileName = name;
        this.fileTime = time;
        this.fileStatus = status;
    }
    public String getFileType() {
        return fileStatus;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileTime(){
        return fileTime;
    }
    public void setFileTime(String fileTime) {
        this.fileTime = fileTime;
    }
    public String getFileStatus(){
        return fileStatus;
    }
    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }








}
