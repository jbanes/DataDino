/*
 * Key.java
 *
 * Created on April 18, 2004, 8:57 PM
 */

package com.datadino.dbo;

/**
 *
 * @author  jbanes
 */
public abstract class Key
{
    protected Column column;
    
    public Key(Column column)
    {
        this.column = column;
    }
    
    public Column getColumn()
    {
        return column;
    }
}
