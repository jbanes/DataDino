/*
 * VFSException.java
 *
 * Created on December 12, 2002, 10:54 PM
 */

package com.datadino.sqlclient.vfs;

/**
 *
 * @author  jbanes
 * @version 
 */
public class VFSException extends Exception
{
    private String path;
    
    public VFSException(String message)
    {
        this(message, null);
    }
    
    public VFSException(String message, String path) 
    {
        super(message);
        
        this.path = path;
    }
    
    public String getPath()
    {
        return path;
    }
}
