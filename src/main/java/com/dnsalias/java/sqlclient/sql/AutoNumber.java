/*
 * AutoNumber.java
 *
 * Created on May 29, 2002, 11:41 AM
 */

package com.dnsalias.java.sqlclient.sql;

/**
 *
 * @author  jbanes
 * @version 
 */
public class AutoNumber 
{
    private String datatype = null;
    private String sql = null;
    
    public AutoNumber() 
    {
    }
    
    public String getDataType()
    {
        return datatype;
    }
    
    public void setDataType(String datatype)
    {
        this.datatype = datatype;
    }
    
    public String getColumnSQL()
    {
        return sql;
    }
    
    public void setColumnSQL(String sql)
    {
        this.sql = sql;
    }
    
    public boolean isDisabled()
    {
        return (sql == null && datatype == null);
    }
}
