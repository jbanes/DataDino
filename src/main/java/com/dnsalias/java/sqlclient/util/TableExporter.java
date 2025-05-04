/*
 * TableExporter.java
 *
 * Created on May 18, 2002, 4:27 PM
 */

package com.dnsalias.java.sqlclient.util;

import java.sql.*;
import java.util.*;

import com.dnsalias.java.sqlclient.*;
import com.dnsalias.java.sqlclient.gdbi.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class TableExporter 
{
    private SQLClientHandler handler;
    private String table;
    private String schema = null;
    
    private DatabaseMetaData meta;
    private Profile profile;
    
    private String newline = System.getProperty("line.separator");
    
    public TableExporter(SQLClientHandler handler, String table) throws SQLException
    {
        this.handler = handler;
        this.table = table;
        
        if(table.indexOf('.') > 0)
        {
            this.schema = table.substring(0, table.indexOf('.'));
            this.table = table.substring(table.indexOf('.')+1);
        }
        
        meta = handler.getMetaData();
        profile = handler.getCurrentProfile();
    }
    
    public TableExporter(SQLClientHandler handler, DBObject dbo) throws SQLException
    {
        this.handler = handler;
        
        this.schema = dbo.getSchema();
        this.table = dbo.getName();
        
        meta = handler.getMetaData();
        profile = handler.getCurrentProfile();
    }
    
    private String addDelimiters(String value)
    {
        boolean dot = false;
        boolean numbers = false;
        boolean delimiter = false;
        boolean multidot = false;
        boolean letters = false;
        boolean space = false;
        boolean symbols = false;
        
        char character;
        
        for(int i=0; i<value.length(); i++)
        {
            character = value.charAt(i);
            
            if(character == '.')
            {
                if(dot) multidot = true;
                else dot = true;
            }
            else if(Character.isDigit(character)) numbers = true;
            else if(Character.isLetter(character)) letters = true;
            else if(Character.isSpace(character)) space = true;
            else if(character == '\'') delimiter = true;
            else symbols = true;
        }
        
        if(delimiter) return value;
        if(letters || space || multidot || symbols) return "'"+value+"'";
        if(numbers) return value;
        
        return "'"+value+"'";
    }
    
    public String[] getForeignKeys() throws SQLException
    {
        ResultSet result = meta.getImportedKeys(null, schema, table);
        Vector keys = new Vector();
        String[] ret;
        
        String columnSource;
        String tableDest;
        String columnDest;
        String name;
        String update;
        String delete;
        String key;
        
        while(result.next())
        {
            key = "CONSTRAINT ";
            
            tableDest = result.getString(3);
            
            columnDest = result.getString(4);
            columnSource = result.getString(8);
            
            update = translateFKRule("ON UPDATE", result.getInt(10));
            delete = translateFKRule("ON DELETE", result.getInt(11));
            
            name = result.getString(12);
            
            if(name == null) name = columnSource+"_FK";
            
            key += name+" FOREIGN KEY ("+columnSource+") REFERENCES "+tableDest+" ("+columnDest+")"+newline;
            key += "        "+update+newline;
            key += "        "+delete;
            
            keys.add(key);
        }
        
        ret = new String[keys.size()];
        
        for(int i=0; i<ret.length; i++) ret[i] = keys.get(i).toString();
        
        return ret;
    }
    
    private String translateFKRule(String prefix, int rule)
    {
        if(rule == DatabaseMetaData.importedKeyNoAction) return prefix+" NO ACTION";
        if(rule == DatabaseMetaData.importedKeyCascade) return prefix+" CASCADE";
        if(rule == DatabaseMetaData.importedKeySetNull) return prefix+" SET NULL";
        if(rule == DatabaseMetaData.importedKeySetDefault) return prefix+" SET DEFAULT";
        if(rule == DatabaseMetaData.importedKeyRestrict) return prefix+" RESTRICT";
        
        return "";
    }

    public String[] getPrimaryKeys() throws SQLException
    {
        ResultSet result = meta.getPrimaryKeys(null, schema, table);
        
        Vector keys = new Vector();
        Vector sorting = new Vector();
        
        String[] ret;
        
        while(result.next())
        {
            String key = result.getString(4);
            Integer sort = result.getInt(5);
            int dest = sorting.size();
            
            for(int i=0; i<dest; i++)
            {
                if(((Integer)sorting.get(i)).intValue() > sort.intValue()) dest = i;
            }
            
            keys.add(dest, key);
            sorting.add(dest, sort);
        }
        
        ret = new String[keys.size()];
        
        for(int i=0; i<ret.length; i++) ret[i] = keys.get(i).toString();
        
        return ret;
    }
    
    public String[] getColumnNames() throws SQLException
    {
        if(schema == null) return handler.getDatabaseColumnList(table);
        else return handler.getDatabaseColumnList(schema+"."+table);
    }
    
    public String getColumnDefinition(String column) throws SQLException
    {
        //ResultSet result = meta.getColumns(null, schema, table, column);
        ResultSet result = meta.getColumns(null, schema, table, null);
        String sql = column;
        String size = "";
        String datatype;
        
        if(!result.next()) return null;
        
        while(!result.getString(4).equals(column))
        {
            if(!result.next()) return null;
        }
        
        datatype = result.getString(6);
        sql += " "+profile.getAlternate(datatype);
        
        size += result.getInt(7);
        if(result.getInt(9) > 0) size += ","+result.getInt(9);
        
        if(profile.isResizable(datatype)) sql += "("+size+")";
        
        if(result.getInt(11) == meta.columnNoNulls) sql += " NOT NULL";
        if(result.getString(13) != null && result.getString(13).trim().length() > 0) sql += " DEFAULT "+addDelimiters(result.getString(13));
        
        result.close();
        
        return sql;
    }
    
    public String[] getColumnDefinitions() throws SQLException
    {
        String[] columns = getColumnNames();
        String[] definitions = new String[columns.length];
        
        for(int i=0; i<definitions.length; i++) definitions[i] = getColumnDefinition(columns[i]);
        
        return definitions;
    }
    
    public String getSchemaSQL() throws SQLException
    {
        String[] definitions = getColumnDefinitions();
        String[] primarykeys = getPrimaryKeys();
        String[] foreignkeys = getForeignKeys();
        String sql = "create table "+table+" ("+newline;
        
        for(int i=0; i<definitions.length; i++)
        {
            if(i > 0) sql += ","+newline;
            sql += "    "+definitions[i];
        }
        
        if(primarykeys.length > 0)
        {
            sql += ","+newline;
            sql += "    CONSTRAINT "+table+"_PK PRIMARY KEY (";
            
            for(int i=0; i<primarykeys.length; i++) 
            {
                if(i > 0) sql += ", ";
                sql += primarykeys[i];
            }
            
            sql += ")";
        }
        
        if(foreignkeys.length > 0)
        {
            for(int i=0; i<foreignkeys.length; i++) sql += ","+newline+"    "+foreignkeys[i];
        }
        
        sql += ");";
        
        return sql;
    }
}
