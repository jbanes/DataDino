/*
 * Diagram.java
 *
 * Created on April 18, 2004, 8:37 PM
 */

package com.datadino.graphing;


import java.awt.*;
import java.util.*;

import javax.swing.*;

import org.jgraph.*;
import org.jgraph.graph.*;

import com.datadino.dbo.*;

/**
 *
 * @author  jbanes
 */
public class Diagram extends JGraph
{
    private ArrayList tables = new ArrayList();
    private ArrayList cells = new ArrayList();
    
    private GraphModel model;
    private Map attributes = new Hashtable();
    
    public Diagram()
    {
        this(new DefaultGraphModel());
    }
    
    public Diagram(GraphModel model)
    {
        super(model);
        
        this.model = model;
    }
    
    public void add(Table table)
    {
        TableGraphCell cell = getCell(table, new Rectangle(10, 10, 50, 50));
        
        if(tables.contains(table)) return;
        
        tables.add(table);
        cells.add(cell);
        
        for(int i=0; i<table.getForeignKeyCount(); i++)
        {
            if(!tables.contains(table.getForeignTable(i)))
            {
                add(table.getForeignTable(i));
            }
            
            connect(cell, (TableGraphCell)cells.get(tables.indexOf(table.getForeignTable(i))));
        }
    }
    
    private TableGraphCell getCell(Table table, Rectangle bounds)
    {
        Map tableAttrib = new Hashtable();
        TableGraphCell tableCell = new TableGraphCell(table);
        
        GraphConstants.setBounds(tableAttrib, bounds);
        
        attributes.put(tableCell, tableAttrib);
        
        return tableCell;
    }
    
    private void connect(TableGraphCell source, TableGraphCell target)
    {
        DefaultEdge edge = new DefaultEdge();
        DefaultPort sourcePort = new DefaultPort();
        DefaultPort targetPort = new DefaultPort();
        
        Map edgeAttrib = new Hashtable();
        
        int arrow = GraphConstants.ARROW_CLASSIC;
        
        
        source.add(sourcePort);
        target.add(targetPort);
        attributes.put(edge, edgeAttrib);
        
        // Set Arrow
        GraphConstants.setLineEnd(edgeAttrib , arrow);
        GraphConstants.setEndFill(edgeAttrib, true);
        GraphConstants.setLineStyle(edgeAttrib, GraphConstants.STYLE_ORTHOGONAL);

        ConnectionSet cs = new ConnectionSet(edge, sourcePort, targetPort);
        Object[] cells = new Object[]{edge, source, target};

        model.insert(cells, attributes, cs, null, null);
    }
    
    protected VertexView createVertexView(Object v, CellMapper cm)
    {
        VertexView view;
        
        if(v instanceof TableGraphCell) 
        {
            view = new TableCellView((TableGraphCell)v, this, cm);
        } 
        else 
        {
            view = super.createVertexView(this, cm, v);
        }

        return view;
    }
    
    protected EdgeView createEdgeView(Edge e, CellMapper cm) 
    {
	TableConnectionEdgeView view = new TableConnectionEdgeView(e, this, cm);

        //GraphConstants.setRouting(view.getAttributes(), new Routing());
        
        return view;
    }
}
