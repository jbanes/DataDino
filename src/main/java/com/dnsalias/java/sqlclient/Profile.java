/*
 * Profile.java
 *
 */

package com.dnsalias.java.sqlclient;

import java.io.*;
import java.net.*;
import java.util.*;

import com.dnsalias.java.sqlclient.sql.*;
import com.dnsalias.java.sqlclient.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class Profile 
{
    public final String name;
    
    public final String url;
    public final String prefix;
    public final String driver;
    
    public String database = null;
    public String username = null;
    public String password = null;
    public String host = null;
    public String port = null;
    public String archive = null;
    
    private Date updated = new Date(0);
    public AutoNumber autonumber = null;
    
    private Vector datatypes = new Vector();
    private Hashtable resizable = new Hashtable();
    private Hashtable alternates = new Hashtable();
    
    private Properties properties = new Properties();
    
//    private static File file = new File(System.getProperty("user.home")+File.separator+".datadino"+File.separator+"drivers");
    private static File file = new File("drivers");
    
    public Profile(String name, String prefix, String url, String driver, String archive, Date updated)
    {
        this.name = name;
        
        this.prefix = prefix;
        this.url = url;
        this.driver = driver;
        this.archive = archive;
        this.updated = updated;
    }
    
    public void setProperty(String key, String value)
    {
        properties.setProperty(key, value);
    }
    
    public String getProperty(String key)
    {
        return properties.getProperty(key);
    }
    
    public String getConnectionString()
    {
        return prefix+":"+parseURL();
    }
    
    private String parseURL()
    {
        StringBuffer buffer = new StringBuffer(url);
        int save = 0;
        
        for(int i=0; i<buffer.length(); i++)
        {
            if(buffer.charAt(i) == '$' && buffer.charAt(i+1) == '[')
            {
                StringBuffer temp = new StringBuffer();
                
                save = i;
                i+=2;
                
                while(buffer.charAt(i) != ']')
                {
                    temp.append(buffer.charAt(i));
                    i++;
                }
                
                buffer.delete(save, i+1);
                i = save;
                
                String replace = getData(temp.toString());
                buffer.insert(i, replace);
                i += replace.length();
                
                //Set it back one so that we catch the next character
                i--;
            }
        }
        
        return buffer.toString();
    }
    
    private String getData(String name)
    {
        String def = "";
        String ret = "";
        
        if(name.indexOf(':') >= 0)
        {
            def = name.substring(name.indexOf(':')+1);
            
            name = name.substring(0, name.indexOf(':'));
        }
        
        if(name.equalsIgnoreCase("database")) ret = database;
        if(name.equalsIgnoreCase("host")) ret = host;
        if(name.equalsIgnoreCase("port")) ret = port;
        
        if(ret == null || ret.trim().length() < 1) ret = def;
        
        return ret;
    }
    
    public void addDataType(String type, boolean resizable, String alternate)
    {
        datatypes.add(type.toUpperCase());
        this.resizable.put(type.toUpperCase(), resizable);
        if(alternate != null) alternates.put(type.toUpperCase(), alternate.toUpperCase());
    }
    
    public String[] getDataTypes()
    {
        String[] types = new String[datatypes.size()];
        
        for(int i=0; i<types.length; i++) types[i] = datatypes.elementAt(i).toString();
        
        return types;
    }
    
    public boolean isResizable(String datatype)
    {
        if(!datatypes.contains(datatype.toUpperCase())) return true;
        
        return ((Boolean)resizable.get(datatype.toUpperCase())).booleanValue();
    }
    
    public String getAlternate(String datatype)
    {
        if(alternates.get(datatype.toUpperCase()) == null) return datatype.toUpperCase();
        
        return alternates.get(datatype.toUpperCase()).toString();
    }
    
    public Object clone()
    {
        Profile profile = new Profile(name, prefix, url, driver, archive, updated);
        
        profile.database = database;
        profile.host = host;
        profile.port = port;
        profile.username = username;
        profile.password = password;
        profile.autonumber = autonumber;
        
        return profile;
    }
    
    public Downloader loadDriver() throws IOException
    {
        if(archive == null) return null;
        
        File archive = new File(file, this.archive.substring(this.archive.lastIndexOf('/')+1));
        Downloader loader = null;

        if(!archive.exists() && ApplicationSettings.getInstance().isMacOSX() && !ApplicationSettings.getInstance().isWebstart())
        {
            URL url = getClass().getResource("/"+this.archive);
            System.out.println("URL ===== "+url);
            loader = new Downloader(url, archive);
            
            loader.start();
            loader.join();
            
            return loader;
        }
        
        if(!archive.exists() || updated.getTime() > archive.lastModified()) 
        {
            URL url = new URL(ApplicationSettings.getInstance().getProperty("codebase")+this.archive);
            loader = new Downloader(url, archive);
            
            loader.start();
            loader.join();
        }

        return loader;
    }
    
    public ClassLoader getDriverClassLoader() throws IOException
    {
        if(archive == null) return this.getClass().getClassLoader();
        
        File archive = new File(file, this.archive.substring(this.archive.lastIndexOf('/')+1));
        URLClassLoader loader;
        
        if(!archive.exists())
        {
            Downloader dloader = loadDriver();
            
            while(!dloader.completed());
        }
        
        System.out.println(archive.toURL());
        
        loader = new URLClassLoader(new URL[]{archive.toURL()});
        
        return loader;
    }
}