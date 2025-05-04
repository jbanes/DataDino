/*
 * FileSystemTreeModel.java
 *
 * Created on December 17, 2002, 7:36 PM
 */

package com.datadino.sqlclient.ui;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.tree.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class FileSystemTreeModel implements TreeModel
{
    private FileSystemView fs = FileSystemView.getFileSystemView();
    private boolean singlefs = true;
    
    private ArrayList listeners = new ArrayList();
    private Hashtable cache = new Hashtable();
    
    private boolean directoriesOnly = false;

    /** Creates new FileSystemTreeModel */
    public FileSystemTreeModel() 
    {
        this(false);
    }
    
    public FileSystemTreeModel(boolean directoriesOnly) 
    {
        this.directoriesOnly = directoriesOnly;
        
        if(fs.getRoots().length > 1) singlefs = false;
    }

    public Object getRoot()
    {
        if(singlefs) return fs.getRoots()[0];
        else return this;
    }
    
    private File[] getDirectories(File[] source)
    {
        int directories = 0;
        int index = 0;
        File[] dest;
        
        for(int i=0; i<source.length; i++)
        {
            if(source[i].isDirectory()) directories++;
        }
        
        dest = new File[directories];
        
        for(int i=0; i<source.length; i++)
        {
            if(source[i].isDirectory()) 
            {
                dest[index] = source[i];
                index++;
            }
        }
        
        return dest;
    }
    
    public int getIndexOfChild(Object parent, Object child)
    {
        File[] files;
        
        if(cache.containsKey(parent)) 
        {
            files = (File[])cache.get(parent);
        }
        else
        {
            if(parent == this) files = fs.getRoots();
            else files = fs.getFiles((File)parent, true);
            
            if(directoriesOnly) files = getDirectories(files);
            
            cache.put(parent, files);
        }
        
        for(int i=0; i<files.length; i++)
        {
            if(files[i].equals(child)) return i;
        }
        
        return -1;
    }
    
    public boolean isLeaf(Object obj)
    {
        if(directoriesOnly) return false;
        
        if(obj == this) return false;
        else return !((File)obj).isDirectory();
    }
    
    public Object getChild(Object parent, int index)
    {
        File[] files;
        
        if(cache.containsKey(parent)) 
        {
            files = (File[])cache.get(parent);
        }
        else
        {
            if(parent == this) files = fs.getRoots();
            else files = fs.getFiles((File)parent, true);
            
            if(directoriesOnly) files = getDirectories(files);
            
            cache.put(parent, files);
        }
        
        return files[index];
    }
    
    public void valueForPathChanged(TreePath treePath, Object obj)
    {
        //Don't do that
    }
    
    public int getChildCount(Object parent)
    {
        File[] files;
        
        if(cache.containsKey(parent)) 
        {
            files = (File[])cache.get(parent);
        }
        else
        {
            if(parent == this) files = fs.getRoots();
            else files = fs.getFiles((File)parent, true);
            
            if(directoriesOnly) files = getDirectories(files);
            
            cache.put(parent, files);
        }
        
        return files.length;
    }
    
    public void addTreeModelListener(TreeModelListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeTreeModelListener(TreeModelListener listener)
    {
        listeners.remove(listener);
    }
    
    public String toString()
    {
        return "File System";
    }
}
