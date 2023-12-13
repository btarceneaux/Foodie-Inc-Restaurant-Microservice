package inc.foodie.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3StorageService
{
    @Value("${AWS-BUCKETNAME}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException
    {
        File convertedFile = new File(file.getOriginalFilename());
        try(FileOutputStream fos = new FileOutputStream(convertedFile))
        {
            fos.write(file.getBytes());
        }
        catch (IOException e)
        {
            System.out.println("Error converting multipart file to file : " + e);
        }

        return convertedFile;
    }
    public String uploadFile(MultipartFile file) throws IOException
    {
        File fileObject = convertMultiPartFileToFile(file);
        s3Client.putObject(new PutObjectRequest(bucketName, file.getOriginalFilename().replace(' ', '_'), fileObject));
        fileObject.delete();

        return file.getOriginalFilename().replace(' ', '_');
    }

    public byte[] downloadFile(String fileName)
    {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try
        {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return null;
    }

    public String deleteFile(String filename)
    {
        s3Client.deleteObject(bucketName, filename);

        return filename + " was successfully deleted.";
    }
}