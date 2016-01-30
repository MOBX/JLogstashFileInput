/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.jlogstash;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zxc Jan 8, 2016 5:09:02 PM
 */
public class DefaultJniExtractor implements JniExtractor {

    private static final Logger logger     = LoggerFactory.getLogger(DefaultJniExtractor.class);

    private static final String LIB_PATH   = "lib/";
    /**
     * this is where JNI libraries are extracted to.
     */
    private File                outputFile = null;

    /**
     * Gets the working directory to use for jni extraction.
     * <p>
     * Attempts to create it if it doesn't exist.
     * 
     * @return jni working dir
     * @throws IOException if there's a problem creating the dir
     */
    public File getJniFilePath(String filename) throws IOException {
        if (outputFile == null) {
            // Split filename to prexif and suffix (extension)
            String prefix = "";
            String suffix = null;
            String[] parts;
            if (filename != null) {
                parts = filename.split("\\.", 2);
                prefix = parts[0];
                suffix = (parts.length > 1) ? "." + parts[parts.length - 1] : null;
            }

            // Check if the filename is okay
            if (filename == null || prefix.length() < 3) {
                throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
            }

            outputFile = File.createTempFile(prefix, suffix);
            outputFile.deleteOnExit();
            logger.debug("Initialised JNI library to '" + outputFile + "'");
        }
        return outputFile;
    }

    /**
     * extract a JNI library from the classpath
     * 
     * @param libname - System.loadLibrary() - compatible library name
     * @return the extracted file
     * @throws IOException
     */
    public File extractJni(String libname) throws IOException {
        String mappedlib = System.mapLibraryName(libname);

        /*
         * on darwin, the default mapping is to .jnilib; but we use .dylibs so that library interdependencies are
         * handled correctly. if we don't find a .jnilib, try .dylib instead.
         */
        if (mappedlib.endsWith(".dylib")) {
            if (this.getClass().getClassLoader().getResource(LIB_PATH + mappedlib) == null) mappedlib = mappedlib.substring(0,
                                                                                                                            mappedlib.length() - 6)
                                                                                                        + ".jnilib";
        }

        return extractResource(LIB_PATH + mappedlib, mappedlib);
    }

    /**
     * extract a resource to the tmp dir (this entry point is used for unit testing)
     * 
     * @param resourcename the name of the resource on the classpath
     * @param outputname the filename to copy to (within the tmp dir)
     * @return the extracted file
     * @throws IOException
     */
    File extractResource(String resourcename, String outputname) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcename);
        if (in == null) throw new IOException("Unable to find library " + resourcename + " on classpath");
        File outfile = getJniFilePath(outputname);
        logger.debug("Extracting '" + resourcename + "' to '" + outfile.getAbsolutePath() + "'");
        OutputStream out = new FileOutputStream(outfile);
        copy(in, out);
        out.close();
        in.close();
        return outfile;
    }

    /**
     * copy an InputStream to an OutputStream.
     * 
     * @param in InputStream to copy from
     * @param out OutputStream to copy to
     * @throws IOException if there's an error
     */
    static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] tmp = new byte[8192];
        int len = 0;
        while (true) {
            len = in.read(tmp);
            if (len <= 0) {
                break;
            }
            out.write(tmp, 0, len);
        }
    }
}
