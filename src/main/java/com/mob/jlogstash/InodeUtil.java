/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.jlogstash;

import java.io.IOException;

/**
 * @author zxc Jan 8, 2016 5:09:33 PM
 */
public class InodeUtil {

    static {
        try {
            NativeLoader.loadLibrary("inodeutil");
        } catch (IOException e) {
            System.err.println("can't find library inodeutil");
            System.exit(1);
        }
    }

    /**
     * Get inode of a file with the absolute path.
     * 
     * @param path path of the file
     * @return the inode of the file
     */
    public static native long getInode(String path);
}
