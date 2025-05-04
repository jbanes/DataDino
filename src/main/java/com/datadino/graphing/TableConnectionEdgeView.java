/*
 * TableConnectionEdgeView.java
 *
 * Created on April 22, 2004, 8:42 PM
 */

package com.datadino.graphing;

import java.awt.*;

import org.jgraph.*;
import org.jgraph.graph.*;

import com.datadino.dbo.*;
import com.datadino.graphing.renderer.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author  jbanes
 */
public class TableConnectionEdgeView extends EdgeView
{
    private ForeignKey foreign;
    private PrimaryKey primary;
    
    private int fcolumn;
    private int pcolumn;
    
    public TableConnectionEdgeView(Object cell, JGraph graph, CellMapper mapper) 
    {
        super(cell, graph, mapper);
        
        update();
    }
    
    public Point2D getPoint(int index)
    {
        Point2D point = super.getPoint(index);
        TableCellView view;
        
        Rectangle2D bounds1;
        Rectangle2D bounds2;
        
        if(index == 0 && points.get(index) instanceof PortView)
        {
            bounds1 = ((PortView)points.get(0)).getParentView().getBounds();
            bounds2 = ((PortView)points.get(1)).getParentView().getBounds();
            
            if(foreign == null) loadColumns();
            
            view = (TableCellView)((PortView)points.get(index)).getParentView();
            
            if(bounds1.getX() > bounds2.getX())
            {
                point.setLocation(view.getBounds().getX(), view.getBounds().getY() + ((TableCellRenderer)view.getRenderer()).getPointerY(fcolumn));
            }
            else
            {
                point.setLocation(view.getBounds().getX() + view.getBounds().getWidth(), view.getBounds().getY() + ((TableCellRenderer)view.getRenderer()).getPointerY(fcolumn));
            }
            
            if(point.getY() > view.getBounds().getY() + view.getBounds().getHeight()) point.setLocation(point.getX(), view.getBounds().getY() + 10);
        }
        else if(index == 1 && points.get(index) instanceof PortView)
        {
            bounds1 = ((PortView)points.get(0)).getParentView().getBounds();
            bounds2 = ((PortView)points.get(1)).getParentView().getBounds();
            
            if(foreign == null) loadColumns();
            
            view = (TableCellView)((PortView)points.get(index)).getParentView();
            
            if(bounds1.getX() > bounds2.getX())
            {
                point.setLocation(view.getBounds().getX() + view.getBounds().getWidth(), view.getBounds().getY() + ((TableCellRenderer)view.getRenderer()).getPointerY(pcolumn));
            }
            else
            {
                point.setLocation(view.getBounds().getX(), view.getBounds().getY() + ((TableCellRenderer)view.getRenderer()).getPointerY(pcolumn));
            }
            
            if(point.getY() > view.getBounds().getY() + view.getBounds().getHeight()) point.setLocation(point.getX(), view.getBounds().getY() + 10);
        }
        
        return point;
    }
    
    private void loadColumns()
    {
        Table table1 = ((TableCellView)((PortView)points.get(0)).getParentView()).getTable();
        Table table2 = ((TableCellView)((PortView)points.get(1)).getParentView()).getTable();
        
        for(int i=0; i<table1.getForeignKeyCount(); i++)
        {
            if(table1.getForeignTable(i) == table2)
            {
                foreign = table1.getForeignKey(i);
                primary = table2.getPrimaryKey();
                
                fcolumn = table1.getForeignColumn(i);
                pcolumn = table2.getPrimaryColumn();
            }
        }
    }
}
