/*
 * Downloader.java
 *
 * Created on May 11, 2002, 7:24 PM
 */

package com.dnsalias.java.sqlclient.util;

import java.io.*;
import java.net.*;

public class Downloader implements Runnable
{
    private URL url;
    private File file;
    
    private int length = -1;
    private int progress = 0;
    
    private Thread thread = new Thread(this);
    private boolean stop = true;
    
    public Downloader(URL url, File destination) 
    {
        this.url = url;
        this.file = destination;
    }

    public void run()
    {
        URLConnection connection;
        InputStream in = null;
        OutputStream out = null;
        
        try
        {
            connection = url.openConnection();
            in = connection.getInputStream();
            out = new FileOutputStream(file);

            byte[] data = new byte[4096];
            int total;
            
            length = connection.getContentLength();
            
            while(!stop && (total = in.read(data)) > 0)
            {
                out.write(data, 0, total);
                progress += total;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            stop = true;
        }
        
        try
        {
            in.close();
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        if(stop)
        {
            try
            {
               if(file.exists()) file.delete(); 
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public void start()
    {
        stop = false;
        thread.start();
    }
    
    public void join()
    {
        try
        {
            thread.join();
        }
        catch(InterruptedException e) {e.printStackTrace();}
    }
    
    public void cancel()
    {
        stop = true;
    }
    
    public int getLength()
    {
        return length;
    }
    
    public int getProgress()
    {
        return progress;
    }
    
    public int getProgressInPercent()
    {
        return (int)((double)progress/length*100);
    }
    
    public boolean completed()
    {
        return (!thread.isAlive() && progress == length);
    }
    
    public boolean stopped()
    {
        return (!thread.isAlive() && stop);
    }
    
}