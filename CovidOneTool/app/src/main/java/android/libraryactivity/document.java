package android.libraryactivity;

import org.litepal.crud.DataSupport;

//document类型对象
public class document extends DataSupport {
    private String fileType;
    private String fileName;
    private String fileTime;
    private String fileStatus;
    private String fileDescription;

    public document() {
        this.fileType = "?Type?";
        this.fileName = "?Name?";
        this.fileTime = "?Time?";
        this.fileStatus = "?Status?";
        this.fileDescription = "?Description?";
    }

    public document(String type, String name, String time, String status, String description) {
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

    public String getFileTime() {
        return fileTime;
    }

    public void setFileTime(String fileTime) {
        this.fileTime = fileTime;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }
}




