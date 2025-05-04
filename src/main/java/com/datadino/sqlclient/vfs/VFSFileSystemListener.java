/*
 * VFSFileSystemListener.java
 *
 * Created on December 16, 2002, 9:33 PM
 */

package com.datadino.sqlclient.vfs;

/**
 *
 * @author  jbanes
 * @version 
 */
public interface VFSFileSystemListener 
{
    public void pathMounted(String path);
    
    public void pathUnmounted(String path);
}

