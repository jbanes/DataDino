/*
 * KunststoffDesktopTheme.java
 *
 * Created on 17. Oktober 2001, 22:40
 */

package com.datadino.themes;

import java.awt.*;
import java.util.*;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.dnsalias.java.sqlclient.util.*;

/**
 *
 * @author  christophw
 * @version
 */
public class KunststoffDataDinoTheme extends com.incors.plaf.kunststoff.KunststoffTheme 
{
    private FontUIResource controlFont;
    private FontUIResource menuFont;
    private FontUIResource windowTitleFont;
    private FontUIResource monospacedFont;

    // primary colors
    /* Cool blue
    private final ColorUIResource primary1 = new ColorUIResource(11, 37, 107);
    //private final ColorUIResource primary3 = new ColorUIResource(112, 145, 194);
    private final ColorUIResource primary3 = new ColorUIResource(94, 174, 255);
    private final ColorUIResource primary2 = new ColorUIResource(165, 200, 239);*/
    
    /* Mint
    private final ColorUIResource primary1 = new ColorUIResource(71, 97, 120);
    private final ColorUIResource primary2 = new ColorUIResource(94, 174, 205);
    private final ColorUIResource primary3 = new ColorUIResource(159, 192, 221);*/
    
    private final ColorUIResource primary1 = new ColorUIResource(102, 98, 90);
    private final ColorUIResource primary2 = new ColorUIResource(182, 178, 170);
    private final ColorUIResource primary3 = new ColorUIResource(202, 202, 230);
    
    // secondary colors
    private final ColorUIResource secondary3 = new ColorUIResource(212, 208, 200);
    
    /**
     * Crates this Theme
     */
    public KunststoffDataDinoTheme()
    {
        String font = "Serif";
        
        if(ApplicationSettings.getInstance().isLinux()) font = "Sans Serif";
        
        menuFont = new FontUIResource(font, Font.PLAIN, 12);
        controlFont = new FontUIResource(font, Font.PLAIN, 12);
        windowTitleFont =  new FontUIResource(font, Font.BOLD, 14);
        monospacedFont = new FontUIResource("Monospaced", Font.PLAIN, 13);
    }

    public String getName()
    {
        return "DataDino";
    }

    protected ColorUIResource getPrimary1()
    {
        return primary1;
    }

    protected ColorUIResource getPrimary2()
    {
        return primary2;
    }

    protected ColorUIResource getPrimary3()
    {
        return primary3;
    }
    
    protected ColorUIResource getSecondary3()
    {
        return secondary3;
    }
    
    /**
     * The Font of Labels in many cases
     */
    public FontUIResource getControlTextFont()
    {
        return controlFont;
    }

    /**
     * The Font of Menus and MenuItems
     */
    public FontUIResource getMenuTextFont()
    {
        return menuFont;
    }

    /**
     * The Font of Nodes in JTrees
     */
    public FontUIResource getSystemTextFont()
    {
        return controlFont;
    }

    /**
     * The Font in TextFields, EditorPanes, etc.
     */
    public FontUIResource getUserTextFont()
    {
        return controlFont;
    }

    /**
     * The Font of the Title of JInternalFrames
     */
    public FontUIResource getWindowTitleFont()
    {
        return windowTitleFont;
    }

    private void dumpSettings(UIDefaults defaults)
    {
        Enumeration keys = defaults.keys();
        Object key;
        
        while(keys.hasMoreElements())
        {
            key = keys.nextElement();
            System.out.println(key+"="+defaults.get(key));
        }
    }
    
    public void addCustomEntriesToTable(UIDefaults table)
    {
        UIDefaults info = new UIDefaults();
        
        super.addCustomEntriesToTable(table);
        
        UIManager.getDefaults().put("PasswordField.font", monospacedFont);
        UIManager.getDefaults().put("TextArea.font", monospacedFont);
        UIManager.getDefaults().put("TextPane.font", monospacedFont);
        UIManager.getDefaults().put("EditorPane.font", monospacedFont);
    }
}
