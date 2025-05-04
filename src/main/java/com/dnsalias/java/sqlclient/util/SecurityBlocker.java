/*
 * SecurityBlocker.java
 *
 * Created on May 14, 2002, 9:13 PM
 */

package com.dnsalias.java.sqlclient.util;

import java.security.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class SecurityBlocker extends SecurityManager
{
    public void checkPermission(Permission perm)
    {
        return;
    }
    
    public void checkPermission(Permission perm, Object context)
    {
        return;
    }
}
