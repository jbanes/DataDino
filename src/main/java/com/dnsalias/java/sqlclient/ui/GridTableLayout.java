/*
 * GridTableLayout.java
 *
 * Created on September 24, 2002, 10:50 PM
 */

package com.dnsalias.java.sqlclient.ui;

import java.awt.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class GridTableLayout implements LayoutManager
{
    int vcells = 0;
    int hcells = 0;
    int size = 0;
    
    int[] columns;
    int[] rows;
    
    public GridTableLayout(int hcells, int vcells) 
    {
        this.hcells = hcells;
        this.vcells = vcells;
        
        columns = new int[hcells];
        rows = new int[vcells];
    }

    
    public GridTableLayout(int hcells, int vcells, int size) 
    {
        this.hcells = hcells;
        this.vcells = vcells;
        this.size = size;
        
        columns = new int[hcells];
        rows = new int[vcells];
    }
    
    public void layoutContainer(Container container)
    {
        Dimension sizes;
        Component[] components = container.getComponents();
        
        int xloc = 0;
        int yloc = 0;
        
        for(int y=0; y<vcells; y++)
        {
            for(int x=0; x<hcells; x++)
            {
                if(y*hcells+x < components.length)
                {
                    sizes = components[y*hcells+x].getPreferredSize();
                    
                    if(sizes.width > columns[x]) columns[x] = sizes.width;
                    if(sizes.height > rows[y]) rows[y] = sizes.height;
                }
            }
        }
        
        for(int y=0; y<vcells; y++)
        {
            for(int x=0; x<hcells; x++)
            {
                if(y*hcells+x < components.length)
                {
                    components[y*hcells+x].setBounds(xloc, yloc, columns[x], rows[y]);
                }
                
                xloc += columns[x];
            }
            
            yloc += rows[y];
            xloc = 0;
        }
    }
    
    public Dimension preferredLayoutSize(Container container)
    {
        Dimension dim = new Dimension(0, 0);
        Dimension sizes;
        Component[] components = container.getComponents();
        
        for(int y=0; y<vcells; y++)
        {
            for(int x=0; x<hcells; x++)
            {
                if(y*hcells+x < components.length)
                {
                    sizes = components[y*hcells+x].getPreferredSize();
                    
                    if(sizes.width > columns[x]) columns[x] = sizes.width;
                    if(sizes.height > rows[y]) rows[y] = sizes.height;
                }
            }
        }
        
        for(int i=0; i<columns.length; i++) dim.width += columns[i];
        for(int i=0; i<rows.length; i++) dim.height += rows[i];
        
        return dim;
    }
    
    public void addLayoutComponent(String str, Component component)
    {
    }
    
    public void addLayoutComponent(Component component, Object obj)
    {
    }
    
    public Dimension minimumLayoutSize(Container container)
    {
        return preferredLayoutSize(container);
    }
    
    public Dimension maximumLayoutSize(Container container)
    {
        return preferredLayoutSize(container);
    }
    
    public void removeLayoutComponent(Component component)
    {
    }
}