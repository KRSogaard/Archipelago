package build.archipelago.kauai.core.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;

@Slf4j
public class S3PackageStorage implements PackageStorage {

    private AmazonS3 s3Client;
    private String bucketName;

    public S3PackageStorage(AmazonS3 s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void upload(String packageName, String version, String hash, byte[] artifactBytes) {
        String keyName = getS3FileName(packageName, version, hash);
        log.info("Saving build artifact to \"{}\"", keyName);


        ObjectMetadata om = new ObjectMetadata();
        om.setContentLength(artifactBytes.length);

        PutObjectResult result = s3Client.putObject(new PutObjectRequest(
                bucketName,
                getS3FileName(packageName, version, hash),
                new ByteArrayInputStream(artifactBytes), om));
    }

    private String getS3FileName(String packageName, String version, String hash) {
        return packageName + "-" + version + "-" + hash + ".zip";
    }
}
