/*
 * Test.java
 *
 * Created on April 18, 2004, 11:06 PM
 */

package com.datadino.graphing;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import org.jgraph.*;
import org.jgraph.graph.*;

import com.datadino.dbo.*;
import com.datadino.graphing.renderer.*;

/**
 *
 * @author  jbanes
 */
public class Test
{
    public static void main(String[] args)
    {
        // Construct Model and Graph
        //
        GraphModel model = new DefaultGraphModel();
        Diagram graph = new Diagram(model);
        
        Table table1 = getTable1();
        Table table2 = getTable2(table1);
        
        int x = 0;
        
//        Map attributes = new Hashtable();
//        
//        TableCellRenderer renderer = new TableCellRenderer();
//        
//        Dimension dim1 = renderer.getPreferredSize(table1);
//        Dimension dim2 = renderer.getPreferredSize(table2);
//        
//        Rectangle table1Bounds = new Rectangle(10, 10, dim1.width, dim1.height);
//        Rectangle table2Bounds = new Rectangle(50+dim1.width, 10, dim2.width, dim2.height);
//        
//        TableGraphCell table1Cell = getCell(table1, table1Bounds, attributes);
//        TableGraphCell table2Cell = getCell(table2, table2Bounds, attributes);
        
        graph.setSelectNewCells(false);
        graph.setGridMode(graph.LINE_GRID_MODE);
        graph.setGridVisible(true);

        graph.add(table1);
        graph.add(table2);

//        connect(table2Cell, table1Cell, model, attributes);

        // Show in Frame
        //
        JFrame frame = new JFrame();
        frame.getContentPane().add(new JScrollPane(graph));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static TableGraphCell getCell(Table table, Rectangle bounds, Map attributes)
    {
//        Map tableAttrib = GraphConstants.createMap();
        Map tableAttrib = new Hashtable();
        TableGraphCell tableCell = new TableGraphCell(table);
        
        GraphConstants.setBounds(tableAttrib, bounds);
        
        attributes.put(tableCell, tableAttrib);
        
        return tableCell;
    }
    
    public static void connect(TableGraphCell source, TableGraphCell target, GraphModel model, Map attributes)
    {
        DefaultEdge edge = new DefaultEdge();
        DefaultPort sourcePort = new DefaultPort();
        DefaultPort targetPort = new DefaultPort();
        
//        Map edgeAttrib = GraphConstants.createMap();
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
    
    public static Table getTable1()
    {
        Table table = new Table("Table 1");
        PrimaryKey key;
        
        table.add(new Column("id", "INT", -1));
        table.add(new Column("Name", "VARCHAR", 255));
        table.add(new Column("Address", "VARCHAR", 255));
        
        key = new PrimaryKey(table.getColumn(0));
        
        table.setPrimaryKey(key);
        
        return table;
    }
    
    public static Table getTable2(Table foreign)
    {
        Table table = new Table("Table 2");
        ForeignKey key;
        
        table.add(new Column("id", "INT", -1));
        table.add(new Column("AlternateName", "VARCHAR", 255));
        table.add(new Column("table1id", "INT", -1));
        
        key = new ForeignKey(table.getColumn(2), foreign);
        
        table.add(key);
        
        return table;
    }
}
