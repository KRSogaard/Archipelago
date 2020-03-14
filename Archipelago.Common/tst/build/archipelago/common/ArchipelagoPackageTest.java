package build.archipelago.common;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ArchipelagoPackageTest {
    String name = "Package1";
    String version = "1.2";
    String hash;

    @Before
    public void setUp() {
        hash = UUID.randomUUID().toString();
    }

    @Test
    public void testParseNameVersion() {
        String testString = name + "-" + version;
        ArchipelagoPackage pkg = ArchipelagoPackage.parse(testString);
        assertEquals(name, pkg.getName());
        assertEquals(version, pkg.getVersion());
    }

    @Test
    public void testParseNameVersionWithTestVersion() {
        String version = "Awesome Build";
        String testString = name + "-" + version;

        ArchipelagoPackage pkg = ArchipelagoPackage.parse(testString);
        assertEquals(name, pkg.getName());
        assertEquals(version, pkg.getVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseNameWithoutVersion() {
        ArchipelagoPackage.parse(name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseNameWithoutVersionButHasSeparator() {
        String testString = name + "-";
        ArchipelagoPackage.parse(testString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithoutNameHasVersionButHasSeparator() {
        String testString = "-" + version;
        ArchipelagoPackage.parse(testString);
    }

    @Test
    public void testConstructorWithNameAndVersion() {
        ArchipelagoPackage pkg = new ArchipelagoPackage(name, version);
        assertEquals(name + "-" + version, pkg.toString());
    }

}