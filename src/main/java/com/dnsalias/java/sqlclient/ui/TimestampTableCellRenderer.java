/*
 * TimestampTableCellRenderer.java
 *
 * Created on August 4, 2002, 3:42 PM
 */

package com.dnsalias.java.sqlclient.ui;

import java.awt.*;
import java.lang.reflect.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class TimestampTableCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor
{
    private DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    private JTextField field = new JTextField();
    
    public TimestampTableCellRenderer()
    {
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {   
        Class clazz = null; 
        
        if(value != null) clazz = value.getClass();
        
        if(clazz != null && clazz.getName().equals("oracle.sql.TIMESTAMP"))
        {
            try
            {
                Method method = clazz.getMethod("timestampValue", new Class[0]);
                Timestamp timestamp = (Timestamp)method.invoke(value, new Object[0]);

                return renderer.getTableCellRendererComponent(table, timestamp, isSelected, hasFocus, row, column);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
       
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
    public Object getCellEditorValue()
    {
        return field.getText();
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        Class clazz = null; 
        
        if(value != null) clazz = value.getClass();
        
        if(clazz != null && clazz.getName().equals("oracle.sql.TIMESTAMP"))
        {
            try
            {
                Method method = clazz.getMethod("timestampValue", new Class[0]);
                Timestamp timestamp = (Timestamp)method.invoke(value, new Object[0]);

                field.setText(timestamp.toString());
                return field;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                
                field.setText(value.toString());
                return field;
            }
        }
        
        if(value != null) field.setText(value.toString());
        else field.setText("");
        
        return field;
    }
}
