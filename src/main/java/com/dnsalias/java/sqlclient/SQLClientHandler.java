/*
 * SQLClientHandler.java
 *
 */

package com.dnsalias.java.sqlclient;

import java.sql.*;
import java.util.*;

import javax.swing.table.*;

import com.dnsalias.java.sqlclient.gdbi.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public abstract class SQLClientHandler 
{
    private static SQLClientHandler handler = null;
    
    public static void setCurrentHandler(SQLClientHandler handler)
    {
        SQLClientHandler.handler = handler;
    }
    
    public static SQLClientHandler getCurrentHandler()
    {
        return handler;
    }   
    
    public abstract String[] getDatabaseTableList() throws SQLException;
    
    public abstract String[] getDatabaseTableList(boolean refresh) throws SQLException;
    
    public abstract String[] getDatabaseTableList(String type) throws SQLException;
    
    public abstract String[] getDatabaseTableList(String schema, String type) throws SQLException;
    
    public abstract String[] getDatabaseTableTypes() throws SQLException;
    
    public abstract String[] getDatabaseColumnList(String table) throws SQLException;
    
    public abstract String[] getDatabaseColumnList(DBObject dbo) throws SQLException;
    
    public abstract String getDatabaseColumnComments(String table, String column) throws SQLException;
    
    public abstract String getDatabaseColumnComments(String schema, String table, String column) throws SQLException;
    
    public abstract String[] getDatabaseSchemaList() throws SQLException;
    
    public abstract TableModel performTableSQLQuery(String query) throws SQLException;
    
    public abstract Iterator executeQuery(String query) throws SQLException;
    
    public abstract DatabaseMetaData getMetaData() throws SQLException;
    
    public abstract Statement createStatement() throws SQLException;
    
    public abstract int executeSQLChange(String query) throws SQLException;
    
    public abstract TableModel getTableColumnInfo(DBObject dbo) throws SQLException;
    
    public abstract TableModel getTablePrimaryKeyInfo(String table) throws SQLException;
    
    public abstract TableModel getTableReferences(String table) throws SQLException;
    
    public abstract void openConnection() throws SQLException;
    
    public abstract void closeConnection() throws SQLException;
    
    public abstract void rollback() throws SQLException;
    
    public abstract void commit() throws SQLException;
    
    public abstract void setAutoCommit(boolean auto) throws SQLException;
    
    public abstract Profile getCurrentProfile();
    
    public abstract PreparedStatement createPreparedStatement(String statement) throws SQLException;
    
    public SQLClientHandler getConnection()
    {
        return this;
    }
    
    public void completeOperation() throws SQLException
    {
        // No op for most connections
    }
}
