/*
 * DBKey.java
 *
 * Created on October 1, 2002, 8:40 PM
 */

package com.dnsalias.java.sqlclient.gdbi;

import java.awt.*;
import java.util.*;

import javax.swing.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public abstract class DBKey 
{
    private String name;
    private Icon icon;
    
    private ArrayList columns = new ArrayList();
    
    public DBKey(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    
    public void setIcon(Icon icon)
    {
        this.icon = icon;
    }
    
    public Icon getIcon()
    {
        return icon;
    }
    
    public void addColumn(String column)
    {
        columns.add(column);
    }
    
    public String[] getColumns()
    {
        String[] cols = new String[columns.size()];
        
        for(int i=0; i<cols.length; i++) cols[i] = columns.get(i).toString();
        
        return cols;
    }
    
    public abstract String getType();
}
