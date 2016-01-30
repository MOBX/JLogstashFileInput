/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.jlogstash;

import java.io.File;
import java.io.IOException;

/**
 * @author zxc Jan 8, 2016 5:08:35 PM
 */
public interface JniExtractor {

    /**
     * extract a JNI library to a temporary file
     * 
     * @param libname - "System.loadLibrary()"-compatible library name
     * @return the extracted file
     * @throws IOException
     */
    public File extractJni(String libname) throws IOException;
}
