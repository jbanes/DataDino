/*
 * Routing.java
 *
 * Created on April 28, 2004, 11:12 PM
 */

package com.datadino.graphing;

import java.util.*;

import org.jgraph.graph.*;

/**
 *
 * @author  jbanes
 */
public class Routing implements Edge.Routing
{
    
    /** Creates a new instance of Routing */
    public Routing() 
    {
        
    }
    
    public void route(EdgeView edgeView, List list) 
    {
        list.add(edgeView.getPoint(1));
        list.add(edgeView.getPoint(0));
    }
}
