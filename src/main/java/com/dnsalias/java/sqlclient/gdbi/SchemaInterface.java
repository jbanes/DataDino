/*
 * SchemaInterface.java
 *
 * Created on September 4, 2002, 1:27 AM
 */

package com.dnsalias.java.sqlclient.gdbi;

import com.dnsalias.java.sqlclient.SQLClientHandler;
import java.sql.*;
import java.util.*;

import javax.swing.*;

import com.dnsalias.java.sqlclient.gdbi.panels.*;
import com.dnsalias.java.sqlclient.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class SchemaInterface
{
    private SQLClientHandler handler;
    
    public SchemaInterface(SQLClientHandler handler)
    {
        this.handler = handler;
    }
    
    public String[] getCategories() throws SQLException
    {
        return handler.getDatabaseTableTypes();
    }

    public Iterator getDBObjects(String category, String schemaFilter) throws SQLException
    {
        return new TableIterator(handler.getConnection(), category, schemaFilter);
    }
    
    public String[] getCategoryTabs(String category) throws SQLException
    {
        if(!ApplicationSettings.getInstance().isRegisteredVersion())
        {
            return new String[]{"Data", "Columns"};
        }
        else 
        {
            if(category.indexOf("TABLE") >= 0) return new String[]{"Data", "Columns", "Keys", "Indexes", /*"Relationships",*/ "Source"};
            else if(category.indexOf("INDEX") >= 0) return new String[]{"Columns", "Source"};
            else return new String[]{"Data", "Columns", "Source"};
        }
    }
    
    public DatabaseInterfacePanel getTabInterface(DBObject dbo, String tabName) throws SQLException
    {
        if(tabName.equalsIgnoreCase("Data")) return new TableDataPanel(dbo);
        if(tabName.equalsIgnoreCase("Columns")) return new ColumnsPanel(dbo);
        if(tabName.equalsIgnoreCase("Keys")) return new KeysPanel(dbo);
        if(tabName.equalsIgnoreCase("Indexes")) return new IndexPanel(dbo);
        //if(tabName.equalsIgnoreCase("Relationships")) return new RelationshipPanel(dbo);
        if(tabName.equalsIgnoreCase("Source")) return new SourcePanel(dbo);

        return new NYIPanel();
    }
    
    private class NYIPanel implements DatabaseInterfacePanel
    {
        public void deactivate() {}
        
        public JComponent getPanel()
        {
            return new JLabel("Not yet implemented", JLabel.CENTER);
        }
        
        public boolean isSaved() { return true; }
        
        public void activate() {}
        
        public void saveChanges() {}
        
    }
    
    public String[] getSchemas() throws SQLException
    {
        return handler.getDatabaseSchemaList();
    }
    
    private class TableIterator implements Iterator
    {
        private SQLClientHandler handler;
        private ResultSet set;
        private boolean next;
        private String category;
        private String schemaFilter;
        
        boolean showSchema;
        
        char leftdelim = '"';
        char rightdelim = '"';
        
        public TableIterator(SQLClientHandler handler, String category, String schemaFilter) throws SQLException
        {
            DatabaseMetaData meta = handler.getMetaData();
            
            category = category.trim();
            this.handler = handler;
            this.category = category;
            this.schemaFilter = schemaFilter;
            this.leftdelim = SQLNormalizer.getLeftDelimeter(handler);
            this.rightdelim = SQLNormalizer.getRightDelimeter(handler);
            
            showSchema = (schemaFilter == null || schemaFilter.indexOf('%') >= 0);
            
            System.out.println("Category: ["+category+"]"+":"+Integer.toHexString((int)' ')+":"+Integer.toHexString((int)category.charAt(category.length()-1)));
            
            set = meta.getTables(null, schemaFilter, null, new String[]{category});
            next = set.next();
        }
        
        public boolean hasNext()
        {
            return next;
        }
        
        public Object next()
        {
            DBObject dbo = null;
            
            if(next)
            {
                try
                {
                    dbo = new DBObject(set.getString("TABLE_CAT"), category, set.getString("TABLE_SCHEM"), set.getString("TABLE_NAME"));
                    
                    dbo.setLeftDelimeter(leftdelim);
                    dbo.setRightDelimeter(rightdelim);
                    
                    if(!showSchema) dbo.setPrintSchema(false);
                    
                    next = set.next();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                    return null;
                }
                
                if(!next)
                {
                    try
                    {
                        set.close();
                        handler.completeOperation();
                    }
                    catch(SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            
            return dbo;
        }
        
        public void remove()
        {
            try
            {
                next = false;
                set.close();
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}
