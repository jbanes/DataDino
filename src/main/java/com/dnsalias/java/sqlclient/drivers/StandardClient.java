package com.dnsalias.java.sqlclient.drivers;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.URL;
import javax.swing.table.*;

import com.dnsalias.java.sqlclient.*;
import com.dnsalias.java.sqlclient.gdbi.*;
import com.dnsalias.java.sqlclient.util.*;

import com.datadino.sqlclient.util.*;

public class StandardClient extends SQLClientHandler
{
    private Class driverClass;
    private Connection connection;
    
    private Profile profile;
    
    private String[] dbListCache;
    private String[] tableListCache;
    private String[] tableTypeCache;
    private TableModel tableAdapterCache;
    private Hashtable columnAdapterCache;
    
    protected boolean isOpen;
    
    public StandardClient(Profile profile) throws SQLException, ClassNotFoundException, IOException
    {
        ClassLoader loader = profile.getDriverClassLoader();
        
        this.profile = profile;
        
        this.connection = null;
        
        this.tableListCache = null;
        this.tableAdapterCache = null;
        this.columnAdapterCache = new Hashtable();
        
        try
        {
            this.driverClass = Class.forName(profile.driver, true, loader);
        }
        catch(ClassNotFoundException ex)
        {
            this.driverClass = null;
            throw ex;
        }
    }
    
    public Profile getCurrentProfile()
    {
        return profile;
    }
    
    public void finalize()
    {
        try
        {
            closeConnection();
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    public void flushAllCaches()
    {
        this.dbListCache = null;
        this.flushTableCaches();
    }
    
    public void flushTableCaches()
    {
        this.tableListCache = null;
        this.tableAdapterCache = null;
        this.columnAdapterCache = new Hashtable();
    }
    
    public void closeConnection()
    {
        if (this.isOpen() && this.connection != null)
        {
            try
            {
                this.connection.close();
            }
            catch(SQLException ex)
            {
                System.err.println("ERROR closing connection: "+ex.getMessage());
            }
        }
        
        this.isOpen = false;
        this.tableListCache = null;
        this.tableTypeCache = null;
        this.tableAdapterCache = null;
        this.columnAdapterCache = null;
        this.dbListCache = null;
    }
    
    public void openConnection() throws SQLException
    {
        Properties info = new Properties();
        
        if (profile.username != null)
        {
            info.put("user", profile.username);
        }
        
        if (profile.password != null)
        {
            info.put("password", profile.password);
        }
        
        this.openConnection(profile.getConnectionString(), info);
    }
    
    private void openConnection(String url, Properties info) throws SQLException
    {
        if (this.isOpen()) throw new SQLException("connection is already open");
        
        try
        {
            Driver driver = (Driver)driverClass.newInstance();
            
            if (driver != null)
            {
                this.connection = driver.connect(url, info);
            }
            
            if(this.connection == null)
            {
                System.out.println(url);
                this.connection = DriverManager.getConnection(url, info.getProperty("user"), info.getProperty("password"));
            }
            
            this.isOpen = true;
        }
        catch (SQLException ex)
        {
            this.connection = null;
            throw ex;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw new SQLException("Could not load driver: "+ex.getMessage());
        }
    }
    
    public DatabaseMetaData getMetaData() throws SQLException
    {
        return this.connection.getMetaData();
    }
    
    public Statement createStatement() throws SQLException
    {
        return this.connection.createStatement();
    }
    
    public PreparedStatement createPreparedStatement(String statement) throws SQLException
    {
        return this.connection.prepareStatement(statement);
    }
    
    public TableModel performTableSQLQuery(String query) throws SQLException
    {
        // Create statement
        PreparedStatement stmt = this.connection.prepareStatement(query);
        
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
            DefaultTableModel adapter = new DefaultTableModel(new Object[]
            {"Total"}, 1);
            adapter.setValueAt(stmt.getUpdateCount(), 0, 0);
            
            return adapter;
        }
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

                        if(!hasNext()) stmt.close();
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
                    
                    if(!hasNext()) stmt.close();
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
    
    public Iterator executeQuery(String query) throws SQLException
    {
        return new ResultsIterator(query);
    }
    
    //    public TableModel[] executeQuery(String query) throws SQLException
    //    {
    //        // Create statement
    //        PreparedStatement stmt = this.connection.prepareStatement(query);
    //        ArrayList list = new ArrayList();
    //        TableModel[] model;
    //
    //        ResultSet rs;
    //        int update;
    //
    //        // Execute query
    //        boolean results = stmt.execute(query);
    //
    //        update = stmt.getUpdateCount();
    //
    //        while(results || update >= 0)
    //        {
    //            rs = stmt.getResultSet();
    //
    //            if(results)
    //            {
    //                DBTableModel adapter = new DBTableModel();
    //
    //                adapter.setResultSet(rs, true);
    //
    //                list.add(adapter);
    //            }
    //            else if(update >= 0)
    //            {
    //                DefaultTableModel adapter = new DefaultTableModel(new Object[]{"Total"}, 1);
    //                adapter.setValueAt(new Integer(update), 0, 0);
    //
    //                list.add(adapter);
    //            }
    //
    //            results = stmt.getMoreResults();
    //            update = stmt.getUpdateCount();
    //        }
    //
    //        model = new TableModel[list.size()];
    //
    //        for(int i=0; i<model.length; i++) model[i] = (TableModel)list.get(i);
    //
    //        System.out.println("Done!");
    //
    //        return model;
    //    }
    
    public int executeSQLChange(String query) throws SQLException
    {
        // Create statement
        Statement stmt = this.connection.createStatement();
        
        // Execute query
        stmt.execute(query);
        
        return stmt.getUpdateCount();
    }
    
    public TableModel getTableColumnInfo(DBObject dbo) throws SQLException
    {
        Object[] columnNames = {"Name", "Type", "Length", "Nullable?", "Default"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            Class[] types = new Class []
            {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Boolean.class,
                java.lang.String.class
            };
            
            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }
            
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        
        DatabaseMetaData meta = this.connection.getMetaData();
        ResultSet result;
        
        if(dbo.getSchema() != null && dbo.getSchema().trim().length() > 0) result = meta.getColumns(null, dbo.getSchema(), dbo.getName(), null);
        else result = meta.getColumns(null, null, dbo.getName(), null);
        
        while(result.next())
        {
            Object[] row = {result.getString("COLUMN_NAME"), result.getString("TYPE_NAME"), result.getInt("COLUMN_SIZE"), (result.getInt("NULLABLE") > 0), result.getString("COLUMN_DEF")};
            
            model.addRow(row);
        }
        
        return model;
    }
    
    private String translateReference(int type)
    {
        if(type == DatabaseMetaData.importedKeyNoAction) return "NO ACTION";
        if(type == DatabaseMetaData.importedKeyRestrict) return "RESTRICT";
        if(type == DatabaseMetaData.importedKeyCascade) return "CASCADE";
        if(type == DatabaseMetaData.importedKeySetNull) return "SET NULL";
        if(type == DatabaseMetaData.importedKeySetDefault) return "SET DEFAULT";
        
        return "";
    }
    
    public TableModel getTableReferences(String table) throws SQLException
    {
        Object[] columnNames =
        {"Key Column", "Table", "Column", "On Update", "On Delete", "Key Name", "Deferrable?"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0)
        {
            Class[] types = new Class []
            {
                java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.Boolean.class
            };
            
            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }
            
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        
        DatabaseMetaData meta = this.connection.getMetaData();
        ResultSet result;
        
        if(table.indexOf(".") > 0) result = meta.getImportedKeys(null, table.substring(0, table.indexOf(".")), table.substring(table.indexOf(".")+1));
        else result = meta.getImportedKeys(null, null, table);
        
        while(result.next())
        {
            String fk = result.getString("FKCOLUMN_NAME");
            String schema = result.getString("PKTABLE_SCHEM");
            String tableName = ((schema != null && schema.length() >0) ? schema+"." : "")+result.getString("PKTABLE_NAME");
            String column = result.getString("PKCOLUMN_NAME");
            String onupdate = translateReference(result.getInt("UPDATE_RULE"));
            String ondelete = translateReference(result.getInt("DELETE_RULE"));
            
            Object[] row =
            {fk, tableName, column, onupdate, ondelete, result.getString("FK_NAME"), new Boolean((result.getInt("DEFERRABILITY")) != DatabaseMetaData.importedKeyNotDeferrable)};
            
            model.addRow(row);
        }
        
        return model;
    }
    
    public TableModel getTablePrimaryKeyInfo(String table) throws SQLException
    {
        Object[] columnNames =
        {"Column", "Key Order"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0)
        {
            Class[] types = new Class []
            {
                java.lang.String.class, java.lang.Integer.class
            };
            
            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }
            
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        
        DatabaseMetaData meta = this.connection.getMetaData();
        ResultSet result;
        
        if(table.indexOf(".") > 0) result = meta.getPrimaryKeys(null, table.substring(0, table.indexOf(".")), table.substring(table.indexOf(".")+1));
        else result = meta.getPrimaryKeys(null, null, table);
        
        while(result.next())
        {
            Object[] row = {result.getString("COLUMN_NAME"), result.getInt("KEY_SEQ")};
            
            model.addRow(row);
        }
        
        return model;
    }
    
    public String[] getDatabaseTableList() throws SQLException
    {
        return this.getDatabaseTableList(false);
    }
    
    public String[] getDatabaseTableList(boolean refresh) throws SQLException
    {
        if(this.tableListCache != null && !refresh) return this.tableListCache;
        
        String[] list = null;
        boolean schema = includeSchema();
        DatabaseMetaData meta = this.connection.getMetaData();
        ResultSet result = meta.getTables(null, null, "%", null);
        
        Vector temp = new Vector();
        
        //list = this.columnToStringList(result, 3);
        
        while(result.next())
        {
            if(schema) temp.add(result.getString(2)+"."+result.getString(3));
            else temp.add(result.getString(3));
        }
        
        list = new String[temp.size()];
        
        for(int i=0; i<list.length; i++) list[i] = temp.get(i).toString();
        
        this.tableListCache = list;
        
        result.close();
        
        return list;
    }
    
    public String[] getDatabaseTableList(String type) throws SQLException
    {
        return getDatabaseTableList(null, type);
    }
    
    public String[] getDatabaseTableList(String schemaName, String type) throws SQLException
    {
        String[] list = null;
        boolean schema = includeSchema();
        DatabaseMetaData meta = this.connection.getMetaData();
        ResultSet result = meta.getTables(null, schemaName, "%", new String[]
        {type});
        
        Vector temp = new Vector();
        
        while(result.next())
        {
            if(schema) temp.add(result.getString(2)+"."+result.getString(3));
            else temp.add(result.getString(3));
        }
        
        list = new String[temp.size()];
        
        for(int i=0; i<list.length; i++) list[i] = temp.get(i).toString();
        
        //this.tableListCache = list;
        
        result.close();
        
        return list;
    }
    
    public String[] getDatabaseTableTypes() throws SQLException
    {
        if(this.tableTypeCache != null) return this.tableTypeCache;
        
        String[] list = null;
        
        DatabaseMetaData meta = this.connection.getMetaData();
        
        ResultSet result = meta.getTableTypes();
        
        list = this.columnToStringList(result, 1);
        
        this.tableTypeCache = list;
        
        return list;
    }
    
    public String[] getDatabaseColumnList(String table) throws SQLException
    {
        String[] list = null;
        DatabaseMetaData meta = this.connection.getMetaData();
        ResultSet result;
        
        if(table.indexOf(".") > 0) result = meta.getColumns(null, table.substring(0, table.indexOf(".")), table.substring(table.indexOf(".")+1), null);
        else result = meta.getColumns(null, null, table, null);
        
        list = this.columnToStringList(result, 4);
        
        result.close();
        
        return list;
    }
    
    public String[] getDatabaseColumnList(DBObject dbo) throws SQLException
    {
        String[] list = null;
        DatabaseMetaData meta = this.connection.getMetaData();
        ResultSet result;
        
        if(dbo.getSchema() != null && dbo.getSchema().trim().length() > 0) result = meta.getColumns(null, dbo.getSchema(), dbo.getName(), null);
        else result = meta.getColumns(null, null, dbo.getName(), null);
        
        list = this.columnToStringList(result, 4);
        
        result.close();
        
        return list;
    }
    
    public String getDatabaseColumnComments(String table, String column) throws SQLException
    {
        DatabaseMetaData meta = this.connection.getMetaData();
        ResultSet result;
        String[] list;
        
        if(table.indexOf(".") > 0) result = meta.getColumns(null, table.substring(0, table.indexOf(".")), table.substring(table.indexOf(".")+1), column);
        else result = meta.getColumns(null, null, table, column);
        
        list = this.columnToStringList(result, 12);
        
        return list[0];
    }
    
    public String getDatabaseColumnComments(String schema, String table, String column) throws SQLException
    {
        DatabaseMetaData meta = this.connection.getMetaData();
        ResultSet result;
        String comments = "";
        
        result = meta.getColumns(null, schema, table, column);
        
        if(result.next()) comments = result.getString(12);
        
        result.close();
        
        if(comments == null) comments = "";
        
        return comments;
    }
    
    private boolean includeSchema() throws SQLException
    {
        try
        {
            String[] list = getDatabaseSchemaList();
            
            if(list.length < 1) return false;
            if(list.length == 1 && list[0].equals("")) return false;
            
            return true;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public String[] getDatabaseSchemaList() throws SQLException
    {
        String[] list = null;
        DatabaseMetaData meta = this.connection.getMetaData();
        ResultSet result = meta.getSchemas();
        
        Vector temp = new Vector();
        
        list = this.columnToStringList(result, 1);
        
        return list;
    }
    
    public void rollback() throws SQLException
    {
        this.connection.rollback();
    }
    
    public void commit() throws SQLException
    {
        this.connection.commit();
    }
    
    public void setAutoCommit(boolean auto) throws SQLException
    {
        this.connection.setAutoCommit(auto);
    }
    
    private String[] columnToStringList(ResultSet result, int colIdx) throws SQLException
    {
        String[] list = new String[0];
        
        Vector tempList = new Vector();
        
        while(result.next())
        {
            String value = result.getString(colIdx);
            tempList.addElement(value);
        }
        
        list = new String[tempList.size()];
        
        for(int idx = 0 ; idx < list.length ; ++idx)
        {
            list[idx] = (String) tempList.elementAt(idx);
        }
        
        return list;
    }
    
    public String getName()
    {
        return profile.username;
    }
    
    public boolean isOpen()
    {
        return this.isOpen;
    }
}

