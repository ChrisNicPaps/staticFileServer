import java.io.Serializable;

public class FileEvent implements Serializable {

    public FileEvent() {
    }

    private static final long serialVersionUID = 1L;

    private String destinationDirectory;
    private String sourceDirectory;
    private String filename;
    private boolean fileType;
    private long fileSize;
    private byte[] fileData;
    private String status;
    private int remainder;

    public boolean isFileTypeDir() {
        return fileType;
    }

    public void setFileType(boolean fileType) {
        this.fileType = fileType;
    }

    public String getDestinationDirectory() {
        return destinationDirectory;
    }

    public void setDestinationDirectory(String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public int getRemainder() {
        return remainder;
    }

    public void setRemainder(int remainder) {
        this.remainder = remainder;
    }
}
