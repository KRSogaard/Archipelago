package build.archipelago.packageservice.utils;

import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestUtils {
    private static final ClassLoader classLoader = TestUtils.class.getClassLoader();

    public static String readResourceFile(String fileName) throws IOException {
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            Preconditions.checkNotNull(inputStream);
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    public static byte[] readBinaryResourceFile(String fileName) throws IOException {
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            Preconditions.checkNotNull(inputStream);
            return inputStream.readAllBytes();
        }
    }

    // get file from classpath, resources folder
    public static File getFileFromResources(String fileName) {
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }
    }
}
