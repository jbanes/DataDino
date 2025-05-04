/*
 * Column.java
 *
 * Created on June 9, 2001, 12:55 AM
 */

package com.dnsalias.java.sqlclient;

/**
 *
 * @author  jbanes
 * @version 
 */
public interface Column 
{
    public String toString();
    
    public void fromString(String data);
    
    public Record getParent();
    
    public void setValue(Object obj);
    
    public Object getValue();
}

