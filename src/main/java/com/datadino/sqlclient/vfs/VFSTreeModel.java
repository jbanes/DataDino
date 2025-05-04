/*
 * VFSTableModel.java
 *
 * Created on December 12, 2002, 10:40 PM
 */

package com.datadino.sqlclient.vfs;

import java.util.*;

import javax.swing.event.*;
import javax.swing.tree.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class VFSTreeModel implements TreeModel
{
    private ArrayList listeners = new ArrayList();
    private VFSFileSystem manager = new VFSFileSystem();
    
    public VFSTreeModel() 
    {
        manager.addFileSystemListener(new FileSystemListener(this));
    }
    
    public VFSFileSystem getFileSystem()
    {
        return manager;
    }

    public Object getRoot()
    {
        return manager.getVFSFile("/");
    }
    
    public int getIndexOfChild(Object parent, Object child)
    {
        VFSFile dir = (VFSFile)parent;
        String name = ((VFSFile)child).getName();
        
        for (int i=0; i<dir.getFileCount(); i++)
        {
            if(name.equals(dir.getChild(i).getName())) return i;
        }
        
        return -1;
    }
    
    public boolean isLeaf(Object obj)
    {
        return !((VFSFile)obj).isDirectory();
    }
    
    public Object getChild(Object parent, int index)
    {
        VFSFile file = (VFSFile)parent;
        
        return file.getChild(index);
    }
    
    public void valueForPathChanged(TreePath treePath, Object obj)
    {
        VFSFile file = (VFSFile)treePath.getLastPathComponent();
        TreeModelEvent event = new TreeModelEvent(this, treePath);

        boolean success = file.renameFile(obj.toString());
        
        //if(file.getParent() != null) 
        //{
        //    file.getParent().refresh();
            //fireTreeModelChanged(file.getParent());
        //}
        
        for(int i=0; i<listeners.size(); i++)
        {
            ((TreeModelListener)listeners.get(i)).treeNodesChanged(event);
        }
    }
    
    public int getChildCount(Object parent)
    {
        return ((VFSFile)parent).getFileCount();
    }
    
    public void addTreeModelListener(TreeModelListener treeModelListener)
    {
        listeners.add(treeModelListener);
    }
    
    public void removeTreeModelListener(TreeModelListener treeModelListener)
    {
        listeners.remove(treeModelListener);
    }
    
    public void fireTreeModelChanged(VFSFile file)
    {
        String path = manager.getPath(file);
        //TreeModelEvent event = new TreeModelEvent(this, new Object[]{getFileSystem().getVFSFile("/")});
        TreeModelEvent event = new TreeModelEvent(this, getPath(path));
            
        System.out.println(path);
        
        for(int i=0; i<listeners.size(); i++)
        {
            ((TreeModelListener)listeners.get(i)).treeStructureChanged(event);
        }
    }
    
    public void fireTreeNodeRemoved(VFSFile file, TreePath path)
    {
        TreeModelEvent event = new TreeModelEvent(this, path);
            
        System.out.println(path);
        
        for(int i=0; i<listeners.size(); i++)
        {
            ((TreeModelListener)listeners.get(i)).treeNodesRemoved(event);
        }
    }
    
    public String findPath(VFSFile file)
    {
        return manager.getPath(file);
    }
    
    private Object[] getPath(String path)
    {
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        Object[] obj = new Object[tokenizer.countTokens()];
        VFSFile file = getFileSystem().getVFSFile("/");

        for(int i=0; tokenizer.hasMoreTokens(); i++)
        {
            obj[i] = file;
            
            if(file.isDirectory()) file = file.getChild(tokenizer.nextToken());
        }
        
        if(obj.length == 0) return new Object[]{getRoot()};
        
        return obj;
    }
    
    private class FileSystemListener implements VFSFileSystemListener
    {
        private Object source;
        
        public FileSystemListener(Object source)
        {
            this.source = source;
        }
        
        public void pathUnmounted(String path)
        {
            TreeModelEvent event = new TreeModelEvent(source, new Object[]{getFileSystem().getVFSFile("/")});
            
            for(int i=0; i<listeners.size(); i++)
            {
                ((TreeModelListener)listeners.get(i)).treeStructureChanged(event);
            }
        }
        
        public void pathMounted(String path)
        {
            TreeModelEvent event = new TreeModelEvent(source, new Object[]{getFileSystem().getVFSFile("/")});
            
            for(int i=0; i<listeners.size(); i++)
            {
                ((TreeModelListener)listeners.get(i)).treeStructureChanged(event);
            }
        }
    }
}
