/*
 * Copyright 2024 INVIRGANCE LLC

Permission is hereby granted, free of charge, to any person obtaining a copy 
of this software and associated documentation files (the “Software”), to deal 
in the Software without restriction, including without limitation the rights to 
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
of the Software, and to permit persons to whom the Software is furnished to do 
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
SOFTWARE.
 */
package com.dnsalias.java.sqlclient.drivers;

import com.datadino.sqlclient.util.InterruptibleProcess;
import com.dnsalias.java.sqlclient.DBTableModel;
import com.dnsalias.java.sqlclient.Profile;
import com.dnsalias.java.sqlclient.SQLClientHandler;
import com.dnsalias.java.sqlclient.gdbi.DBObject;
import java.sql.*;
import java.util.Iterator;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author jbanes
 */
public class ConnectionClient extends SQLClientHandler
{
    private SQLClientHandler parent;
    private Connection connection;

    public ConnectionClient(SQLClientHandler parent, Connection connection)
    {
        this.parent = parent;
        this.connection = connection;
    }

    @Override
    public String[] getDatabaseTableList() throws SQLException
    {
        return parent.getDatabaseTableList();
    }

    @Override
    public String[] getDatabaseTableList(boolean refresh) throws SQLException
    {
        return parent.getDatabaseTableList(refresh);
    }

    @Override
    public String[] getDatabaseTableList(String type) throws SQLException
    {
        return parent.getDatabaseTableList(type);
    }

    @Override
    public String[] getDatabaseTableList(String schema, String type) throws SQLException
    {
        return parent.getDatabaseTableList(schema, type);
    }

    @Override
    public String[] getDatabaseTableTypes() throws SQLException
    {
        return parent.getDatabaseTableTypes();
    }

    @Override
    public String[] getDatabaseColumnList(String table) throws SQLException
    {
        return parent.getDatabaseColumnList(table);
    }

    @Override
    public String[] getDatabaseColumnList(DBObject dbo) throws SQLException
    {
        return parent.getDatabaseColumnList(dbo);
    }

    @Override
    public String getDatabaseColumnComments(String table, String column) throws SQLException
    {
        return parent.getDatabaseColumnComments(table, column);
    }

    @Override
    public String getDatabaseColumnComments(String schema, String table, String column) throws SQLException
    {
        return parent.getDatabaseColumnComments(schema, table, column);
    }

    @Override
    public String[] getDatabaseSchemaList() throws SQLException
    {
        return parent.getDatabaseSchemaList();
    }

    @Override
    public TableModel performTableSQLQuery(String query) throws SQLException
    {
        // Create statement
        PreparedStatement stmt = connection.prepareStatement(query);
        
        // Execute query
        boolean results = stmt.execute();
        
        if(results)
        {
            ResultSet rs = stmt.getResultSet();
            DBTableModel adapter = new DBTableModel();
            adapter.setResultSet(rs);
            
            return adapter;
        }
        else
        {
            DefaultTableModel adapter = new DefaultTableModel(new Object[]{"Total"}, 1);
            adapter.setValueAt(stmt.getUpdateCount(), 0, 0);
            
            return adapter;
        }
    }

    @Override
    public Iterator executeQuery(String query) throws SQLException
    {
        return new ResultsIterator(query);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException
    {
        return connection.getMetaData();
    }

    @Override
    public Statement createStatement() throws SQLException
    {
        return connection.createStatement();
    }

    @Override
    public int executeSQLChange(String query) throws SQLException
    {
        // Create statement
        Statement stmt = this.connection.createStatement();
        
        // Execute query
        stmt.execute(query);
        
        return stmt.getUpdateCount();
    }

    @Override
    public TableModel getTableColumnInfo(DBObject dbo) throws SQLException
    {
        return parent.getTableColumnInfo(dbo);
    }

    @Override
    public TableModel getTablePrimaryKeyInfo(String table) throws SQLException
    {
        return parent.getTablePrimaryKeyInfo(table);
    }

    @Override
    public TableModel getTableReferences(String table) throws SQLException
    {
        return parent.getTableReferences(table);
    }

    @Override
    public void openConnection() throws SQLException
    {
        // Connection is already open
    }

    @Override
    public void closeConnection() throws SQLException
    {
        connection.close();
    }

    @Override
    public void rollback() throws SQLException
    {
        connection.rollback();
    }

    @Override
    public void commit() throws SQLException
    {
        connection.commit();
    }

    @Override
    public void setAutoCommit(boolean auto) throws SQLException
    {
        connection.setAutoCommit(auto);
    }

    @Override
    public Profile getCurrentProfile()
    {
        return parent.getCurrentProfile();
    }

    @Override
    public PreparedStatement createPreparedStatement(String statement) throws SQLException
    {
        return connection.prepareStatement(statement);
    }
    
    @Override
    public void completeOperation() throws SQLException
    {
        connection.close();
    }
    
    private class ResultsIterator implements Iterator, InterruptibleProcess
    {
        private Statement stmt = null;
        
        private ResultSet rs;
        private int update;
        private boolean results;
        private boolean stopped;
        
        private TableModel model;
        private boolean block = false;
        
        private String sql;
        private Exception exception = null;
        
        public ResultsIterator(String sql) throws SQLException
        {
            this.sql = sql;
        }
        
        public boolean hasNext()
        {
            while(block)
            {
                try{Thread.sleep(100);} catch(InterruptedException e) {}
            }
            
            if(stmt == null)
            {
                try
                {
                    stmt = connection.createStatement();
                    results = stmt.execute(sql);
                    update = stmt.getUpdateCount();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    //stopped = true;
                    exception = e;
                }
            }
            
            return ((results || update >= 0 || exception != null) && !stopped);
        }
        
        /** Returns the next element in the iteration.
         *
         * @return the next element in the iteration.
         * @exception NoSuchElementException iteration has no more elements.
         *
         */
        public Object next()
        {
            if(exception != null)
            {
                stopped = true;
                return exception;
            }
            
            try
            {
                rs = stmt.getResultSet();
                
                if(results)
                {
                    model = new DBTableModel();
                    
                    block = true;
                    
                    try
                    {
                        ((DBTableModel)model).setResultSet(rs, true);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        exception = e;

                        block = false;
                    }

                    try
                    {
                        results = stmt.getMoreResults();
                        update = stmt.getUpdateCount();

                        block = false;

                        if(!hasNext()) 
                        {
                            stmt.close();
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        exception = e;

                        block = false;
                    }
                }
                else if(update >= 0)
                {
                    DefaultTableModel adapter = new DefaultTableModel(new Object[]{"Total"}, 1);
                    adapter.setValueAt(update, 0, 0);
                    
                    model = adapter;                    

                    results = stmt.getMoreResults();
                    update = stmt.getUpdateCount();
                    
                    if(!hasNext()) 
                    {
                        stmt.close();
                    }
                }
                
                /*if(!hasNext())
                {
                    stmt.close();
                }*/
                
                return model;
            }
            catch(Exception e)
            {
                System.out.println("Returning exception!");
                return e;
            }
        }
        
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
        
        public void stop()
        {
            stopped = true;
            
            if(model != null && model instanceof InterruptibleProcess) ((InterruptibleProcess)model).stop();
        }
    }
}
