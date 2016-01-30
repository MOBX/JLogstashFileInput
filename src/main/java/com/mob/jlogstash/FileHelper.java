/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.jlogstash;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lamfire.utils.IOUtils;
import com.lamfire.utils.StringUtils;
import com.mob.jlogstash.tailer.*;

/**
 * @author zxc Jan 8, 2016 5:09:33 PM
 */
public class FileHelper {

    private static final Logger             logger    = LoggerFactory.getLogger(FileHelper.class);
    private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(10);

    public interface ILineDo {

        void work(String data);
    }

    public interface ILinePositionDo {

        long getPosition(String fileName);

        void savePosition(String fileName, Long position);

        void work(String data);
    }

    /**
     * 对监听的文件进行实时解析
     * 
     * @param ILinePositionDo
     * @param fileNames
     * @throws Exception
     */
    public void watch(final ILinePositionDo ido, String... fileNames) throws Exception {
        if (fileNames == null || fileNames.length == 0) {
            return;
        }
        for (final String fileName : fileNames) {
            File targetFile = null;
            try {
                targetFile = new File(fileName);
            } catch (Exception e) {
                logger.error("file read error,name " + fileName);
            }
            if (targetFile == null) {
                logger.error("file not exit,name " + fileName);
                continue;
            }

            long _position = ido.getPosition(fileName);
            logger.info("now watch fileName={},position={}", fileName, _position);
            Tailer tailer = TailerHelper.createTailer(targetFile, new AbstractTailerListener() {

                @Override
                public void handle(String line, long position, long lastModified) {
                    logger.info("file={},position={},lastModified={}",
                                new Object[] { fileName, position, lastModified });
                    ido.work(line);
                    ido.savePosition(fileName, position);
                }
            }, _position);
            scheduled.submit(new Thread(tailer));
        }
    }

    /**
     * 对监听的文件进行实时解析
     * 
     * @param ILineDo
     * @param fileNames
     * @throws Exception
     */
    public void watch(final ILineDo ido, String... fileNames) throws Exception {
        if (fileNames == null || fileNames.length == 0) {
            return;
        }
        for (final String fileName : fileNames) {
            File targetFile = null;
            try {
                targetFile = new File(fileName);
            } catch (Exception e) {
                logger.error("file read error,name " + fileName);
            }
            if (targetFile == null) {
                logger.error("file not exit,name " + fileName);
                continue;
            }

            long _position = 0l;
            logger.info("now watch fileName={},position={}", fileName, _position);
            Tailer tailer = TailerHelper.createTailer(targetFile, new AbstractTailerListener() {

                @Override
                public void handle(String line, long position, long lastModified) {
                    logger.info("file={},position={},lastModified={}",
                                new Object[] { fileName, position, lastModified });
                    ido.work(line);
                }
            }, _position);
            scheduled.submit(new Thread(tailer));
        }
    }

    public void save(String line, String fileName) {
        try {
            IOUtils.write(new FileOutputStream(new File(fileName), true), line + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取resource文件
     * 
     * @param fileName
     * @param ido
     * @throws Exception
     */
    public void readSourceFile(String fileName, ILineDo ido) throws Exception {
        URL filePath = Thread.currentThread().getContextClassLoader().getResource(fileName);
        readFile(filePath.getFile(), ido);
    }

    /**
     * 读取特定路径下文件,以不消耗内存为前提
     * 
     * @param filePath
     * @param ido
     * @throws Exception
     */
    public void readFile(String filePath, ILineDo ido) throws Exception {
        LineIterator it = FileUtils.lineIterator(new File(filePath), "UTF-8");
        try {
            while (it.hasNext()) {
                String data = it.nextLine();
                if (StringUtils.isBlank(data)) break;
                try {
                    ido.work(data);
                } catch (Exception e) {
                    logger.error("ido.work error!", e);
                    continue;
                }
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
    }
}
