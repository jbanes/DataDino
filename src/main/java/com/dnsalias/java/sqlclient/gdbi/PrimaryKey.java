/*
 * PrimaryKey.java
 *
 * Created on October 1, 2002, 8:51 PM
 */

package com.dnsalias.java.sqlclient.gdbi;

/**
 *
 * @author  jbanes
 * @version 
 */
public class PrimaryKey extends DBKey
{
    public PrimaryKey(String name) 
    {
        super(name);
    }

    public String getType()
    {
        return "Primary Key";
    }
}
