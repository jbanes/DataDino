/*
 * StatusBarPanel.java
 *
 * Created on August 8, 2003, 10:12 PM
 */

package com.datadino.sqlclient.ui;

import java.awt.*;

import javax.swing.*;

/**
 *
 * @author  jbanes
 */
public class StatusBarPanel extends JPanel implements StatusBarOwner 
{
    private JStatusBar statusBar;
    
    public StatusBarPanel(JStatusBar statusBar) 
    {
        this.statusBar = statusBar;
    }
    
    public StatusBarPanel(JStatusBar statusBar, LayoutManager layout) 
    {
        super(layout);
        
        this.statusBar = statusBar;
    }
    
    public JStatusBar getStatusBar() 
    {
        return statusBar;
    }
}
