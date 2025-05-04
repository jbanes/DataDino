/*
 * TableCellView.java
 *
 * Created on April 18, 2004, 10:19 PM
 */

package com.datadino.graphing;

import java.awt.*;

import org.jgraph.*;
import org.jgraph.graph.*;

import com.datadino.dbo.*;
import com.datadino.graphing.renderer.*;

/**
 *
 * @author  jbanes
 */
public class TableCellView extends VertexView
{
    private TableGraphCell tableCell;
    private TableCellRenderer renderer;
    
    public TableCellView(TableGraphCell cell, JGraph graph, CellMapper cm)
    {
        super(cell, graph, cm);
        
        this.tableCell = cell;
        this.renderer = new TableCellRenderer();
    }
    
    public Table getTable()
    {
        return tableCell.getTable();
    }
    
    public Dimension getPreferredSize()
    {
        return renderer.getPreferredSize(getTable());
    }
    
    public CellViewRenderer getRenderer() 
    {
        return renderer;
    }
}
