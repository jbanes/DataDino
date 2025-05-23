/*
 * RelationshipPanel.java
 *
 * Created on May 6, 2004, 7:42 PM
 */

package com.dnsalias.java.sqlclient.gdbi.panels;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.dnsalias.java.sqlclient.SQLClientHandler;
import com.dnsalias.java.sqlclient.dialog.*;
import com.dnsalias.java.sqlclient.gdbi.*;
import com.dnsalias.java.sqlclient.ui.*;
import com.dnsalias.java.sqlclient.util.*;

import com.datadino.dbo.*;
import com.datadino.sqlclient.ui.*;
import com.datadino.sqlclient.dialog.*;


/**
 *
 * @author  jbanes
 */
public class RelationshipPanel extends JPanel implements DatabaseInterfacePanel
{
    private static Icon rotate = new ImageIcon(RelationshipPanel.class.getResource("/images/rotate.png"));
    
    private SQLClientHandler handler;
    private DBObject dbo;
    private int depth = 1;
    
    private boolean activated = false;
    
    public RelationshipPanel(DBObject dbo) 
    {
        this.handler = SQLClientHandler.getCurrentHandler();
        this.dbo = dbo;
        
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        toolbar = new javax.swing.JToolBar();
        zoomin = new javax.swing.JButton();
        zoomout = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        diagram = new com.datadino.graphing.Diagram();

        setLayout(new java.awt.BorderLayout());

        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        zoomin.setText("Zoom In");
        zoomin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/ZoomIn16.gif")));
        toolbar.add(zoomin);

        zoomout.setText("Zoom Out");
        zoomout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/ZoomOut16.gif")));
        toolbar.add(zoomout);

        jButton3.setText("jButton3");
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Zoom16.gif")));
        toolbar.add(jButton3);

        add(toolbar, java.awt.BorderLayout.NORTH);

        diagram.setBorder(new javax.swing.border.EtchedBorder());
        diagram.setVerifyInputWhenFocusTarget(false);
        diagram.setMinimumMove(2);
        diagram.setBendable(false);
        diagram.setEditable(false);
        diagram.setCloneable(false);
        diagram.setDragEnabled(true);
        diagram.setDoubleBuffered(false);
        diagram.setDropEnabled(false);
        diagram.setGridVisible(true);
        diagram.setTolerance(2);
        diagram.setConnectable(false);
        add(diagram, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void addTable(Table table)
    {
        diagram.add(table);
        
        System.out.println(table);
        
        for(int i=0; i<table.getForeignKeyCount(); i++)
        {
            addTable(table.getForeignTable(i));
        }
    }
    
    private Table getTable(String schema, String name, int depth) throws SQLException
    {
        DatabaseMetaData meta = handler.getMetaData();
        Table table = new Table(name);
        
        Table foreign;
        ForeignKey key;
        
        ResultSet set = meta.getColumns(null, schema, name, "%");
        
        while(set.next())
        {
            table.add(new Column(set.getString("COLUMN_NAME"), set.getString("TYPE_NAME")));
        }
        
        set.close();
        
        if(depth < this.depth)
        {
            set = meta.getImportedKeys(null, schema, name);
            
            while(set.next())
            {
                table.add(new ForeignKey(table.getColumn(set.getString("FKCOLUMN_NAME")), getTable(set.getString("PKTABLE_SCHEM"), set.getString("PKTABLE_NAME"), depth+1)));
            }
            
            set.close();
        }
        
        return table;
    }
    
    public synchronized void activate() 
    {
        System.out.println("Activating Relationships for ["+dbo+"].");
        
        if(activated) 
        {
            JStatusBar.setText(this, "Ready", 0);
            return;
        }
        
        try
        {
            JStatusBar.setText(this, "Loading...", rotate, 0);
            
            addTable(getTable(dbo.getSchema(), dbo.getName(), 0));

            revalidate();
            
            JStatusBar.setText(this, "Ready", 0);
            
            activated = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            ErrorReport.displayError(e, this);
        }
    }    
    
    public void deactivate() 
    {
        
    }    
    
    public JComponent getPanel() 
    {
        return this;
    }
    
    public boolean isSaved() 
    {
        return true;
    }
    
    public void saveChanges() 
    {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.datadino.graphing.Diagram diagram;
    private javax.swing.JButton jButton3;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JButton zoomin;
    private javax.swing.JButton zoomout;
    // End of variables declaration//GEN-END:variables
    
}
