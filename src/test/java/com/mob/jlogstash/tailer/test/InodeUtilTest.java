/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.jlogstash.tailer.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;

import com.mob.jlogstash.InodeUtil;

/**
 * Test case for getting inode.
 * 
 * @author zxc Jan 8, 2016 4:51:07 PM
 */
public class InodeUtilTest {

    public static long getInodeByCmd(String path) {
        try {
            String[] fullCmd = { "ls", "-i", path };

            ProcessBuilder builder = new ProcessBuilder(fullCmd);
            builder.redirectErrorStream(true);

            Process p = builder.start();

            BufferedReader bfInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String result = bfInput.readLine();

            // line include the target process id
            p.waitFor();

            int spaceIndex = result.indexOf(" ");
            return Long.valueOf(result.substring(0, spaceIndex));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Test
    public void testGetInode() throws Exception {
        String filePath = "/etc/hosts";

        long inode = InodeUtil.getInode(filePath);
        long inodeByCmd = getInodeByCmd(filePath);

        Assert.assertEquals(inodeByCmd, inode);
    }

    @Test
    public void testPerformance() throws Exception {
        String filePath = "/etc/hosts";
        int round = 1000;

        long start = System.currentTimeMillis();
        long inode = -1;
        for (int i = 0; i < round; i++) {
            inode = InodeUtil.getInode(filePath);
        }
        long end = System.currentTimeMillis();

        System.out.println("inode=" + inode + ", time=" + (end - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < round; i++) {
            inode = getInodeByCmd(filePath);
        }
        end = System.currentTimeMillis();

        System.out.println("inode=" + inode + ", time=" + (end - start));
    }
}
