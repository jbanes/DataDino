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
import com.invirgance.convirgance.jdbc.AutomaticDriver;
import com.invirgance.convirgance.jdbc.AutomaticDrivers;
import com.invirgance.convirgance.jdbc.StoredConnection;
import com.invirgance.convirgance.jdbc.callback.ConnectionCallback;
import com.invirgance.convirgance.jdbc.callback.DatabaseMetaDataCallback;
import com.invirgance.convirgance.jdbc.schema.Catalog;
import com.invirgance.convirgance.jdbc.schema.DatabaseSchemaLayout;
import com.invirgance.convirgance.jdbc.schema.Schema;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.sql.DataSource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author jbanes
 */
public class DataSourceClient extends SQLClientHandler
{
    private Profile profile;
    private DataSource source;
    private DatabaseSchemaLayout layout;
    
    private String[] tableListCache;
    private String[] tableTypeCache;

    public DataSourceClient(Profile profile)
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName(profile.getProperty("convirgance-driver"));
        StoredConnection connection = driver
                                        .createConnection(profile.name)
                                        .driver()
                                            .username(profile.username)
                                            .password(profile.password)
                                            .url(profile.getConnectionString())
                                        .build();
        
        System.out.println("URL: " + profile.getConnectionString());

        this.profile = profile;
        this.source = connection.getDataSource();
        this.layout = connection.getSchemaLayout();
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
    
    @Override
    public String[] getDatabaseTableList() throws SQLException
    {
        return getDatabaseTableList(false);
    }

    @Override
    public String[] getDatabaseTableList(boolean refresh) throws SQLException
    {
        if(this.tableListCache != null && !refresh) return this.tableListCache;
        
        ConnectionCallback.execute(source, new ConnectionCallback() {
            
            @Override
            public void execute(Connection connection) throws SQLException
            {
                boolean schema = includeSchema();
                DatabaseMetaData meta = connection.getMetaData();
                
                try (ResultSet result = meta.getTables(null, null, "%", null)) 
                {
                    var temp = new ArrayList<String>();
                    
                    while(result.next())
                    {
                        if(schema) temp.add(result.getString(2)+"."+result.getString(3));
                        else temp.add(result.getString(3));
                    }
                    
                    tableListCache = temp.toArray(String[]::new);
                }
            }
        });
        
        return tableListCache;
    }

    @Override
    public String[] getDatabaseTableList(String type) throws SQLException
    {
        return getDatabaseTableList(null, type);
    }

    @Override
    public String[] getDatabaseTableList(String schemaName, String type) throws SQLException
    {
        var temp = new ArrayList<String>();
        
        ConnectionCallback.execute(source, new ConnectionCallback() {
            
            @Override
            public void execute(Connection connection) throws SQLException
            {
                boolean schema = includeSchema();
                DatabaseMetaData meta = connection.getMetaData();
                
                try(ResultSet result = meta.getTables(null, schemaName, "%", new String[]{type})) 
                {
                    while(result.next())
                    {
                        if(schema) temp.add(result.getString(2)+"."+result.getString(3));
                        else temp.add(result.getString(3));
                    }
                }
            }
        });
        
        return temp.toArray(String[]::new);
    }
    
    private String[] columnToStringList(ResultSet result, int colIdx) throws SQLException
    {
        var list = new ArrayList<String>();
        
        while(result.next())
        {
            list.add(result.getString(colIdx));
        }
        
        return list.toArray(String[]::new);
    }
    
    private ArrayList<String> columnToStringList(ResultSet result, int colIdx, ArrayList<String> list) throws SQLException
    {
        while(result.next())
        {
            list.add(result.getString(colIdx));
        }
        
        return list;
    }

    @Override
    public String[] getDatabaseTableTypes() throws SQLException
    {
        if(this.tableTypeCache != null) return this.tableTypeCache;
        
        ConnectionCallback.execute(source, new DatabaseMetaDataCallback() {
            
            @Override
            public void execute(DatabaseMetaData meta) throws SQLException
            {
                tableTypeCache = columnToStringList(meta.getTableTypes(), 1);
            }
        });
        
        return tableTypeCache;
    }

    @Override
    public String[] getDatabaseColumnList(String table) throws SQLException
    {
        var list = new ArrayList<String>();
        
        ConnectionCallback.execute(source, new DatabaseMetaDataCallback() {
            
            @Override
            public void execute(DatabaseMetaData meta) throws SQLException
            {
                ResultSet result;
        
                if(table.indexOf(".") > 0) result = meta.getColumns(null, table.substring(0, table.indexOf(".")), table.substring(table.indexOf(".")+1), null);
                else result = meta.getColumns(null, null, table, null);

                columnToStringList(result, 4, list);
            }
        });
        
        return list.toArray(String[]::new);
    }

    @Override
    public String[] getDatabaseColumnList(DBObject dbo) throws SQLException
    {
        var list = new ArrayList<String>();
        
        ConnectionCallback.execute(source, new DatabaseMetaDataCallback() {
            
            @Override
            public void execute(DatabaseMetaData meta) throws SQLException
            {
                ResultSet result;
                
                if(dbo.getSchema() != null && dbo.getSchema().trim().length() > 0) result = meta.getColumns(null, dbo.getSchema(), dbo.getName(), null);
                else result = meta.getColumns(null, null, dbo.getName(), null);

                columnToStringList(result, 4, list);
            }
        });
        
        return list.toArray(String[]::new);
    }

    @Override
    public String getDatabaseColumnComments(String table, String column) throws SQLException
    {
        var list = new ArrayList<String>();
        
        ConnectionCallback.execute(source, new DatabaseMetaDataCallback() {
            
            @Override
            public void execute(DatabaseMetaData meta) throws SQLException
            {
                ResultSet result;
                
                if(table.indexOf(".") > 0) result = meta.getColumns(null, table.substring(0, table.indexOf(".")), table.substring(table.indexOf(".")+1), column);
                else result = meta.getColumns(null, null, table, column);

                columnToStringList(result, 12, list);
            }
        });
        
        return list.getFirst();

    }

    @Override
    public String getDatabaseColumnComments(String schema, String table, String column) throws SQLException
    {
        StringBuffer buffer = new StringBuffer();
        
        ConnectionCallback.execute(source, new DatabaseMetaDataCallback() {
            
            @Override
            public void execute(DatabaseMetaData meta) throws SQLException
            {
                ResultSet result = meta.getColumns(null, schema, table, column);

                if(result.next()) buffer.append(result.getString(12));

                result.close();
            }
        });
        
        return buffer.toString();
    }

    @Override
    public String[] getDatabaseSchemaList() throws SQLException
    {
        var list = new ArrayList<String>();
        
        ConnectionCallback.execute(source, new DatabaseMetaDataCallback() {
            
            @Override
            public void execute(DatabaseMetaData meta) throws SQLException
            {
                columnToStringList(meta.getSchemas(), 1, list);
            }
        });
        
        return list.toArray(String[]::new);
    }

    @Override
    public TableModel performTableSQLQuery(String query) throws SQLException
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private class ResultsIterator implements Iterator, InterruptibleProcess
    {
        private Connection connection;
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
                    connection = source.getConnection();
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
                            connection.close();
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
                        connection.close();
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
    
    @Override
    public Iterator executeQuery(String query) throws SQLException
    {
        return new ResultsIterator(query); //TODO: Leak
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public Statement createStatement() throws SQLException
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int executeSQLChange(String query) throws SQLException
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TableModel getTableColumnInfo(DBObject dbo) throws SQLException
    {
        Object[] columnNames = {"Name", "Type", "Length", "Nullable?", "Default"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            Class[] types = new Class[]  {
                java.lang.String.class, 
                java.lang.String.class, 
                java.lang.Integer.class, 
                java.lang.Boolean.class,
                java.lang.String.class
            };
            
            public Class getColumnClass(int columnIndex)
            {
                return types[columnIndex];
            }
            
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        
        ConnectionCallback.execute(source, new DatabaseMetaDataCallback() {
            
            @Override
            public void execute(DatabaseMetaData meta) throws SQLException
            {
                ResultSet result;

                if(dbo.getSchema() != null && dbo.getSchema().trim().length() > 0) result = meta.getColumns(null, dbo.getSchema(), dbo.getName(), null);
                else result = meta.getColumns(null, null, dbo.getName(), null);

                while(result.next())
                {
                    Object[] row = {result.getString("COLUMN_NAME"), result.getString("TYPE_NAME"), result.getInt("COLUMN_SIZE"), (result.getInt("NULLABLE") > 0), result.getString("COLUMN_DEF")};

                    model.addRow(row);
                }
            }
        });
        
        return model;
    }

    @Override
    public TableModel getTablePrimaryKeyInfo(String table) throws SQLException
    {
        Object[] columnNames = {"Column", "Key Order"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            
            Class[] types = new Class[] {
                java.lang.String.class, java.lang.Integer.class
            };
            
            public Class getColumnClass(int columnIndex)
            {
                return types[columnIndex];
            }
            
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        
        ConnectionCallback.execute(source, new DatabaseMetaDataCallback() {
            
            @Override
            public void execute(DatabaseMetaData meta) throws SQLException
            {
                ResultSet result;
        
                if(table.indexOf(".") > 0) result = meta.getPrimaryKeys(null, table.substring(0, table.indexOf(".")), table.substring(table.indexOf(".")+1));
                else result = meta.getPrimaryKeys(null, null, table);

                while(result.next())
                {
                    Object[] row = {result.getString("COLUMN_NAME"), result.getInt("KEY_SEQ")};

                    model.addRow(row);
                }
            }
        });
        
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

    @Override
    public TableModel getTableReferences(String table) throws SQLException
    {
        Object[] columnNames = {"Key Column", "Table", "Column", "On Update", "On Delete", "Key Name", "Deferrable?"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            
            Class[] types = new Class[]
            {
                java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.Boolean.class
            };
            
            public Class getColumnClass(int columnIndex)
            {
                return types[columnIndex];
            }
            
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        
        ConnectionCallback.execute(source, new DatabaseMetaDataCallback() {
            
            @Override
            public void execute(DatabaseMetaData meta) throws SQLException
            {
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
            }
        });
        
        return model;
    }

    @Override
    public void openConnection() throws SQLException
    {

    }

    @Override
    public void closeConnection() throws SQLException
    {

    }

    @Override
    public void rollback() throws SQLException
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void commit() throws SQLException
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setAutoCommit(boolean auto) throws SQLException
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Profile getCurrentProfile()
    {
        return profile;
    }

    @Override
    public PreparedStatement createPreparedStatement(String statement) throws SQLException
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public SQLClientHandler getConnection()
    {
        try
        {
            return new ConnectionClient(this, source.getConnection());
        }
        catch(SQLException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
