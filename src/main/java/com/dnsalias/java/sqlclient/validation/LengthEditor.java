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
public class LengthEditor extends JTextField 
{
    private Toolkit toolkit;

    public LengthEditor() 
    {
        super();
        toolkit = getToolkit();
        this.setHorizontalAlignment(this.RIGHT);
    }
    
    public LengthEditor(int columns) 
    {
        this("", columns);
    }
    
    public LengthEditor(String value, int columns) 
    {
        super(columns);
        toolkit = getToolkit();
        setValue(value);
        this.setAlignmentX(this.RIGHT_ALIGNMENT);
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
        return new LengthEditorDocument();
    }

    protected class LengthEditorDocument extends PlainDocument 
    {
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException 
        {
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;

            for (int i = 0; i < result.length; i++) 
            {
                if (Character.isDigit(source[i]) || source[i] == ',') 
                {
                    result[j++] = source[i];
                }
                else 
                {
                    toolkit.beep();
                    System.err.println("insertString: " + source[i]);
                }
            }
            super.insertString(offs, new String(result, 0, j), a);
        }
    }
}

