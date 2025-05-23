/*
 * ColumnsPanel.java
 *
 * Created on September 20, 2002, 8:00 PM
 */

package com.dnsalias.java.sqlclient.gdbi.panels;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.dnsalias.java.sqlclient.*;
import com.dnsalias.java.sqlclient.dialog.*;
import com.dnsalias.java.sqlclient.gdbi.*;
import com.dnsalias.java.sqlclient.ui.*;
import com.dnsalias.java.sqlclient.util.*;

import com.datadino.sqlclient.ui.*;
import com.datadino.sqlclient.dialog.*;

/**
 *
 * @author  jbanes
 */
public class ColumnsPanel extends JPanel implements DatabaseInterfacePanel, ListSelectionListener, ActionListener
{
    private static String[] toolbarNames = {"Add Column", "Delete Column", null, "Refresh"};
    private static String[] toolbarIcons = {"/toolbarButtonGraphics/table/ColumnInsertAfter16.gif", "/toolbarButtonGraphics/table/ColumnDelete16.gif", null, "/toolbarButtonGraphics/general/Refresh16.gif"};
    private static boolean[] toolbarEnabled = {true, false, false, true};
    
    private JButton[] toolbarButtons = new JButton[toolbarNames.length];
    private MultiplexedToolbar toolbar = new MultiplexedToolbar();
    
    private SQLClientHandler handler;
    private DBObject dbo;
    private PropertiesPanel.PropertyList[] properties;
    
    private PropertiesPanel comments = new PropertiesPanel();
    private boolean activated = false;
    
    private Icon rotate = new ImageIcon(getClass().getResource("/images/rotate.png"));
    
    private int querytime = 0;
    private int recordcount = 0;
    
    public ColumnsPanel(DBObject dbo) 
    {
        this.handler = SQLClientHandler.getCurrentHandler();
        this.dbo = dbo;
        
        initComponents(); 
        initButtons();
        
        comments.setBackground(Color.white);
        comments.setOpaque(true);
        
        add(toolbar.getToolbar(), BorderLayout.NORTH);
        
        setLayout(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        scrollColumns = new javax.swing.JScrollPane();
        columns = new javax.swing.JTable();
        infoPanel = new javax.swing.JPanel();
        columnInfoHeader = new javax.swing.JLabel();
        scrollInfo = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());

        scrollColumns.setPreferredSize(new java.awt.Dimension(300, 300));
        columns.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollColumns.setViewportView(columns);

        add(scrollColumns, java.awt.BorderLayout.CENTER);

        infoPanel.setLayout(new java.awt.BorderLayout());

        infoPanel.setBorder(new javax.swing.border.EtchedBorder());
        infoPanel.setMaximumSize(new java.awt.Dimension(50000, 300));
        infoPanel.setPreferredSize(new java.awt.Dimension(74, 300));
        columnInfoHeader.setText(" Column Info");
        columnInfoHeader.setBorder(new javax.swing.border.MatteBorder(new java.awt.Insets(0, 0, 1, 0), new java.awt.Color(180, 180, 180)));
        infoPanel.add(columnInfoHeader, java.awt.BorderLayout.NORTH);

        scrollInfo.setBorder(null);
        infoPanel.add(scrollInfo, java.awt.BorderLayout.CENTER);

        add(infoPanel, java.awt.BorderLayout.SOUTH);

    }//GEN-END:initComponents

    public void deactivate()
    {
    }    

    private void initButtons()
    {
        JButton button;
        ImageIcon icon;
        
        for(int i=0; i<toolbarIcons.length; i++)
        {
            if(toolbarIcons[i] == null) 
            {
                toolbar.addSeparator();
            }
            else
            {
                icon = new ImageIcon(getClass().getResource(toolbarIcons[i]));

                button = new JButton(icon);
                toolbarButtons[i] = button;

                button.setActionCommand(toolbarNames[i]);
                button.addActionListener(this);
                button.setToolTipText(toolbarNames[i]);
                button.setEnabled(toolbarEnabled[i]);

                toolbar.add(button);
            }
        }
    }
    
    public void doLayout()
    {
        Dimension dim = getSize();
        Dimension toolbarSize = toolbar.getToolbar().getPreferredSize();
        
        infoPanel.setBounds(2, dim.height-82, dim.width-4, 80);
        toolbar.getToolbar().setBounds(2, 2, dim.width-4, toolbarSize.height);
        scrollColumns.setBounds(2, toolbarSize.height+2, dim.width-4, dim.height-90-toolbarSize.height);
    }
    
    public JComponent getPanel()
    {
        return this;
    }    

    public boolean isSaved()
    {
        return true;
    }
    
    public void activate()
    {
        TableModel model;
        PreparedStatement statement;
        ResultSet result;
        String sqlsuffix;
        long timer;
        
        if(activated) 
        {
            displayMetrics();
            return;
        }
        
        try
        {
            JStatusBar.setText(this, "Loading...", rotate, 0);
            
            timer = System.currentTimeMillis();
            model = handler.getTableColumnInfo(dbo);
            
            columns.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            columns.getSelectionModel().addListSelectionListener(this);
            
            recordcount = model.getRowCount();
            
            columns.setModel(model);
            
            properties = new PropertiesPanel.PropertyList[columns.getRowCount()];
            
            for(int i=0; i<properties.length; i++) properties[i] = getProperties(i);
            
            querytime = (int)(System.currentTimeMillis()-timer);
            
            scrollInfo.setViewportView(comments);
            
            displayMetrics();
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
    
    public void refresh()
    {
        activated = false;
        activate();
    }
    
    private void displayMetrics()
    {
        JStatusBar.setText(this, recordcount+" column(s)", 1);
        JStatusBar.setText(this, querytime+" ms", 2);
    }
    
    public void saveChanges()
    {
        
    }
    
    private PropertiesPanel.PropertyList getProperties(int row) throws SQLException
    {
        String comments = handler.getDatabaseColumnComments(dbo.getSchema(), dbo.getName(), columns.getModel().getValueAt(row, 0).toString());
        PropertiesPanel.PropertyList properties = this.comments.createList();
        
        properties.addProperty("Comments", comments);
        
        return properties;
    }
    
    public void valueChanged(ListSelectionEvent evt)
    {
        if(columns.getSelectedRow() < 0)
        {
            //Disable the buttons
            toolbarButtons[1].setEnabled(false); 
            
            comments.removeAll();
            return;
        }
        
        try
        {
            //Enable the buttons
            toolbarButtons[1].setEnabled(true); 
            
            while(properties[columns.getSelectedRow()] == null) 
            {
                try{Thread.sleep(50);} catch(InterruptedException e) {}
            }
            
            if(scrollInfo.getViewport().getView() != comments) scrollInfo.setViewportView(comments);
            comments.display(properties[columns.getSelectedRow()]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            ErrorReport.displayError(e, this);
        }
    }
    
    private void addColumn()
    {
        Component comp = this;
        ColumnEditor editor;
        
        if(!ApplicationSettings.getInstance().isRegisteredVersion())
        {
            JOptionPane.showMessageDialog(this, "This is a feature of the Professional Edition.\nVisit www.datadino.com to upgrade today!", "", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        while(comp.getParent() != null) comp = comp.getParent();
        
        editor = new ColumnEditor(dbo.getDelimitedString(), (Frame)comp, false);
        editor.show();
    }
    
    private void deleteColumn()
    {
        SQLClientHandler handler = this.handler.getConnection();
        int selected = columns.getSelectedRow();
        String column;
        String sql;
        
        
        if(!ApplicationSettings.getInstance().isRegisteredVersion())
        {
            JOptionPane.showMessageDialog(this, "This is a feature of the Professional Edition.\nVisit www.datadino.com to upgrade today!", "", JOptionPane.INFORMATION_MESSAGE);
            
            try { handler.completeOperation(); } catch(Exception e) { e.printStackTrace(); }
            
            return;
        }
        
        if(selected < 0) return;
        
        try
        {
            column = columns.getValueAt(selected, 0).toString();
            sql = "ALTER TABLE "+dbo.getDelimitedString()+" DROP COLUMN "+SQLNormalizer.columnName(column, handler);

            System.out.println(sql);

            handler.executeSQLChange(sql);
            handler.completeOperation();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            ErrorReport.displayError(e, this);
        }
        
        refresh();
    }
    
    public void actionPerformed(ActionEvent evt)
    {
        if(evt.getActionCommand().equals(toolbarNames[0])) addColumn();
        if(evt.getActionCommand().equals(toolbarNames[1])) deleteColumn();
        if(evt.getActionCommand().equals(toolbarNames[3])) refresh();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable columns;
    private javax.swing.JScrollPane scrollInfo;
    private javax.swing.JScrollPane scrollColumns;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLabel columnInfoHeader;
    // End of variables declaration//GEN-END:variables

}
