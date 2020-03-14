package build.archipelago.packageservice.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageUtil {
    private final static Pattern re = Pattern.compile("[^A-Za-z0-9]+");

    public static boolean validateHash(String name) {
        Matcher m = re.matcher(name);
        return !m.find();
    }
}
