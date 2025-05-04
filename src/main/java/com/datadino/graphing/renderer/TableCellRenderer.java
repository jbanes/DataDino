/*
 * TableCellRenderer.java
 *
 * Created on April 18, 2004, 10:49 PM
 */

package com.datadino.graphing.renderer;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import org.jgraph.*;
import org.jgraph.graph.*;

import com.datadino.dbo.*;
import com.datadino.graphing.*;

/**
 *
 * @author  jbanes
 */
public class TableCellRenderer extends JPanel implements CellViewRenderer
{
    private Table table;
    
    private JGraph graph;
    private boolean focused;
    private boolean selected;
    private boolean preview;
    
    
    public TableCellRenderer()
    {
        setOpaque(true);
        setBackground(new Color(255, 255, 204));
        setBorder(new LineBorder(Color.BLACK, 1));
    }
    
    public Component getRendererComponent(JGraph graph, CellView view, boolean selected, boolean focused, boolean preview)
    {
        TableGraphCell tableCell = (TableGraphCell)view.getCell();
            
        table = tableCell.getTable();


        this.graph = graph;
        this.focused = focused;
        this.selected = selected;
        this.preview = preview;

        setComponentOrientation(graph.getComponentOrientation());

        //installAttributes(view, graph.getModel().getAttributes(cellview.getCell()));

        return this;
    }
    
    public Dimension getPreferredSize(Table table)
    {
        Dimension dim = new Dimension();
        
        FontMetrics fm = getFontMetrics(getFont());
        int colTextHeight = fm.getMaxAscent() - fm.getMaxDescent() + 2;

        int xCursor = 16 + 6 + fm.stringWidth(table.getName());
        int yCursor = 4 + 3 + colTextHeight + 2;
        
        for(int i=0; i<table.getColumnCount(); i++)
        {
            if(fm.stringWidth(table.getColumn(i).getName()) + 16 + 2 > xCursor) xCursor = fm.stringWidth(table.getColumn(i).getName()) + 16 + 6;
            yCursor += colTextHeight + 2;
        }
        
        dim.width = xCursor;
        dim.height = yCursor;
        
        return dim;
    }
    
    public int getPointerY(int column)
    {
        FontMetrics fm = getFontMetrics(getFont());
        int colTextHeight = fm.getMaxAscent() - fm.getMaxDescent() + 2;

        int yCursor = 4 + 3 + colTextHeight + 2;
        
        for(int i=0; i<column; i++)
        {
            yCursor += colTextHeight + 2;
        }
        
        yCursor += colTextHeight/2;
        
        return yCursor;
    }
    
    public void paint(Graphics g) 
    {
        super.paintBorder(g);
        super.paint(g);

        Graphics2D g2 = (Graphics2D)g;
        Dimension dim = getSize();
        int width = dim.width;
        int height = dim.height;

        FontMetrics fm = g.getFontMetrics();
        Font normalFont = g.getFont();
        Font titleFont = normalFont.deriveFont( Font.BOLD );
        int colTextHeight = fm.getMaxAscent() - fm.getMaxDescent() + 2 ;

        int xCursor = (width - fm.stringWidth( table.getName() )) / 2;
        int yCursor = colTextHeight + 2;

        Stroke lineStroke = new BasicStroke( 1.0f );
        Stroke textStroke = new BasicStroke( 1.0f );

        // Draw the title box with name centered
        g2.setStroke( textStroke );
        //g.setColor( bordercolor );
        //g.setFont( titleFont );
        g.drawString( table.getName(), xCursor, yCursor );

        g2.setStroke( lineStroke );
        yCursor += 3;
        g.drawLine( 0, yCursor, width, yCursor );

            
        // Draw the columns
        g.setColor( getForeground());
        g2.setStroke( textStroke );
        g.setFont( normalFont );
        
        for( int i = 0; i < table.getColumnCount(); i++) 
        {
            Column column = table.getColumn(i);
            
            yCursor += 2 + colTextHeight;
            
//            if( col.isKey()) {
//                KEY_ICON.paintIcon( this, g, COLUMN_ICON_LEFT, 
//                                    yCursor + keyIconYOffset );
//            }
            
            g.drawString(column.getName(), 16 + 2, yCursor);

            // If we're below the the bottom of the box
            if( yCursor > height ) 
            {
                // Draw the more-content arrow
                int right = width - 4;
                int left = right - 8;
                int center = right - 8/2;
                int bottom = height - 4;
                int top = bottom - 7;
                int[] xPts = {center, left, right};
                int[] yPts = {bottom, top, top};
                g.fillPolygon( xPts, yPts, 3 );

                break;
            }
        }

        
        

//        if (selected || hasFocus) 
//        {
//
//            // Draw selection/highlight border
//            ((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
//            
//            if (hasFocus) g.setColor(graph.getGridColor());
//            else if (selected) g.setColor(graph.getHighlightColor());
//            
//            Dimension d = getSize();
//            
//            g.drawRect(0, 0, d.width - 1, d.height - 1);
//        }
    }
}
