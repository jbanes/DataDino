/*
 * Index.java
 *
 * Created on November 3, 2002, 10:51 PM
 */

package com.dnsalias.java.sqlclient.gdbi;

import java.awt.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class Index 
{
    private static ImageIcon statistic = new ImageIcon(Index.class.getResource("/images/icons/statistic.png"));
    private static ImageIcon hash = new ImageIcon(Index.class.getResource("/images/icons/hash.png"));
    private static ImageIcon cluster = new ImageIcon(Index.class.getResource("/images/icons/cluster.png"));
    private static ImageIcon other = new ImageIcon(Index.class.getResource("/images/icons/question.png"));
    
    private String name;
    private String type = "";
    private boolean unique = false;
    private String sort = "";
    private int cardinality = 0;
    private int pages = 0;
    private String filter = "None";
    
    private ArrayList columns = new ArrayList();
    
    public Index(String name) 
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    
    public Icon getIcon()
    {
        if(type.equals("Statistic")) return statistic;
        else if(type.equals("Clustered")) return cluster;
        else if(type.equals("Hashed")) return hash;
        else return other;
    }
    
    public void setType(int type)
    {
        if(type == DatabaseMetaData.tableIndexStatistic) this.type = "Statistic";
        else if(type == DatabaseMetaData.tableIndexClustered) this.type = "Clustered";
        else if(type == DatabaseMetaData.tableIndexHashed) this.type = "Hashed";
        else if(type == DatabaseMetaData.tableIndexOther) this.type = "Other";
        else this.type = "Unknown";
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setPages(int pages)
    {
        this.pages = pages;
    }
    
    public int getPages()
    {
        return pages;
    }
    
    public void setFilter(String filter)
    {
        if(filter == null) this.filter = "None";
        else this.filter = filter;
    }
    
    public String getFilter()
    {
        return filter;
    }
    
    public void setCardinality(int cardinality)
    {
        this.cardinality = cardinality;
    }
    
    public int getCardinality()
    {
        return cardinality;
    }
    
    public void setSorting(String sorting)
    {
        if(sorting == null || sorting.length() < 1) this.sort = "None";
        else if(sorting.equalsIgnoreCase("A") || sorting.equalsIgnoreCase("ASC")) this.sort = "Ascending";
        else if(sorting.equalsIgnoreCase("D") || sorting.equalsIgnoreCase("DESC")) this.sort = "Descending";
        else this.sort = "Unknown";
    }
    
    public String getSorting()
    {
        return sort;
    }
    
    public void setUnique(boolean unique)
    {
        this.unique = unique;
    }
    
    public boolean getUnique()
    {
        return unique;
    }
    
    public void addColumn(String column)
    {
        columns.add(column);
    }
    
    public String[] getColumns()
    {
        String[] cols = new String[columns.size()];
        
        for(int i=0; i<cols.length; i++) cols[i] = columns.get(i).toString();
        
        return cols;
    }
}
