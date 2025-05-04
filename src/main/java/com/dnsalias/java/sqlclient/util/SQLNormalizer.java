/*
 * SQLNormalizer.java
 *
 * Created on July 12, 2002, 4:16 PM
 */

package com.dnsalias.java.sqlclient.util;

import java.sql.*;

import com.dnsalias.java.sqlclient.*;
import com.dnsalias.java.sqlclient.drivers.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class SQLNormalizer 
{
    public static char getLeftDelimeter(SQLClientHandler handler) throws SQLException
    {
        char leftdelim = handler.getMetaData().getIdentifierQuoteString().charAt(0);
        Profile profile = handler.getCurrentProfile();
        
        if(profile.getProperty("left-token-delim") != null) leftdelim = profile.getProperty("left-token-delim").charAt(0);
        
        return leftdelim;
    }
    
    public static char getRightDelimeter(SQLClientHandler handler) throws SQLException
    {
        char rightdelim = handler.getMetaData().getIdentifierQuoteString().charAt(0);
        Profile profile = handler.getCurrentProfile();
        
        if(profile.getProperty("right-token-delim") != null) rightdelim = profile.getProperty("right-token-delim").charAt(0);
        
        return rightdelim;
    }
    
    public static String tableName(String tableName, SQLClientHandler handler) throws SQLException
    {
        String schema = "";
        String table = tableName;
        char leftdelim = handler.getMetaData().getIdentifierQuoteString().charAt(0);
        char rightdelim = leftdelim;
        Profile profile = handler.getCurrentProfile();
        
        String result;
        
        if(tableName.indexOf('.') > 0)
        {
            schema = tableName.substring(0, tableName.indexOf('.'));
            table = tableName.substring(tableName.indexOf('.')+1);
        }
        
        if(profile.getProperty("left-token-delim") != null) leftdelim = profile.getProperty("left-token-delim").charAt(0);
        if(profile.getProperty("right-token-delim") != null) rightdelim = profile.getProperty("right-token-delim").charAt(0);
        
        if(schema.length() > 0) result = leftdelim+schema+rightdelim+"."+leftdelim+table+rightdelim;
        else result = leftdelim+table+rightdelim;
        
        System.out.println(result);
        
        return result;
    }
    
    public static String columnName(String columnName, SQLClientHandler handler) throws SQLException
    {
        char leftdelim = handler.getMetaData().getIdentifierQuoteString().charAt(0);
        char rightdelim = leftdelim;
        Profile profile = handler.getCurrentProfile();
        
        String result;
        
        if(profile.getProperty("left-token-delim") != null) leftdelim = profile.getProperty("left-token-delim").charAt(0);
        if(profile.getProperty("right-token-delim") != null) rightdelim = profile.getProperty("right-token-delim").charAt(0);
        
        result = leftdelim+columnName+rightdelim;
        
        System.out.println(result);
        
        return result;
    }
    
    public static String string(String value)
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
    
    public static String stripIdentifierQuotes(String token, SQLClientHandler handler) throws SQLException
    {
        char leftdelim = handler.getMetaData().getIdentifierQuoteString().charAt(0);
        char rightdelim = leftdelim;
        Profile profile = handler.getCurrentProfile();
        
        if(profile.getProperty("left-token-delim") != null) leftdelim = profile.getProperty("left-token-delim").charAt(0);
        if(profile.getProperty("right-token-delim") != null) rightdelim = profile.getProperty("right-token-delim").charAt(0);
        
        token = token.trim();
        
        if(token.charAt(0) == leftdelim && token.charAt(token.length()-1) == rightdelim) token = token.substring(1, token.length()-1);
        
        return token;
    }
    
    public static String combineSchemaTable(String schema, String table)
    {
        if(schema == null || schema.length() < 1) return table;
        else return schema+"."+table;
    }
}
