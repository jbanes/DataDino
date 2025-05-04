/*
 * DBTableModel.java
 *
 */

package com.dnsalias.java.sqlclient;

import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import com.dnsalias.java.sqlclient.dialog.*;
import com.dnsalias.java.sqlclient.drivers.*;
import com.dnsalias.java.sqlclient.gdbi.*;
import com.dnsalias.java.sqlclient.util.*;

import com.datadino.sqlclient.ui.*;
import com.datadino.sqlclient.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class DBTableModel extends AbstractTableModel implements InterruptibleProcess
{
    private Vector cache = new Vector();
    private String[] names = new String[0];
    private Class[] classes = new Class[0];
    private int[] types = new int[0];
    private boolean[] asobject = new boolean[0];
    private boolean[] nullable = new boolean[0];
    private ResultSet set = null;
    private Vector updated = new Vector();
    
    private int last = -1;
    private int columnSize = 0;
    private int extra = 0;
    private boolean next = true;
    private int totalRows = 0;
    
    private JComponent comp = null;
    private CacheThread thread = new CacheThread();
    
    private static Icon rotate = new ImageIcon(DBTableModel.class.getResource("/images/rotate.png"));
    
    private boolean cancelLoad = false;
    
    /** Creates new DBTableModel */
    public DBTableModel() 
    {
    }

    public void dispose()
    {
        cache.removeAllElements();
        totalRows = 0;
        last = 0;
        extra = 0;
    }
    
    public boolean isCellEditable(int row, int col)
    {
        return true;
    }
    
    public void commitChanges(SQLClientHandler handler, DBObject dbo) throws SQLException
    {
        DatabaseMetaData meta = handler.getMetaData();
        ResultSet rs;
        PreparedStatement statement;
        Vector keys = new Vector();
        
        String sql;
        Record record;
        boolean found;
        int col;
        
        if(dbo.getSchema() == null || dbo.getSchema().length() > 0) rs = meta.getPrimaryKeys(null, dbo.getSchema(), dbo.getName());
        else rs = meta.getPrimaryKeys(null, null, dbo.getName());
        
        while(rs.next()) keys.add(rs.getString(4));
        
        rs.close();
        
        //runCheck();
        
        if(updated.isEmpty()) return;
        
        if(keys.isEmpty())
        {
            throw new SQLException("Editing is not supported on non-keyed tables!");
        }
        
        for(int records=0; records<updated.size(); records++)
        {
            sql = "update "+dbo.getDelimitedString()+" set ";
            record = (Record)updated.get(records);
            col = 0;
            found = false;

            for(int i=0; i<names.length; i++)
            {
                if(record.isChanged(i))
                {
                    if(found) sql += ", ";
                
                    sql += SQLNormalizer.columnName(names[i], handler)+" = ?";
                    found = true;
                }
            }

            sql += " where ";
            
            for(int i=0; i<keys.size(); i++)
            {
                if(i > 0) sql += " and ";
                
                sql += SQLNormalizer.columnName(keys.get(i).toString(), handler)+" = ?";
            }

            System.out.println(sql);
            
            statement = handler.createPreparedStatement(sql);
            
            for(int i=0, j=0; i<names.length; i++)
            {
                if(record.isChanged(i))
                {
                    statement.setObject(col+1, record.getColumn(i).getValue());
                    col++;
                }
            }
            
            for(int i=0; i<keys.size(); i++)
            {
                int index = -1;
                
                for(int j=0; j<names.length; j++)
                {
                    if(names[j].equals(keys.get(i))) index = j;
                }
                
                statement.setObject(col+i+1, record.getOriginalColumn(index).getValue());
            }
            
            statement.execute();
        }
    }
    
    private void runCheck() throws SQLException
    {
        Record record;
        
        for(int i=0; i<cache.size() && ((Record)cache.elementAt(i)).getID() < 1; i++)
        {
            record = (Record)cache.elementAt(i);
            
            for(int col=0; col<record.getTotalColumns(); col++)
            {
                if(record.getColumn(col).getValue() == null && !nullable[col])
                {
                    throw new SQLException("Error row "+i+". "+names[i]+" cannot be null.");
                }
            }
        }
    }
    
    public boolean isUpdated()
    {
        return (updated.size() > 0 || extra > 0);
    }
    
    public void setValueAt(Object value, int row, int col)
    {
        try
        {
            Record record = null;
            
            if(last >= 0 && row >= last+extra) return;
        
            if(row < cache.size()) record = (Record)cache.elementAt(row);
        
            if(record == null) 
            {
                //cache(row+1, 100);
                thread.cacheData(row+1, 100);
                
                while(cache.size() <= row)
                {
                    try{Thread.sleep(100);} catch(InterruptedException e) {}
                }
                
                setValueAt(value, row, col);
            }
            else
            {
                record.updateColumn(col, value);
                
                if(!updated.contains(record) && record.getID() != 0) updated.add(record);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            ErrorReport.displayError(e, new Frame());
            return;
        }
        
        this.fireTableCellUpdated(row, col);
    }
    
    public int getColumnCount()
    {
        if(set == null) return 0;
        
        return columnSize;
    }
    
    public int getRowCount()
    {
        if(set == null) return 0;
        
        if(last >= 0) return last+extra;
        
        //return Math.max(totalRows+extra, cache.size()+extra); //+((totalRows < 1 || (next && cache.size() == totalRows)) ? 100 : 0));
        
        synchronized(cache)
        {
            return Math.max(totalRows+extra, cache.size()+extra);
        }
    }
    
    public void setTotalRows(int totalRows)
    {
        this.totalRows = totalRows;
    }
    
    public void setComponent(JComponent comp)
    {
        this.comp = comp;
    }
    
    public JComponent getComponent()
    {
        return comp;
    }
    
    public int addRow()
    {
        synchronized(this)
        {
            Record record = new Record(0);

            Column[] columns = new Column[columnSize];

            for(int col=0; col<columnSize; col++)
            {
                columns[col] = new ObjectColumn(ClassTranslator.getDefault(getColumnClass(col)), record);
            }

            record.setColumns(columns);
            cache.insertElementAt(record, 0);

            extra++;
        }
        
        fireTableDataChanged();
        return 0;
    }
    
    public Object getValueAt(int row, int column)
    {
        try
        {
            Record record = null;
            
            if(last >= 0 && row >= last+extra) return null;
        
            if(row < cache.size()) record = (Record)cache.elementAt(row);
        
            if(record == null) 
            {
                thread.cacheData(row+1, 100);
                
                if(last > 0) fireTableDataChanged();
                
                return null;
            }
            
            if(row+1 >= cache.size()) thread.cacheData(row+1, 100);
            
            return ((Column)record.getColumn(column)).getValue();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            ErrorReport.displayError(e, new Frame());
            return null;
        }
    }
    
    public String getColumnName(int index)
    {
        return names[index];
    }
    
    public void setResultSet(ResultSet set) throws SQLException
    {
        setResultSet(set, false);
    }
    
    public void setResultSet(ResultSet set, boolean loadAll) throws SQLException
    {
        this.set = set;
        
        ResultSetMetaData meta = set.getMetaData();
        
        columnSize = meta.getColumnCount();
        
        names = new String[columnSize];
        classes = new Class[columnSize];
        types = new int[columnSize];
        asobject = new boolean[columnSize];
        nullable = new boolean[columnSize];
        
        for(int i=0; i<names.length; i++)
        {
            names[i] = meta.getColumnName(i+1);
            types[i] = meta.getColumnType(i+1);
            asobject[i] = ClassTranslator.getAsObject(types[i]);
            nullable[i] = ((meta.isNullable(i+1) != meta.columnNoNulls) || meta.isAutoIncrement(i+1));
        }
        
        if(loadAll)
        {
            while(next && !cancelLoad) cache(cache.size(), 10);
            
            if(next) set.close();            
            
            last = cache.size();
            next = false;
        }
        else
        {
            thread.cacheData(1, 1);
        }
    }
    
    public Class getColumnClass(int col)
    {
        Class ret = classes[col];
        
        if(ret == null) ret = ClassTranslator.translateSQLType(types[col]);
        
        if(ret.getName().equals("oracle.sql.TIMESTAMP")) ret = java.sql.Timestamp.class;
        
        if(ret == null) ret = String.class;
        
        return ret;
    }
    
    private synchronized void cache(int needed, int length) throws SQLException
    {
        ResultSetMetaData meta = set.getMetaData();
        boolean isLast = false;
        Object value;
        String oldtext = null;
        
        int cacheStart = cache.size();
        int lastid = 1;
        
        if(cache.size() > 0) lastid = ((Record)cache.elementAt(cache.size()-1)).getID()+1;
        
        while(needed > lastid+length) length += 100;
        
        if(comp != null && cacheStart >= 100) JStatusBar.setText(comp, "Loading...", rotate, 0);
        if(comp != null) oldtext = JStatusBar.getText(comp, 2);
        
        for(int i=0; i<length && (next = set.next()) && !cancelLoad; i++, cacheStart++, lastid++)
        {   
            Record record = new Record(lastid);
            Column[] columns = new Column[columnSize];
            
            for(int col=0; col<columnSize; col++)
            {
                try
                {
                    if(asobject[col]) value = set.getObject(col+1);
                    else value = set.getString(col+1);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    if(asobject[col]) value = set.getString(col+1);
                    else value = set.getObject(col+1);
                }
                
                if(value instanceof byte[]) value = new String((byte[])value);
                
                if(classes[col] == null && value != null) classes[col] = value.getClass();
                
                columns[col] = new ObjectColumn(value, record);
            }
            
            record.setColumns(columns);
            cache.add(cacheStart, record);
            
            if(i%100 == 0 && cache.size() >= 100 && comp != null) JStatusBar.setText(comp, "Loaded "+cache.size()+" rows", 2);
        }
        
        if(!next) 
        {
            last = lastid-1;
            totalRows = cache.size();
        }
        
        if(comp != null) JStatusBar.setText(comp, "Ready", 0);
        if(comp != null) JStatusBar.setText(comp, oldtext, 2);

        fireTableDataChanged();
        
        //if(cache.size() > totalRows || (!next && cache.size() != totalRows) || (cache.size() == totalRows && next)) fireTableDataChanged();
        //else fireTableRowsUpdated(Math.max(0, cache.size()-length), cache.size()-1);
    }
    
    public void finalize()
    {
        try
        {
            Statement statement = set.getStatement();
            
            set.close();
            if(statement != null) statement.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void stop() 
    {
        System.out.println("Cancelling load!");
        cancelLoad = true;
    }
    
    public class CacheThread implements Runnable
    {
        private int needed;
        private int length;
        private boolean ready = true;
        
        private int requested = 0;
        
        public synchronized void cacheData(int needed, int length)
        {
            int lastid = 1;
            
            this.needed = needed;
            this.length = length;
            
            while(this.needed > lastid+this.length) this.length += 100;
            
            if(requested > needed || !next) return;
            
            while(!ready) 
            {
                try{Thread.sleep(100);} catch(InterruptedException e) {}
            }
            
            ready = false;
            ThreadPool.run(this);
        }
        
        public void run()
        {
            int needed = this.needed;
            int length = this.length;
        
            this.requested = requested+length;
            
            ready = true;
            
            try
            {
                cache(needed, length);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                ErrorReport.displayError(e, new Frame());
                return;
            }
        }
    }
}
