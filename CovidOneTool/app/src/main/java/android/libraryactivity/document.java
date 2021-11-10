package android.libraryactivity;

import org.litepal.crud.DataSupport;

//document类型对象
public class document extends DataSupport{
    private String fileType;
    private String fileName;
    private String fileTime;
    private Boolean fileStatus;
    private String fileDescription;

    public document(){
        this.fileType = "?Type?";
        this.fileName = "?Name?";
        this.fileTime = "?Time?";
        this.fileStatus = false;
        this.fileDescription = "?Description?";
    }

    public document(String type, String name, boolean status, String time, String description){
        this.fileType = type;
        this.fileName = name;
        this.fileTime = time;
        this.fileStatus = status;
        this.fileDescription = description;
    }
    public String getFileType() {
        return fileType;
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
    public boolean getFileStatus(){
        return fileStatus;
    }
    public void setFileStatus(boolean fileStatus) {
        this.fileStatus = fileStatus;
    }
    public String getFileDescription() {return fileDescription;}
    public void setFileDescription(String fileDescription) {this.fileDescription = fileDescription;}








}
