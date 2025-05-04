/*
 * ImportedKey.java
 *
 * Created on October 1, 2002, 8:54 PM
 */

package com.dnsalias.java.sqlclient.gdbi;

import java.sql.*;
import java.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class ImportedKey extends DBKey
{   
    private String pktable = "";
    private String updateRule = "No Action";
    private String deleteRule = "No Action";
    private String pkname = "";
    private String deferrable = "";
    
    private ArrayList pkcolumns = new ArrayList();
    
    public ImportedKey(String name)
    {
        super(name);
    }

    public String getType()
    {
        return "Imported Foreign Key";
    }
    
    public String getPrimaryKeyName()
    {
        return pkname;
    }
    
    public void setPrimaryKeyName(String pkname)
    {
        this.pkname = pkname;
    }
    
    public String getPrimaryKeyTable()
    {
        return pktable;
    }
    
    public void setPrimaryKeyTable(String pktable)
    {
        this.pktable = pktable;
    }
    
    public void addPimaryKeyColumn(String column)
    {
        pkcolumns.add(column);
    }
    
    public String[] getPrimaryKeyColumns()
    {
        String[] cols = new String[pkcolumns.size()];
        
        for(int i=0; i<cols.length; i++) cols[i] = pkcolumns.get(i).toString();
        
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
