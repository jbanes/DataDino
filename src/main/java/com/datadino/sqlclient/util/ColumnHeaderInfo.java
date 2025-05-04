/*
 * ColumnHeaderEvent.java
 *
 * Created on July 27, 2003, 11:24 PM
 */

package com.datadino.sqlclient.util;

import java.io.Serializable;

import javax.swing.table.*;

/**
 *
 * @author  jbanes
 */
public class ColumnHeaderInfo implements Serializable
{   
    private Object[] names;
    private int[] widths;
    private int[] indexes;
    
    public ColumnHeaderInfo(TableColumnModel model)
    {
        TableColumn column;
        
        names = new Object[model.getColumnCount()];
        widths = new int[model.getColumnCount()];
        indexes = new int[model.getColumnCount()];
        
        for(int i=0; i<model.getColumnCount(); i++)
        {
            column = model.getColumn(i);
            
            names[i] = column.getHeaderValue();
            widths[i] = column.getWidth();
            indexes[i] = column.getModelIndex();
        }
    }
    
    public void restore(TableColumnModel model)
    {
        TableColumn column;
        
        for(int i=0; i<model.getColumnCount() && i<names.length; i++)
        {
            try
            {
                column = model.getColumn(model.getColumnIndex(names[i]));

                column.setWidth(widths[i]);
                column.setPreferredWidth(widths[i]);
                //if(column.getModelIndex() != indexes[i]) 
                model.moveColumn(model.getColumnIndex(names[i]), i);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
