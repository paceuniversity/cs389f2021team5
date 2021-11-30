package android.libraryactivity;

import org.litepal.crud.DataSupport;

//document类型对象
public class document extends DataSupport{
    private int id;
    private String fileType;
    private String fileName;
    private String startDate;
    private String expirationDate;
    private Boolean fileStatus;
    private String fileDescription;
    private String filePath;

    public document(){
        this.fileType = "?Type?";
        this.fileName = "?Name?";
        this.startDate = "?startDate?";
        this.expirationDate = "?expirationDate?";
        this.fileStatus = false;
        this.fileDescription = "?Description?";
        this.filePath = "?Path?";
    }

    public document(String type, String name, boolean status, String startDate,String expirationDate, String description,String path){
        this.fileType = type;
        this.fileName = name;
        this.startDate = startDate;
        this.expirationDate = expirationDate;
        this.fileStatus = status;
        this.fileDescription = description;
        this.filePath = path;
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
    public boolean getFileStatus(){
        return fileStatus;
    }
    public void setFileStatus(boolean fileStatus) {
        this.fileStatus = fileStatus;
    }
    public String getFileDescription() {return fileDescription;}
    public void setFileDescription(String fileDescription) {this.fileDescription = fileDescription;}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
