/*
 * VFSFile.java
 *
 * Created on December 11, 2002, 11:07 PM
 */

package com.datadino.sqlclient.vfs;

import java.io.*;
import java.net.*;

import javax.swing.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public interface VFSFile 
{
    public String getName();
    
    public void setName(String name);
    
    public boolean isDirectory();
    
    public boolean createFile(String name);
    
    public boolean createDirectory(String name);
    
    public void refresh();
    
    public URL getFileLocation() throws MalformedURLException;
    
    public InputStream getFileInputStream() throws IOException;
    
    public OutputStream getFileOutputStream() throws IOException;
    
    public VFSFile getParent();
    
    public int getFileCount();
    
    public VFSFile getChild(int index);
    
    public VFSFile getChild(String name);
    
    public String getPath();
    
    public Icon getIcon();
    
    public boolean renameFile(String name);
    
    public boolean delete();
}

