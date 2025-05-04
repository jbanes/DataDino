/*
 * ClassTranslator.java
 *
 * Created on May 8, 2002, 11:32 AM
 */

package com.dnsalias.java.sqlclient.util;

import java.sql.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public final class ClassTranslator 
{
    private static final int[] types = {
                            Types.ARRAY,
                            Types.BIGINT,
                            Types.BINARY,
                            Types.BIT,
                            Types.BLOB,             //5
                            Types.CHAR,
                            Types.CLOB,
                            Types.DATE,
                            Types.DECIMAL,
                            Types.DOUBLE,           //10
                            Types.FLOAT,
                            Types.INTEGER,
                            Types.JAVA_OBJECT,
                            Types.LONGVARBINARY,
                            Types.LONGVARCHAR,      //15
                            Types.NULL,
                            Types.NUMERIC,
                            Types.OTHER,
                            Types.REAL,
                            Types.REF,              //20
                            Types.SMALLINT,
                            Types.STRUCT,
                            Types.TIME,
                            Types.TIMESTAMP,
                            Types.TINYINT,          //25
                            Types.VARBINARY,
                            Types.VARCHAR};
                            
    private  static final Class[] classes = {
                                java.sql.Array.class, 
                                java.lang.Long.class,
                                java.sql.Blob.class,
                                java.lang.Boolean.class,
                                java.sql.Blob.class,        //5
                                java.lang.String.class,
                                java.sql.Clob.class,
                                java.sql.Date.class,        
                                java.math.BigDecimal.class, 
                                java.lang.Double.class,     //10
                                java.lang.Float.class,
                                java.lang.Integer.class,
                                java.lang.Object.class,
                                java.util.BitSet.class,     
                                java.lang.String.class,     //15
                                null,
                                java.math.BigDecimal.class,
                                java.lang.String.class,
                                java.lang.Float.class,      
                                java.sql.Ref.class,         //20
                                java.lang.Short.class,
                                java.sql.Struct.class,
                                java.sql.Time.class,
                                java.sql.Timestamp.class,
                                java.lang.Byte.class,       //25
                                java.sql.Blob.class,
                                java.lang.String.class};
                                
    private static final int[] object_types = {
                            Types.BIGINT,
                            Types.BINARY,
                            Types.BIT,
                            Types.BLOB, 
                            Types.CHAR,
                            Types.CLOB,
                            Types.DECIMAL,
                            Types.DOUBLE,  
                            Types.FLOAT,
                            Types.INTEGER,
                            Types.JAVA_OBJECT,
                            Types.LONGVARBINARY,
                            Types.LONGVARCHAR,  
                            Types.NULL,
                            Types.NUMERIC,
                            Types.REAL,
                            Types.REF,    
                            Types.SMALLINT,
                            Types.STRUCT,
                            Types.TINYINT,   
                            Types.VARBINARY,
                            Types.VARCHAR};
                                
    
    public static Class translateSQLType(int type)
    {
        Class ret = java.lang.String.class;
        
        for(int i=0; i<types.length; i++)
        {
            if(types[i] == type) ret = classes[i];
        }
        
        return ret;
    }

    public static Object getDefault(Class type)
    {
        if(type.getName().equals("java.lang.Boolean")) return false;
        
        return null;
    }
    
    public static boolean getAsObject(int type)
    {
        for(int i=0; i<object_types.length; i++)
        {
            if(object_types[i] == type) return true;
        }
        
        return false;
    }
}
