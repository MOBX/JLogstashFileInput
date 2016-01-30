/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.jlogstash.tailer;

/**
 * 监听器父类
 * 
 * @author zxc Jan 11, 2016 4:05:01 PM
 */
public abstract class AbstractTailerListener implements TailerListener {

    @Override
    public void init(Tailer tailer) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void fileNotFound() {

    }

    @Override
    public void fileRotated() {

    }

    @Override
    public void handle(String line, long position, long lastModified) {

    }

    @Override
    public void handle(Exception ex) {

    }
}
