#include <jni.h>
#include <stdio.h>
#include <sys/stat.h>
#include "com_mob_jlogstash_InodeUtil.h"

/*
 * Class:     com_mob_jlogstash_InodeUtil
 * Method:    getInode
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_mob_jlogstash_InodeUtil_getInode
(JNIEnv *env, jclass cls, jstring path) {
    jlong inode = -1;
    const char *cpath = env->GetStringUTFChars(path, 0);
    struct stat statbuf;
    if (stat(cpath, &statbuf) != -1) {
        inode = (jlong)statbuf.st_ino;
    }
    
    env->ReleaseStringUTFChars(path, cpath);
    
    return inode;
}