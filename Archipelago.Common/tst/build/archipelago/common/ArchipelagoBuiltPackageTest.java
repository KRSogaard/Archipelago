package build.archipelago.common;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ArchipelagoBuiltPackageTest {
    String name = "Package1";
    String version = "1.2";
    String hash;

    @Before
    public void setUp() {
        hash = UUID.randomUUID().toString();
    }

    @Test
    public void testParseNameVersionHash() {
        String testString = name + "-" + version + ":" + hash;
        ArchipelagoBuiltPackage pkg = ArchipelagoBuiltPackage.parse(testString);
        assertEquals(name, pkg.getName());
        assertEquals(version, pkg.getVersion());
        assertEquals(hash, pkg.getHash());
    }

    @Test
    public void testConstructorWithNameAndVersionAndHash() {
        ArchipelagoPackage pkg = new ArchipelagoBuiltPackage(name, version, hash);
        assertEquals(name + "-" + version + ":" + hash, pkg.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithoutName() {
        new ArchipelagoBuiltPackage(null, version, hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() {
        new ArchipelagoBuiltPackage("", version, hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithoutVersion() {
        new ArchipelagoBuiltPackage(name,null, hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyVersion() {
        new ArchipelagoBuiltPackage(name,"", hash);
    }

}