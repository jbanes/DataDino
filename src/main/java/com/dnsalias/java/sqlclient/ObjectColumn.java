/*
 * ObjectColumn.java
 *
 * Created on May 2, 2002, 9:27 AM
 */

package com.dnsalias.java.sqlclient;

/**
 *
 * @author  jbanes
 * @version 
 */
public class ObjectColumn implements Column 
{
    public Object data;
    
    private Record parent;
    
    /** Creates new VarcharColumn */
    public ObjectColumn(Object data, Record parent) 
    {
        this.data = data;
        this.parent = parent;
    }

    public Record getParent()
    {
        return parent;
    }
    
    public void fromString(String data)
    {
        //fix me
        System.out.println(data.getClass());
        if(this.data instanceof String) this.data = data;
        if(this.data instanceof Integer) this.data = Integer.valueOf(data);
        if(this.data instanceof Boolean) this.data = Boolean.valueOf(data);
        if(this.data instanceof Double) this.data = Double.valueOf(data);
        if(this.data instanceof Character) this.data = data.charAt(0);
    }
    
    public String toString()
    {
        return data.toString();
    }
    
    public void setValue(Object obj)
    {
        data = obj;
    }
    
    public Object getValue()
    {
        return data;
    }
}
