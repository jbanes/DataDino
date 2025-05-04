/*
 * SettingsPanel.java
 *
 * Created on May 25, 2002, 8:38 PM
 */

package com.dnsalias.java.sqlclient.util;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class SettingsPanel extends JPanel
{
    private JSplitPane split;
    
    public SettingsPanel(JSplitPane split) 
    {
        this.split = split;
        
        add(split);
        setLayout(null);
    }
    
    public void doLayout()
    {
        int height = ((Container)split.getRightComponent()).getComponentCount()*23+3;
        
        if(getComponentCount() > 0)
        {
            getComponent(0).setBounds(0, 0, getWidth(), height);
        }
    }
}
