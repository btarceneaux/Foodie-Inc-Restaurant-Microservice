package inc.foodie.bean;

import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class S3Documents
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_PATH")
    private String filePath;

    @Column(name = "VERSION")
    private String version;

    public S3Documents( String fileName, String filePath, String version) {
        super();
        this.fileName = fileName;
        this.filePath = filePath;
        this.version = version;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    public S3Documents()
    {
        super();
    }


}