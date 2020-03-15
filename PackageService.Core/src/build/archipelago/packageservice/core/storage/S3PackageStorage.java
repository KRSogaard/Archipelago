package build.archipelago.packageservice.core.storage;

import build.archipelago.common.ArchipelagoBuiltPackage;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
public class S3PackageStorage implements PackageStorage {

    private AmazonS3 s3Client;
    private String bucketName;

    public S3PackageStorage(AmazonS3 s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void upload(ArchipelagoBuiltPackage pkg, byte[] artifactBytes) {
        String keyName = getS3FileName(pkg);
        log.info("Saving build artifact to \"{}\"", keyName);

        ObjectMetadata om = new ObjectMetadata();
        om.setContentLength(artifactBytes.length);

        s3Client.putObject(
            new PutObjectRequest(bucketName, keyName, new ByteArrayInputStream(artifactBytes), om));
    }

    @Override
    public byte[] get(ArchipelagoBuiltPackage pkg) throws IOException {
        String keyName = getS3FileName(pkg);
        log.debug("Fetching build artifact from S3 \"{}\" with key \"{}\"", bucketName, keyName);
        S3Object result = s3Client.getObject(bucketName, keyName);
        try {
            return IOUtils.toByteArray(result.getObjectContent());
        } catch (AmazonServiceException exp) {
            log.error("Was not able to download the S3 file {}", keyName);
            throw exp; // This should not happen, so it is ok to throw so we can fail fast
        }
    }

    private String getS3FileName(ArchipelagoBuiltPackage pkg) {
        return pkg.toString() + ".zip";
    }
}
