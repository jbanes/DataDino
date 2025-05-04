/*
 * VFSManager.java
 *
 * Created on December 11, 2002, 11:29 PM
 */

package com.datadino.sqlclient.vfs;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class VFSFileSystem 
{
    private Hashtable mounts = new Hashtable();
    private Hashtable dirCache = new Hashtable();
    private RootVFSFile root = new RootVFSFile();
    
    private ArrayList listeners = new ArrayList();
    
    public VFSFileSystem()
    {
        dirCache.put("/", root);
    }

    public void addFileSystemListener(VFSFileSystemListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeFileSystemListener(VFSFileSystemListener listener)
    {
        listeners.remove(listener);
    }
    
    public void mount(String mountpoint, URL url) throws VFSException
    {
        VFSFile file = createVFSFile(mountpoint, url);
        
        if(file == null) throw new VFSException(url+" is invalid!", mountpoint);
        
        if(!file.getName().equals(mountpoint.substring(mountpoint.lastIndexOf('/')+1))) 
        {
            file.setName(mountpoint.substring(mountpoint.lastIndexOf('/')+1));
        }
        
        dirCache.put(mountpoint, file);
        mounts.put(mountpoint, url);
        
        for(int i=0; i<listeners.size(); i++)
        {
            ((VFSFileSystemListener)listeners.get(i)).pathMounted(mountpoint);
        }
    }
    
    public void unmount(String mountpoint)
    {
        mounts.remove(mountpoint);
        
        for(int i=0; i<listeners.size(); i++)
        {
            ((VFSFileSystemListener)listeners.get(i)).pathUnmounted(mountpoint);
        }
    }
    
    private static VFSFile createVFSFile(String path, URL url)
    {
        VFSFile file = null;
        
        if(url.getProtocol().equals("file")) file = LocalVFSFile.fromURL(url);
        
        if(file != null); //initialze parent here
        
        return file;
    }
    
    public Enumeration getMountPoints()
    {
        return mounts.keys();
    }
    
    public URL getMountURL(String path)
    {
        return (URL)mounts.get(path);
    }
    
    public boolean isMountPoint(String path)
    {
        if(mounts.containsKey(path)) return true;
        
        return false;
    }
    
    public VFSFile getMountPoint(String path)
    {
        String temp = path;
        VFSFile file;
        
        while(temp.lastIndexOf('/') >= 0)
        {
            if(isMountPoint(temp))
            {
                if(dirCache.containsKey(temp)) return (VFSFile)dirCache.get(temp);
                
                file = createVFSFile(path, (URL)mounts.get(temp));
                
                dirCache.put(temp, path);
            }
            else temp = temp.substring(0, temp.lastIndexOf('/'));
        }
        
        return null;
    }
    
    public VFSFile getVFSFile(String dir)
    {
        String[] path;
        String temp = dir;
        VFSFile file = null;
        
        if(dir.equals("/")) return root;
        
        while(temp.lastIndexOf('/') >= 0 && file == null)
        {
            if(mounts.containsKey(temp))
            {
                path = tokenize(dir.substring(temp.length()+1));
                file = createVFSFile(dir, (URL)mounts.get(temp));
                
                for(int i=0; i<path.length && file != null; i++)
                {
                    file = file.getChild(path[i]);
                }
            }
            else
            {
                temp = temp.substring(0, temp.lastIndexOf('/'));
            }
        }
        
        return file;
    }
    
    private String[] tokenize(String dir)
    {
        StringTokenizer tokenizer = new StringTokenizer(dir, "/");
        String[] path = new String[tokenizer.countTokens()];
        int index = 0;
        
        while(tokenizer.hasMoreTokens()) path[index++] = tokenizer.nextToken();
        
        return path;
    }
    
    public static String getPath(VFSFile file)
    {
        ArrayList list = new ArrayList();
        VFSFile parent = file;
        String path = "";
        
        while(parent != null)
        {
            list.add(parent);
            parent = parent.getParent();
        }
        
        for(int i=list.size()-1; i>=0; i--) path += "/" + ((VFSFile)list.get(i)).getName();
        
        return path;
    }
    
    private class RootVFSFile implements VFSFile
    {    
        private Icon icon = new ImageIcon(RootVFSFile.class.getResource("/images/icons/cpu.png"));
        private String name = "/";
        
        public String getName()
        {
            return "";
        }

        public boolean isDirectory()
        {
            return true;
        }

        public URL getFileLocation() throws MalformedURLException
        {
            return new URL("vfs", "localhost", "root");
        }

        public InputStream getFileInputStream() throws IOException
        {
            throw new IOException("Cannot open directories for I/O!");
        }

        public OutputStream getFileOutputStream() throws IOException
        {   
            throw new IOException("Cannot open directories for I/O!");
        }

        public VFSFile getParent()
        {
            return null;
        }

        public int getFileCount()
        {
            return mounts.size();
        }

        public VFSFile getChild(int index)
        {
            String[] names = new String[mounts.size()];
            Enumeration enumerator = mounts.keys();
            
            for(int i=0; enumerator.hasMoreElements(); i++) names[i] = enumerator.nextElement().toString();
            
            Arrays.sort(names);
            
            return getMountPoint(names[index]);
        }

        public VFSFile getChild(String name)
        {
            return getMountPoint("/"+name);
        }
        
        public String getPath()
        {
            return "/";
        }
        
        public String toString()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public Icon getIcon()
        {
            return icon;
        }
        
        public boolean createFile(String name)
        {
            return false;
        }
        
        public boolean createDirectory(String name)
        {
            return false;
        }
        
        public void refresh()
        {
        }
        
        public boolean renameFile(String name)
        {
            return false;
        }
        
        public boolean delete()
        {
            return false;
        }
    }
}
