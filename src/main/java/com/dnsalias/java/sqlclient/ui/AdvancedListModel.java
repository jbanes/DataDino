/*
 * TableListModel.java
 *
 * Created on June 28, 2002, 9:21 PM
 */

package com.dnsalias.java.sqlclient.ui;

import java.util.*;

import javax.swing.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class AdvancedListModel extends AbstractListModel implements ComboBoxModel
{
    private ArrayList data = new ArrayList();
    private Object selected = null;
    
    public AdvancedListModel() 
    {
    }

    public void add(Object element)
    {
        data.add(element);
        
        fireIntervalAdded(this, Math.max(0, data.size()-2), data.size()-1);
    }
    
    public void add(Collection c)
    {
        data.addAll(c);
        
        System.out.println(data.size());
        
        fireIntervalAdded(this, data.size()-c.size(), data.size()-1);
    }
    
    public int getSize()
    {
        return data.size();
    }
    
    public Object getElementAt(int index)
    {
        return data.get(index);
    }
    
    public Object getSelectedItem()
    {
        return selected;
    }
    
    public void setSelectedItem(Object obj)
    {
        this.selected = obj;
    }
}
