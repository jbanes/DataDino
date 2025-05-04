/*
 * NewRecordHandler.java
 *
 * Created on July 23, 2001, 8:54 AM
 */

package com.dnsalias.java.sqlclient;

import java.awt.*;
import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.dnsalias.java.sqlclient.dialog.*;
import com.dnsalias.java.sqlclient.drivers.*;
import com.dnsalias.java.sqlclient.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class NewRecordHandler implements TableModelListener
{
    private int row;
    private String table;
    private SQLClientHandler handler;
    private Profile profile;

    private ListSelectionModel selectionModel = null;
    private TableModel tableModel = null;
    
    /** Creates new NewRecordHandler */
    public NewRecordHandler(int row, String table, SQLClientHandler handler) 
    {
        this.row = row;
        this.table = table;
        this.handler = handler;
        this.profile = ((StandardClient)handler).getCurrentProfile();
    }

    public void add(TableModel model)
    {
        model.addTableModelListener(this);
        tableModel = model;
    }
    
    public void add(ListSelectionModel model)
    {
    }
    
    public void tableChanged(TableModelEvent event)
    {
        if(event.getFirstRow() != row) return;
    }
    
    public void commit() throws SQLException
    {
        if(tableModel == null) return;

        int columns = tableModel.getColumnCount();
        int totalValues = 0;
        String values = "";
        String columnNames = "";
        
        PreparedStatement statement;

        for(int i=0; i<columns; i++)
        {
            Object value = tableModel.getValueAt(row, i);
            String column = tableModel.getColumnName(i);

            if(value != null && !value.toString().trim().equals("")) 
            {
                if(i > 0 && columnNames.length() > 0) 
                {
                    values += ", ";
                    columnNames += ", ";
                }

                totalValues++;
                
                values += "?";
                columnNames += SQLNormalizer.columnName(column, handler);
            }
        }
        
        System.out.println("insert into "+table+" ("+columnNames+") values("+values+")");
        statement = handler.createPreparedStatement("insert into "+table+" ("+columnNames+") values("+values+")");
        
        for(int i=0, param=0; i<columns; i++)
        {
            Object value = tableModel.getValueAt(row, i);

            if(value != null && !value.toString().trim().equals("")) 
            {
                System.out.println(value.getClass()+":"+param);
                statement.setObject(param+1, value);
                param++;
            }
        }

        statement.executeUpdate();

        remove();
    }
    
    public void remove()
    {
        if(tableModel != null) tableModel.removeTableModelListener(this);
    }
    
    public void rowAdded()
    {
        row++;
    }
}
