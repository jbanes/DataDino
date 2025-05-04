/*
 * Table.java
 *
 * Created on April 18, 2004, 8:46 PM
 */

package com.datadino.dbo;

import java.util.*;

/**
 *
 * @author  jbanes
 */
public class Table
{
    private String name;
    
    private PrimaryKey primary;
    
    private ArrayList foreign = new ArrayList();
    private ArrayList columns = new ArrayList();
    
    
    public Table(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getColumnCount()
    {
        return columns.size();
    }
    
    public Column getColumn(int column)
    {
        return (Column)columns.get(column);
    }
    
    public Column getColumn(String name)
    {
        Column column;
        
        for(int i=0; i<columns.size(); i++)
        {
            column = (Column)columns.get(i);
            
            if(column.getName().equalsIgnoreCase(name)) return column;
        }
        
        return null;
    }
    
    public boolean isPrimaryKey(int column)
    {
        return (getPrimaryColumn() == column);
    }
    
    public boolean isForeignKey(int col)
    {
        Column column = (Column)columns.get(col);
        
        for(int i=0; i<foreign.size(); i++)
        {
            if(getForeignKey(i).getColumn().equals(column)) return true;
        }
        
        return false;
    }
    
    public Column removeColumn(int column)
    {
        return (Column)columns.remove(column);
    }
    
    public void add(Column column)
    {
        columns.add(column);
    }
    
    public PrimaryKey getPrimaryKey()
    {
        return primary;
    }
    
    public void setPrimaryKey(PrimaryKey key)
    {
        this.primary = key;
    }
    
    public int getPrimaryColumn()
    {
        if(primary == null) return -1;
        
        Column column = primary.getColumn();
        
        return columns.indexOf(column);
    }
    
    public int getForeignKeyCount()
    {
        return foreign.size();
    }
    
    public ForeignKey getForeignKey(int key)
    {
        return (ForeignKey)foreign.get(key);
    }
    
    public Table getForeignTable(int key)
    {
        ForeignKey fkey = (ForeignKey)foreign.get(key);
        
        return fkey.getForeignTable();
    }
    
    public int getForeignColumn(int key)
    {
        ForeignKey fkey = (ForeignKey)foreign.get(key);
        Column column = fkey.getColumn();
        
        return columns.indexOf(column);
    }
    
    public ForeignKey removeForeignKey(int key)
    {
        return (ForeignKey)foreign.remove(key);
    }
    
    public void add(ForeignKey key)
    {
        foreign.add(key);
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(name+"\n");
        buffer.append("{\n");
        
        for(int i=0; i<columns.size(); i++)
        {
            buffer.append("  ");
            
            if(isPrimaryKey(i)) buffer.append("PK ");
            else if(isForeignKey(i)) buffer.append("FK ");
            else buffer.append("   ");
            
            buffer.append(((Column)columns.get(i)).getName());
            buffer.append("\n");
        }
        
        buffer.append("}\n");
        
        return buffer.toString();
    }
}
