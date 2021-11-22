package android.libraryactivity;

import org.litepal.crud.DataSupport;

//document类型对象
public class document extends DataSupport{
    private int id;
    private String fileType;
    private String fileName;
    //private String fileTime;
    private String fileStartDate;
    private String fileExpirationDate;
    private Boolean fileStatus;
    private String fileDescription;
    private String filePath; //the link file path

    public document(){
        this.fileType = "?Type?";
        this.fileName = "?Name?";
        //this.fileTime = "?Time?";
        this.fileStartDate = "?StartDate?";
        this.fileExpirationDate = "?ExpirationDate?";
        this.fileStatus = false;
        this.fileDescription = "?Description?";
        this.filePath = "?Path";
    }

    public document(String type, String name, boolean status, String startDate, String expirationDate, String description, String filePath){
        this.fileType = type;
        this.fileName = name;
        //this.fileTime = time;
        this.fileStartDate = startDate;
        this.fileExpirationDate = expirationDate;
        this.fileStatus = status;
        this.fileDescription = description;
        this.filePath = filePath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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
    public String getFileStartDate(){ return fileStartDate;}
    public void setFileStartDate(String startDate) { this.fileStartDate = startDate; }
    public String getFileExpirationDate() { return fileExpirationDate; }
    public void setFileExpirationDate(String expirationDate) { this.fileExpirationDate = expirationDate; }
    public boolean getFileStatus(){
        return fileStatus;
    }
    public void setFileStatus(boolean fileStatus) {
        this.fileStatus = fileStatus;
    }
    public String getFileDescription() {return fileDescription;}
    public void setFileDescription(String fileDescription) {this.fileDescription = fileDescription;}
    public String getFilePath() { return filePath; }
    public void setFileStatus(String filePath) { this.filePath = filePath; }









}
