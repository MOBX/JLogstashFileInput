/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.jlogstash.tailer.test;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lamfire.utils.PropertiesUtils;
import com.mob.jlogstash.tailer.*;

/**
 * @author zxc Jan 8, 2016 3:24:01 PM
 */
public class FileWatchMain {

    private static final Logger logger = LoggerFactory.getLogger(FileWatchMain.class);

    private static Properties   init_pro;
    private static String       fileName;

    static {
        init_pro = PropertiesUtils.load("input.properties", FileWatchMain.class);
        fileName = init_pro.getProperty("path");
    }

    public static void main(String[] args) {
        File targetFile = null;
        try {
            targetFile = new File(fileName);
        } catch (Exception e) {
            logger.error("file error,name " + fileName);
        }
        if (targetFile == null) {
            logger.error("file not exit,name " + fileName);
        }
        logger.error("file name " + fileName);

        TailerListener taiListener = new Listener1();
        Tailer tailer = TailerHelper.createTailer(targetFile, taiListener, 0);
        Thread thread = new Thread(tailer);
        thread.start();
    }

    static class Listener1 extends AbstractTailerListener {

        @Override
        public void handle(String line, long position, long lastModified) {
            logger.error("line={},position={},lastModified={}", new Object[] { line, position, lastModified });
        }
    }
}
