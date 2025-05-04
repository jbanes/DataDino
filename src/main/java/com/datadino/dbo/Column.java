/*
 * Column.java
 *
 * Created on April 18, 2004, 8:46 PM
 */

package com.datadino.dbo;

/**
 *
 * @author  jbanes
 */
public class Column
{
    private String name;
    private String type;
    private int length;
    
    public Column(String name, String type)
    {
        this(name, type, -1);
    }
    
    public Column(String name, String type, int length)
    {
        this.name = name;
        this.type = type;
        this.length = length;
    }
    
    public int getLength()
    {
        return length;
    }
    
    public void setLength(int length)
    {
        this.length = length;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
}
