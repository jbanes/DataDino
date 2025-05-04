/*
 * ExportedKey.java
 *
 * Created on October 1, 2002, 8:55 PM
 */

package com.dnsalias.java.sqlclient.gdbi;

import java.sql.*;
import java.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class ExportedKey extends DBKey
{
    private String fktable = "";
    private String updateRule = "No Action";
    private String deleteRule = "No Action";
    private String fkname = "";
    private String deferrable = "";
    
    private ArrayList fkcolumns = new ArrayList();
    
    public ExportedKey(String name)
    {
        super(name);
    }

    public String getType()
    {
        return "Exported Foreign Key";
    }
    
    public String getForeignKeyName()
    {
        return fkname;
    }
    
    public void setForeignKeyName(String fkname)
    {
        this.fkname = fkname;
    }
    
    public String getForeignKeyTable()
    {
        return fktable;
    }
    
    public void setForeignKeyTable(String fktable)
    {
        this.fktable = fktable;
    }
    
    public void addForeignKeyColumn(String column)
    {
        fkcolumns.add(column);
    }
    
    public String[] getForeignKeyColumns()
    {
        String[] cols = new String[fkcolumns.size()];
        
        for(int i=0; i<cols.length; i++) cols[i] = fkcolumns.get(i).toString();
        
        return cols;
    }
    
    public String getUpdateRule()
    {
        return updateRule;
    }
    
    public void setDeleteRule(int rule)
    {
        if(rule == DatabaseMetaData.importedKeyNoAction) deleteRule = "No Action";
        else if(rule == DatabaseMetaData.importedKeyCascade) deleteRule = "Cascade";
        else if(rule == DatabaseMetaData.importedKeySetNull) deleteRule = "Set Null";
        else if(rule == DatabaseMetaData.importedKeyRestrict) deleteRule = "Restrict";
        else if(rule == DatabaseMetaData.importedKeySetDefault) deleteRule = "Set Default";
        else deleteRule = "Unknown";
    }
    
    public String getDeleteRule()
    {
        return deleteRule;
    }
    
    public void setUpdateRule(int rule)
    {
        if(rule == DatabaseMetaData.importedKeyNoAction) updateRule = "No Action";
        else if(rule == DatabaseMetaData.importedKeyCascade) updateRule = "Cascade";
        else if(rule == DatabaseMetaData.importedKeySetNull) updateRule = "Set Null";
        else if(rule == DatabaseMetaData.importedKeyRestrict) updateRule = "Restrict";
        else if(rule == DatabaseMetaData.importedKeySetDefault) updateRule = "Set Default";
        else updateRule = "Unknown";
    }
    
    public String getDeferrable()
    {
        return deferrable;
    }
    
    public void setDeferrable(int rule)
    {
        if(rule == DatabaseMetaData.importedKeyInitiallyDeferred) deferrable = "Initially Deferred";
        else if(rule == DatabaseMetaData.importedKeyInitiallyImmediate) deferrable = "Initially Immediate";
        else if(rule == DatabaseMetaData.importedKeyNotDeferrable) deferrable = "Not Deferrable";
        else deferrable = "Unknown";
    }
}
