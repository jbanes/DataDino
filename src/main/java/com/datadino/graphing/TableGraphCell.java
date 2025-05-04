/*
 * TableGraphCell.java
 *
 * Created on April 18, 2004, 9:52 PM
 */

package com.datadino.graphing;

import org.jgraph.graph.*;

import com.datadino.dbo.*;

/**
 *
 * @author  jbanes
 */
public class TableGraphCell extends DefaultGraphCell
{
    protected Table table;
    
    public TableGraphCell(Table table)
    {
        this.table = table;
    }
    
    public Table getTable()
    {
        return table;
    }
}
