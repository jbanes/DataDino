/*
 * LocalVFSFile.java
 *
 * Created on December 11, 2002, 11:14 PM
 */

package com.datadino.sqlclient.vfs;

import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class LocalVFSFile implements VFSFile 
{
    private File file;
    private VFSFile parent;
    private File[] list;
    private VFSFile[] children;
    private String name = null;
    
    
    private Icon icon = null;
    private static boolean enhanced = false;
    private static ImageIcon sqlicon = new ImageIcon(LocalVFSFile.class.getResource("/images/icons/sqldocument.png"));
        
    static
    {
        if(System.getProperty("java.vm.version").startsWith("1.3")) enhanced = false;
        else enhanced = true;
    }
    
    public LocalVFSFile(File file) 
    {
        this(file, null);
    }
    
    public LocalVFSFile(File file, VFSFile parent) 
    {
        this.file = file;
        this.parent = parent;
        
        if(file.isDirectory()) list = file.listFiles();
        else list = new File[0];
        
        children = new VFSFile[list.length];
    }
    
    public VFSFile getParent()
    {
        return parent;
    }
    
    public OutputStream getFileOutputStream() throws IOException
    {
        return new FileOutputStream(file);
    }
    
    public void setParent(VFSFile file)
    {
        this.parent = file;
    }
    
    public VFSFile getChild(int index)
    {
        if(children[index] == null) children[index] = new LocalVFSFile(list[index], this);
        
        return children[index];
    }
    
    public int getFileCount()
    {
        return list.length;
    }
    
    public String getName()
    {
        if(name == null) return file.getName();
        else return name;
    }
    
    public InputStream getFileInputStream() throws IOException
    {
        return new FileInputStream(file);
    }
    
    public boolean isDirectory()
    {
        return file.isDirectory();
    }
    
    public URL getFileLocation() throws MalformedURLException
    {
        return file.toURL();
    }
    
    public VFSFile getChild(String name)
    {
        for(int i=0; i<list.length; i++)
        {
            if(getChild(i).getName().equals(name)) return getChild(i);
        }
        
        return null;
    }
    
    public static VFSFile fromURL(URL url)
    {
        File file = new File(url.getFile());
        
        if(file.exists()) return new LocalVFSFile(file);
        
        return null;
    }
    
    public String getPath()
    {
        return VFSFileSystem.getPath(this);
    }
    
    public String toString()
    {
        return getName();
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Icon getIcon()
    {
        if(getName().endsWith(".sql") && !file.isDirectory()) return sqlicon;
        if(icon == null && enhanced) icon = FileSystemView.getFileSystemView().getSystemIcon(file);
        
        return icon;
    }
    
    public boolean createFile(String name)
    {
        File newFile = new File(file, name);
        
        if(newFile.exists() || !file.isDirectory()) return false;
        
        try
        {
            new FileOutputStream(newFile).close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public boolean createDirectory(String name)
    {
        File newFile = new File(file, name);
        
        if(newFile.exists() || !file.isDirectory()) return false;
        
        System.out.println("Creating directory "+newFile);
        
        return newFile.mkdir();
    }
    
    public void refresh()
    {
        if(file.isDirectory()) list = file.listFiles();
        else list = new File[0];
        
        children = new VFSFile[list.length];
    }
    
    protected void refreshParent(File parent)
    {
        this.file = new File(parent, file.getName());
        
        for(int i=0; i<children.length; i++) 
        {
            if(children[i] != null) ((LocalVFSFile)children[i]).refreshParent(file);
        }
    }
    
    public boolean renameFile(String name)
    {
        File newFile = new File(file.getParentFile(), name);
        
        if(!(parent instanceof LocalVFSFile)) return false;
        
        boolean success = file.renameTo(newFile);
        
        if(success) file = newFile;
        if(success && file.isDirectory())
        {
            for(int i=0; i<children.length; i++)
            {
                if(children[i] != null)
                {
                    ((LocalVFSFile)children[i]).refreshParent(file);
                }
            }
        }
        
        return success;
    }
    
    protected void removeChild(VFSFile child)
    {
        VFSFile[] newChildren = new VFSFile[children.length];
        
        for(int i=0, old=0; i<newChildren.length; i++)
        {
            if(children[i] != child)
            {
                newChildren[i] = children[old];
                old++;
            }
        }
    }
    
    public boolean delete()
    {
        boolean success = file.delete();
        
        if(success && parent instanceof LocalVFSFile) ((LocalVFSFile)parent).removeChild(this);
        
        return success;
    }
}
