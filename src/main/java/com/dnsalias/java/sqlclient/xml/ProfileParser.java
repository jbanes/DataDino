/*
 * ProfileParser.java
 *
 */

package com.dnsalias.java.sqlclient.xml;

import java.awt.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.dnsalias.java.sqlclient.*;
import com.dnsalias.java.sqlclient.dialog.*;
import com.dnsalias.java.sqlclient.sql.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class ProfileParser extends HandlerBase implements DocumentHandler
{
    private Vector profiles = new Vector();
    private Profile profile = null;
    
    private String name = null;
    private String prefix = null;
    private String url = null;
    private String driver = null;
    private String archive = null;
    private Date updated = new Date(0);
    private AutoNumber auto = new AutoNumber();
    
    private Hashtable datatypes = new Hashtable();
    private Hashtable alternates = new Hashtable();
    private Properties props = new Properties();
     
    /** Creates new ProfileParser */
    public ProfileParser() 
    {
        try
        {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser(); //new com.sun.xml.parser.Parser();
            
            //parser.setDocumentHandler(this);
            //parser.parse(new InputSource(getClass().getResourceAsStream("/profiles.xml")));
            parser.parse(new InputSource(getClass().getResourceAsStream("/profiles.xml")), this);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            
            ErrorReport.displayError(e, new Frame());
        }
    }

    public void endDocument() throws SAXException
    {
    }
    
    public void ignorableWhitespace(char[] values, int param, int param2) throws org.xml.sax.SAXException
    {
    }
    
    public void endElement(String name) throws org.xml.sax.SAXException
    {
        if(name.equals("profile"))
        {
            profile = new Profile(this.name, prefix, url, driver, archive, updated);
            profile.autonumber = auto;
            
            profiles.add(profile);
            
            this.name = null;
            prefix = null;
            url = null;
            driver = null;
            updated = new Date(0);
            auto = new AutoNumber();
            
            Enumeration enumeration = datatypes.keys();
            
            while(enumeration.hasMoreElements())
            {
                String key = enumeration.nextElement().toString();
                profile.addDataType(key, datatypes.get(key).toString().equalsIgnoreCase("true"), (String)alternates.get(key));
            }
            
            enumeration = props.keys();
            
            while(enumeration.hasMoreElements())
            {
                String key = enumeration.nextElement().toString();
                profile.setProperty(key, props.getProperty(key));
            }
            
            datatypes = new Hashtable();
            alternates = new Hashtable();
            props = new Properties();
        }
    }
    
    public void processingInstruction(java.lang.String str, java.lang.String str1) throws org.xml.sax.SAXException
    {
    }
    
    public void startElement(String name, AttributeList attributes) throws SAXException
    {
        if(name.equals("profile"))
        {
            this.name = attributes.getValue("name");
        }
        
        if(name.equals("driver"))
        {
            driver = attributes.getValue("name");
            prefix = attributes.getValue("prefix");
            url = attributes.getValue("url");
            archive = attributes.getValue("archive");
            
            try
            {
                if(attributes.getValue("updated") != null)
                    updated = DateFormat.getDateInstance(DateFormat.SHORT).parse(attributes.getValue("updated"));
            }
            catch(ParseException e)
            {
                e.printStackTrace();
            }
        }
        
        if(name.equals("datatype"))
        {
            datatypes.put(attributes.getValue("name"), attributes.getValue("resizable"));
            
            if(attributes.getValue("alternate") != null)
                alternates.put(attributes.getValue("name"), attributes.getValue("alternate"));
        }
        
        if(name.equals("autonumber"))
        {
            if(attributes.getValue("sql") != null)
                auto.setColumnSQL(attributes.getValue("sql"));
            
            if(attributes.getValue("datatype") != null)
                auto.setDataType(attributes.getValue("datatype"));
        }
        
        if(name.equals("property"))
        {
            String key = attributes.getValue("key");
            String value = attributes.getValue("value");
            
            props.setProperty(key, value);
        }
    }
    
    public void setDocumentLocator(Locator locator)
    {
    }
    
    public void characters(char[] values, int param, int param2) throws org.xml.sax.SAXException
    {
    }
    
    public void startDocument() throws org.xml.sax.SAXException
    {
    }
    
    public Profile[] getProfiles()
    {
        Profile[] profiles = new Profile[this.profiles.size()];
        
        for(int i=0; i<profiles.length; i++) profiles[i] = (Profile)this.profiles.elementAt(i);
        
        return profiles;
    }
    
    public Profile getProfile(String name)
    {
        for(int i=0; i<profiles.size(); i++)
        {
            if(((Profile)profiles.elementAt(i)).name.equals(name)) return (Profile)profiles.elementAt(i);
        }
        
        return null;
    }
}

