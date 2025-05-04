/*
 * MultiplexedToolbar.java
 *
 * Created on December 8, 2002, 11:30 PM
 */

package com.dnsalias.java.sqlclient.ui;

import java.awt.*;

import javax.swing.*;

import com.dnsalias.java.sqlclient.util.*;

import com.datadino.sqlclient.ui.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class MultiplexedToolbar 
{
    private JComponent toolbar;
    private boolean macosx;
    
    public MultiplexedToolbar() 
    {
        //macosx = ApplicationSettings.getInstance().isMacOSX();
        
        //if(macosx) toolbar = new JToolBar();
        //else toolbar = new FancyToolbar();
        toolbar = new JToolBar();
    }
    
    public JComponent getToolbar()
    {
        return toolbar;
    }
    
    public void add(Component comp)
    {
        toolbar.add(comp);
    }
    
    public void addSeparator()
    {
        //if(macosx) ((JToolBar)toolbar).addSeparator();
        //else ((FancyToolbar)toolbar).addSeparator();
        ((JToolBar)toolbar).addSeparator();
    }
}
