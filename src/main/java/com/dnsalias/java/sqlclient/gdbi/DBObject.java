/*
 * DBObject.java
 *
 * Created on September 4, 2002, 1:23 AM
 */

package com.dnsalias.java.sqlclient.gdbi;

import java.io.Serializable;

/**
 *
 * @author  jbanes
 * @version 
 */
public class DBObject implements Serializable
{
    private String database;
    private String type;
    private String schema;
    private String name;
    
    private boolean printSchema = true;
    
    private char leftdelim = '"';
    private char rightdelim = '"';
    
    public DBObject(String database, String type, String schema, String name) 
    {
        this.database = database;
        this.type = type;
        this.schema = schema;
        this.name = name;
    }
    
    public char getLeftDelimeter()
    {
        return leftdelim;
    }
    
    public void setLeftDelimeter(char leftdelim)
    {
        this.leftdelim = leftdelim;
    }
    
    public char getRightDelimeter()
    {
        return rightdelim;
    }
    
    public void setRightDelimeter(char rightdelim)
    {
        this.rightdelim = rightdelim;
    }
    
    public String getDatabase()
    {
        return database;
    }

    public String getType()
    {
        return type;
    }
    
    public String getSchema()
    {
        return schema;
    }
    
    public String getName()
    {
        return name;
    }
    
    public boolean getPrintSchema()
    {
        return printSchema;
    }
    
    public void setPrintSchema(boolean printSchema)
    {
        this.printSchema = printSchema;
    }
    
    public String getFullString()
    {
        if(schema != null && schema.length() > 0) return schema+"."+name;
        else return name;
    }
    
    public String getDelimitedString()
    {
        if(schema != null && schema.length() > 0) return leftdelim+schema+rightdelim+"."+leftdelim+name+rightdelim;
        else return leftdelim+name+rightdelim;
    }
    
    public String toString()
    {
        if(schema != null && schema.length() > 0 && printSchema) return schema+"."+name;
        else return name;
    }
    
    public boolean equals(Object object)
    {
        if(object == null || !(object instanceof DBObject)) return false;
        
        DBObject dbo = (DBObject)object;
        
        if(!type.equals(dbo.getType())) return false;
        if(schema == null && dbo.getSchema() != null) return false;
        if(schema != null && !schema.equals(dbo.getSchema())) return false;
        if(!name.equals(dbo.getName())) return false;
        
        return true;
    }
    
    public int hashCode()
    {
        return getFullString().hashCode();
    }
}
