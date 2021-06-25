package com.tsuki.tester;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-06-11 18:11
 **/
public class TestArray {

    @Test
    public void testArray() {
        byte[] encodedSig = new byte[10];
//
//        for (int i = 0 ;i < encodedSig.length; i++) {
//            encodedSig[i] = (byte)i;
//        }
            System.out.println(getUsersProjectRootDirectory());
//        for (byte b : encodedSig) {
//            System.out.println(b);
//        }
//
//        byte[] paddedSig = new byte[12];
//        System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);

//        for (byte b : paddedSig) {
//            System.out.println(b);
//        }
    }

    public String getUsersProjectRootDirectory() {
        String envRootDir = System.getProperty("user.dir");
        System.out.println(envRootDir);
        Path rootDir = Paths.get(".").normalize().toAbsolutePath();
        if ( rootDir.startsWith(envRootDir) ) {
            return rootDir.toString();
        } else {
            throw new RuntimeException("Root dir not found in user directory.");
        }
    }
}
