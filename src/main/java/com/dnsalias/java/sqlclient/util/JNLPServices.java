/*
 * JNLPServices.java
 *
 * Created on May 11, 2002, 5:04 PM
 */

package com.dnsalias.java.sqlclient.util;

import java.io.*;
import java.net.*;
import javax.swing.*;

import javax.jnlp.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class JNLPServices
{   
    public static InputStream open() throws IOException
    {
        FileOpenService fos = null; 
        FileContents fc = null;

        try 
        { 
            fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService"); 
        } 
        catch (UnavailableServiceException e) {e.printStackTrace();} 

        if(fos != null)  fc = fos.openFileDialog(null, null);
        
        if(fc == null) return null;

        return fc.getInputStream();
    }
    
    public static boolean save(InputStream data) throws IOException
    {
        FileSaveService fos = null; 
        FileContents fc = null;

        try 
        { 
            fos = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService"); 
        } 
        catch (UnavailableServiceException e) {e.printStackTrace();} 

        if(fos != null) fc = fos.saveFileDialog(null, null, data, "");
        if(fc == null) return false;
        
        return true;
    }
    
    public static boolean showDocument(String url)
    {
        try 
        {
            BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
            return bs.showDocument(new URL(url));
        } 
        catch(UnavailableServiceException e) 
        {
            e.printStackTrace();
            return false;
        } 
        catch(MalformedURLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public static URL getCodeBase()
    {
        try 
        {
            BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
            return bs.getCodeBase();
        } 
        catch(UnavailableServiceException e) 
        {
            e.printStackTrace();
            return null;
        } 
    }
}
