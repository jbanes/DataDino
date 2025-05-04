/*
 * LengthEditor.java
 *
 * Created on November 19, 2001, 12:20 PM
 */

package com.dnsalias.java.sqlclient.validation;


import java.awt.*;
import java.text.*;
import java.util.*;

import javax.swing.*; 
import javax.swing.text.*; 

/**
 *
 * @author  jbanes
 * @version 
 */
public class LicenseKeyEditor extends JTextField 
{
    private Toolkit toolkit;

    public LicenseKeyEditor() 
    {
        super();
        toolkit = getToolkit();
    }
    
    public LicenseKeyEditor(int columns) 
    {
        this("", columns);
    }
    
    public LicenseKeyEditor(String value, int columns) 
    {
        super(columns);
        toolkit = getToolkit();
        setValue(value);
    }

    public String getValue() 
    {
        return getText();
    }

    public void setValue(String value) 
    {
        setText(value);
    }

    protected Document createDefaultModel() 
    {
        return new LicenseKeyEditorDocument();
    }

    protected class LicenseKeyEditorDocument extends PlainDocument 
    {
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException 
        {
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;

            for (int i = 0; i < result.length; i++) 
            {
                if(Character.isLetterOrDigit(source[i]) && offs+i < 4) 
                {
                    result[j++] = source[i];
                }
                else if(!Character.isLetterOrDigit(source[i]))
                {
                    toolkit.beep();
                }
            }
            
            if(offs > 2 && getNextFocusableComponent() != null)
            {
                getNextFocusableComponent().requestFocus();
                if(offs+str.length() > 4) ((JTextField)getNextFocusableComponent()).setText(str.substring(4-offs));
            }
            
            super.insertString(offs, new String(result, 0, j), a);
        }
    }
}

