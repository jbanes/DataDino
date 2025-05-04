/*
 * Record.java
 *
 * Created on June 9, 2001, 12:54 AM
 */

package com.dnsalias.java.sqlclient;

/**
 *
 * @author  jbanes
 * @version 
 */
public class Record 
{
    private int id;
    private Column[] columns = null;
    private boolean[] changed = null;
    private Column[] newdata = null;
    
    /** Creates new Record */
    public Record(int id) 
    {
        this.id = id;
    }
    
    public int getID()
    {
        return id;
    }
    
    public Column getColumn(int loc)
    {
        if(changed[loc]) return newdata[loc];
        
        return columns[loc];
    }
    
    public boolean isChanged(int loc)
    {
        return changed[loc];
    }
    
    public int getTotalColumns()
    {
        return columns.length;
    }
    
    public void updateColumn(int index, Object value)
    {
        if(newdata == null) newdata = new Column[columns.length];
        
        newdata[index] = new ObjectColumn(value, this);
        changed[index] = true;
    }
    
    public Column getOriginalColumn(int index)
    {
        return columns[index];
    }
    
    public void setColumns(Column[] columns)
    {
        this.columns = columns;
        this.changed = new boolean[columns.length];
    }
    
    public void setID(int id)
    {
        this.id = id;
    }
}
