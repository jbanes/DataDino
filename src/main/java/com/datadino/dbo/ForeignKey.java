/*
 * ForeignKey.java
 *
 * Created on April 18, 2004, 9:37 PM
 */

package com.datadino.dbo;

/**
 *
 * @author  jbanes
 */
public class ForeignKey extends Key
{
    protected Table foreign;
    
    public ForeignKey(Column column, Table foreign)
    {
        super(column);
        
        this.foreign = foreign;
    }
 
    public Table getForeignTable()
    {
        return foreign;
    }
}
