package com.datadino.sqlclient.syntax;

import java.awt.*;

import javax.swing.*;

public interface PainterListener
{
    public void invalidateLineRange(int firstLine, int lastLine);
}