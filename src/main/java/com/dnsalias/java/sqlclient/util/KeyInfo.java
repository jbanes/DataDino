/*
 * KeyInfo.java
 *
 * Created on June 6, 2002, 4:23 PM
 */

package com.dnsalias.java.sqlclient.util;

import java.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class KeyInfo
{
    private static final char[] translation = {'F', 'L', 'Z', 'A', 'G', 'X', 'B', 'Q', 'Y', 'M'};

    private static final int POT1   = 0;
    private static final int MONTH1 = 1;
    private static final int SUM1   = 2;
    private static final int MONTH2 = 3;
    private static final int DAY1   = 4;
    private static final int SUM2   = 5;
    private static final int DAY2   = 6;
    private static final int SUM3   = 7;
    private static final int YEAR1  = 8;
    private static final int SUM4   = 9;
    private static final int YEAR2  = 10;
    private static final int POT2   = 11;

    private static final int PE = 27;
    private static final int EP = 16;

    public String key;

    public Date date;
    public boolean trial;
    public int checksum;

    public KeyInfo(String key)
    {
        this.key = key.toUpperCase();

        if(key.length() < 12) throw new IllegalArgumentException("Key is too short!");
        if(key.length() > 12) throw new IllegalArgumentException("Key is too long!");

        extractDate();
        extractTrialEdition();
        extractChecksum();
    }

    private void extractDate()
    {
        int month = translateCharacter(key.charAt(MONTH1))*10 + translateCharacter(key.charAt(MONTH2));
        int day = translateCharacter(key.charAt(DAY1))*10 + translateCharacter(key.charAt(DAY2));
        int year = translateCharacter(key.charAt(YEAR1))*10 + translateCharacter(key.charAt(YEAR2));

        Date date = new Date();

        if(month < 1 || month > 12) throw new IllegalArgumentException("Illegal date string");
        if(day < 1 || day > 31) throw new IllegalArgumentException("Illegal date string");
        if(year < 0) throw new IllegalArgumentException("Illegal date string");

        date.setDate(1);

        date.setYear(((date.getYear()+1900)/100)*100+year-1900);
        date.setMonth(month-1);
        date.setDate(day);

        this.date = date;
    }

    private void extractTrialEdition()
    {
        char pot1 = key.charAt(POT1);
        char pot2 = key.charAt(POT2);

        boolean found = false;

        if(pot1 == 'P' && pot2 == 'E')
        {
            found = true;
            trial = false;
        }

        if(pot1 == 'E' && pot2 == 'P')
        {
            found = true;
            trial = true;
        }   

        if(!found) throw new IllegalArgumentException("Invalid key type");
    }

    private void extractChecksum() throws NumberFormatException
    {
        int[] sums = {
            Integer.parseInt(key.charAt(SUM1)+""), 
            Integer.parseInt(key.charAt(SUM2)+""),
            Integer.parseInt(key.charAt(SUM3)+""),
            Integer.parseInt(key.charAt(SUM4)+"")};

        int sum = 0;

        for(int i=0; i<sums.length; i++) sum += sums[i];

        if(trial && sum != EP) throw new IllegalArgumentException("Invalid checksum");
        if(!trial && sum != PE) throw new IllegalArgumentException("Invalid checksum");

        this.checksum = sum;
    }

    private int translateCharacter(char c) throws IllegalArgumentException
    {
        int t = -1;

        for(int i=0; i<translation.length; i++)
        {
            if(c == translation[i]) t = i;
        }

        if(t < 0) throw new IllegalArgumentException("Invalid date char");

        return t;
    }
}
