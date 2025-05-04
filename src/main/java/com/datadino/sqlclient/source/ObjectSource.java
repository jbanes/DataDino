/*
 * ObjectSource.java
 *
 * Created on August 14, 2003, 6:16 PM
 */

package com.datadino.sqlclient.source;

import java.io.*;
import java.sql.*;

import com.dnsalias.java.sqlclient.*;
import com.dnsalias.java.sqlclient.drivers.*;
import com.dnsalias.java.sqlclient.gdbi.*;
import com.dnsalias.java.sqlclient.util.*;

/**
 *
 * @author  jbanes
 */
public class ObjectSource 
{
    private SQLClientHandler handler;
    
    public ObjectSource(SQLClientHandler handler)
    {
        this.handler = handler;
    }
    
    public String getSource(DBObject dbo) throws SQLException
    {
        boolean clob = false;
        
        if(handler.getCurrentProfile().getProperty("source-clob") != null) clob = Boolean.getBoolean(handler.getCurrentProfile().getProperty("source-clob"));
        
        if(dbo.getType().equals("TABLE"))
        {
            TableExporter exporter = new TableExporter(handler, dbo);
            
            return exporter.getSchemaSQL();
        }
        
        if(dbo.getType().equals("VIEW") && handler.getCurrentProfile().getProperty("get-view-sql") != null)
        {
            String sql = modifySQL(handler.getCurrentProfile().getProperty("get-view-sql"), dbo);
            String text = getText(sql, clob);
            
            return prettyPrint(text);
        }
        
        if(dbo.getType().equals("INDEX") && handler.getCurrentProfile().getProperty("get-index-sql") != null)
        {
            String sql = modifySQL(handler.getCurrentProfile().getProperty("get-index-sql"), dbo);
            String text = getText(sql, clob);
            
            return prettyPrint(text);
        }
        
        if(dbo.getType().equals("SYNONYM") && handler.getCurrentProfile().getProperty("get-synonym-sql") != null)
        {
            String sql = modifySQL(handler.getCurrentProfile().getProperty("get-synonym-sql"), dbo);
            String text = getText(sql, clob);
            
            return prettyPrint(text);
        }
        
        return null;
    }
    
    private String getText(String sql, boolean clob) throws SQLException
    {
        Statement stmt = handler.createStatement();
        ResultSet result = stmt.executeQuery(sql);
        String text = "";
        
        while(result.next())
        {
            if(!clob) text += result.getString(1);
            else text += result.getCharacterStream(1);
        }
        
        return text;
    }
    
    private String loadClob(Reader in) throws SQLException
    {
        StringBuffer buffer = new StringBuffer();
        char[] data = new char[256];
        int length;
        
        try
        {
            while((length = in.read(data)) > 0) buffer.append(data, 0, length);

            in.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        
        return buffer.toString();
    }
    
    private String modifySQL(String sql, DBObject dbo)
    {
        int index;
        
        while((index = sql.indexOf("$table")) >= 0) sql = sql.substring(0, index)+dbo.getName()+sql.substring(index+"$table".length());
        while((index = sql.indexOf("$index")) >= 0) sql = sql.substring(0, index)+dbo.getName()+sql.substring(index+"$index".length());
        while((index = sql.indexOf("$dbo")) >= 0) sql = sql.substring(0, index)+dbo.getDelimitedString()+sql.substring(index+"$dbo".length());
        while((index = sql.indexOf("$schema")) >= 0) sql = sql.substring(0, index)+dbo.getSchema()+sql.substring(index+"$schema".length());
        
        return sql;
    }
    
    private String prettyPrint(String sql)
    {
        String[] breakswithtabs = {"SELECT ", ", ", "FROM "};
        String[] breakbefore = {"JOIN ", "FROM ", "ON ", "USING "};
     
        StringBuffer buffer = new StringBuffer(sql);
        int index = 0;
        
        
        if((index = sql.indexOf('\n')) >= 0 && sql.indexOf('\n', index) >= 0) return sql;
        
        for(int i=0; i<buffer.length(); i++)
        {
            if(buffer.charAt(i) == '\'')
            {
                i++;
                while(i < buffer.length() && buffer.charAt(i) != '\'') i++;
            }
            
            if(buffer.charAt(i) == '\"')
            {
                i++;
                while(i < buffer.length() && buffer.charAt(i) != '\"') i++;
            }
            
            for(int item=0; item<breakbefore.length; item++)
            {
                if(match(buffer, i, breakbefore[item]))
                {
                    buffer.insert(i, "\n");
                    i++;
                }
            }
            
            for(int item=0; item<breakswithtabs.length; item++)
            {
                if(match(buffer, i, breakswithtabs[item]))
                {
                    i += breakswithtabs[item].length();
                    buffer.insert(i, "\n    ");
                }
            }
        }
        
        return buffer.toString();
    }
    
    private boolean match(StringBuffer buffer, int index, String value)
    {
        if(buffer.length()-index < value.length()) return false;
        
        for(int i=0; i<value.length(); i++)
        {
            if(buffer.charAt(i+index) != value.charAt(i)) return false;
        }   
        
        return true;
    }
}
