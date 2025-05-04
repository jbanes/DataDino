/*
 * Settings.java
 *
 * Created on May 30, 2002, 8:20 PM
 */

package com.dnsalias.java.sqlclient.util;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class ApplicationSettings extends Properties
{
    public static final boolean JAVALOBBY = false;
    
    private static final File settingsFile = new File(System.getProperty("user.home")+File.separator+".datadino"+File.separator+"datadino.properties");
    private ArrayList savekeys = new ArrayList();
    
    private static ApplicationSettings instance = new ApplicationSettings();
    
    private ApplicationSettings() 
    {
        try
        {
            boolean webstart = (System.getProperty("javawebstart.version") != null);

            setProperty("webstart-enabled", new Boolean(webstart).toString());

            if(webstart) setProperty("codebase", JNLPServices.getCodeBase().toString());
            else setProperty("codebase", new File(System.getProperty("user.dir")).toURL().toString());
            
            System.out.println(settingsFile);
            
            if(settingsFile.exists())
            {
                InputStream in = new FileInputStream(settingsFile);
                Properties props = new Properties();
                Enumeration enumeration;
                
                props.load(in);
                
                try {in.close();} catch(Exception e) {e.printStackTrace();}
                
                enumeration = props.keys();
                
                while(enumeration.hasMoreElements()) savekeys.add(enumeration.nextElement());
                
                this.putAll(props);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static ApplicationSettings getInstance()
    {
        return instance;
    }
    
    public void setPesistentProperty(String key, String value)
    {
        savekeys.add(key);
        setProperty(key, value);
        
        savePersistentSettings();
    }
    
    public void removePesistentProperty(String key)
    {
        savekeys.remove(key);
        remove(key);
        
        savePersistentSettings();
    }
    
    private void savePersistentSettings()
    {
        try
        {
            Properties props = new Properties();
            FileOutputStream out;

            for(int i=0; i<savekeys.size(); i++)
            {
                props.setProperty(savekeys.get(i).toString(), getProperty(savekeys.get(i).toString()));
            }
            
            out = new FileOutputStream(settingsFile);
            
            props.save(out, " DataDino properties file.\n# DO NOT DELETE OR MODIFY THIS FILE OR YOUR SETTINGS WILL BE LOST!");
            
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void savePersistentObject(String name, Object object)
    {
        try
        {
            ObjectOutputStream out;
            File settingsFile = new File(System.getProperty("user.home")+File.separator+".datadino"+File.separator+name+".settings");
            
            out = new ObjectOutputStream(new FileOutputStream(settingsFile));
            out.writeObject(object);
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public Object loadPersistentObject(String name)
    {
        try
        {
            ObjectInputStream in;
            File settingsFile = new File(System.getProperty("user.home")+File.separator+".datadino"+File.separator+name+".settings");
            Object result;
            
            in = new ObjectInputStream(new FileInputStream(settingsFile));
            result = in.readObject();
            in.close();
            
            return result;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean isRegisteredVersion()
    {
//        if(getProperty("license-key") == null) return false;
        if(getProperty("license-key") == null) return true;
        
        try
        {
            KeyInfo info = new KeyInfo(getProperty("license-key"));
            
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isExpired()
    {
        if(getProperty("license-key") == null) return false;
        
        try
        {
            KeyInfo info = new KeyInfo(getProperty("license-key"));
            
            if(info.trial && new Date().after(info.date)) return true;
            
            return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isWindows()
    {
        return (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0);
    }
    
    public boolean isMacOSX()
    {
        return (System.getProperty("os.name").toLowerCase().indexOf("osx") >= 0 || System.getProperty("os.name").toLowerCase().indexOf("os x") >= 0);
    }
    
    public boolean isLinux()
    {
        return (System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0);
    }
    
    public boolean isWebstart()
    {
        return (containsKey("webstart-enabled") && getProperty("webstart-enabled").equalsIgnoreCase("true"));
    }
}
