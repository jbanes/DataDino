/*
 * DataDinoLookAndFeel.java
 *
 * Created on March 5, 2003, 9:21 PM
 */

package com.datadino.themes;

import java.util.*;

import javax.swing.*;

import com.dnsalias.java.sqlclient.util.*;

import com.incors.plaf.kunststoff.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class DataDinoLookAndFeel extends KunststoffLookAndFeel
{
    private static boolean isInstalled = false;
    
    /** Creates new DataDinoLookAndFeel */
    public DataDinoLookAndFeel() 
    {
        if (!isInstalled) 
        {
            UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo("DataDino", "com.datadino.themes.LookAndFeel"));
            isInstalled = true;
        }
    }

    public String getID() 
    {
      return "DataDino";
    }

    public String getName() 
    {
      return "DataDino";
    }
    
    public String getDescription() 
    {
        return "Look and Feel giving a plastic effect. Extended from Kunststoff Look and Feel.";
    }
    
    protected void initClassDefaults(UIDefaults table) 
    {
        super.initClassDefaults(table);
        
        //table.put("ButtonUI", "com.datadino.themes.DataDinoButtonUI");
        table.put("MenuBarUI", "com.datadino.themes.DataDinoMenuBarUI");
        table.put("MenuUI", "javax.swing.plaf.basic.BasicMenuUI");
        table.put("ToolBarUI", "com.datadino.themes.DataDinoToolBarUI");
        table.put("ToolBarSeparatorUI", "com.datadino.themes.ToolBarSeparatorUI");
        
        //table.put("Button.border", new com.datadino.themes.borders.ButtonBorder());
        //table.put("Button.textIconGap", new Integer(5));
        
        //table.put("com.datadino.themes.borders.ButtonBorder", );
        //UIManager.put("Button.border", new com.datadino.themes.borders.ButtonBorder());
        //UIManager.put("Button.textIconGap", new Integer(5));
    }
}
