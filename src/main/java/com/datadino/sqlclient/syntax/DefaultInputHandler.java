package com.datadino.sqlclient.syntax;

/*
 * DefaultInputHandler.java - Default implementation of an input handler
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.undo.*;

import com.dnsalias.java.sqlclient.util.*;

/**
 * The default input handler. It maps sequences of keystrokes into actions
 * and inserts key typed events into the text area.
 * @author Slava Pestov
 * @version $Id: DefaultInputHandler.java,v 1.4 2003/10/11 09:20:45 jbanes Exp $
 */
public class DefaultInputHandler extends InputHandler
{
    public static ActionListener PASTE = new paste();
    public static ActionListener COPY = new copy();
    public static ActionListener CUT = new cut();
    public static ActionListener SELECT_ALL = new selectAll();
    public static ActionListener UNDO = new undo();
    public static ActionListener REDO = new redo();
    
	/**
	 * Creates a new input handler with no key bindings defined.
	 */
	public DefaultInputHandler()
	{
		bindings = currentBindings = new Hashtable();
	}

	/**
	 * Sets up the default key bindings.
	 */
	public void addDefaultKeyBindings()
	{
            String meta = "C";
            
            if(ApplicationSettings.getInstance().isMacOSX()) meta = "M";
            
            addKeyBinding("BACK_SPACE",BACKSPACE);
            addKeyBinding(meta+"+BACK_SPACE",BACKSPACE_WORD);
            addKeyBinding("DELETE",DELETE);
            addKeyBinding(meta+"+DELETE",DELETE_WORD);

            addKeyBinding("ENTER",INSERT_BREAK);
            addKeyBinding("TAB",INSERT_TAB);
            addKeyBinding(meta+"+D",REMOVE_TAB);
            addKeyBinding("S+TAB",REMOVE_TAB);

            addKeyBinding("INSERT",OVERWRITE);
            addKeyBinding(meta+"+\\",TOGGLE_RECT);

            addKeyBinding("HOME",HOME);
            addKeyBinding("END",END);
            addKeyBinding("S+HOME",SELECT_HOME);
            addKeyBinding("S+END",SELECT_END);
            addKeyBinding(meta+"+HOME",DOCUMENT_HOME);
            addKeyBinding(meta+"+END",DOCUMENT_END);
            addKeyBinding(meta+"S+HOME",SELECT_DOC_HOME);
            addKeyBinding(meta+"S+END",SELECT_DOC_END);

            addKeyBinding("PAGE_UP",PREV_PAGE);
            addKeyBinding("PAGE_DOWN",NEXT_PAGE);
            addKeyBinding("S+PAGE_UP",SELECT_PREV_PAGE);
            addKeyBinding("S+PAGE_DOWN",SELECT_NEXT_PAGE);

            addKeyBinding("LEFT",PREV_CHAR);
            addKeyBinding("S+LEFT",SELECT_PREV_CHAR);
            addKeyBinding(meta+"+LEFT",PREV_WORD);
            addKeyBinding(meta+"S+LEFT",SELECT_PREV_WORD);
            addKeyBinding("RIGHT",NEXT_CHAR);
            addKeyBinding("S+RIGHT",SELECT_NEXT_CHAR);
            addKeyBinding(meta+"+RIGHT",NEXT_WORD);
            addKeyBinding(meta+"S+RIGHT",SELECT_NEXT_WORD);
            addKeyBinding("UP",PREV_LINE);
            addKeyBinding("S+UP",SELECT_PREV_LINE);
            addKeyBinding("DOWN",NEXT_LINE);
            addKeyBinding("S+DOWN",SELECT_NEXT_LINE);

            addKeyBinding(meta+"+ENTER",REPEAT);

            addKeyBinding(meta+"+V",PASTE);
            addKeyBinding(meta+"+C",COPY);
            addKeyBinding(meta+"+X",CUT);

            addKeyBinding(meta+"+A",SELECT_ALL);
        
            addKeyBinding(meta+"+Z",UNDO);
            addKeyBinding(meta+"+Y",REDO);
	}

	/**
	 * Adds a key binding to this input handler. The key binding is
	 * a list of white space separated key strokes of the form
	 * <i>[modifiers+]key</i> where modifier is C for Control, A for Alt,
	 * or S for Shift, and key is either a character (a-z) or a field
	 * name in the KeyEvent class prefixed with VK_ (e.g., BACK_SPACE)
	 * @param keyBinding The key binding
	 * @param action The action
	 */
	public void addKeyBinding(String keyBinding, ActionListener action)
	{
	        Hashtable current = bindings;

		StringTokenizer st = new StringTokenizer(keyBinding);
		while(st.hasMoreTokens())
		{
			KeyStroke keyStroke = parseKeyStroke(st.nextToken());
			if(keyStroke == null)
				return;

			if(st.hasMoreTokens())
			{
				Object o = current.get(keyStroke);
				if(o instanceof Hashtable)
					current = (Hashtable)o;
				else
				{
					o = new Hashtable();
					current.put(keyStroke,o);
					current = (Hashtable)o;
				}
			}
			else
				current.put(keyStroke,action);
		}
	}

	/**
	 * Removes a key binding from this input handler. This is not yet
	 * implemented.
	 * @param keyBinding The key binding
	 */
	public void removeKeyBinding(String keyBinding)
	{
		throw new InternalError("Not yet implemented");
	}

	/**
	 * Removes all key bindings from this input handler.
	 */
	public void removeAllKeyBindings()
	{
		bindings.clear();
	}

	/**
	 * Returns a copy of this input handler that shares the same
	 * key bindings. Setting key bindings in the copy will also
	 * set them in the original.
	 */
	public InputHandler copy()
	{
		return new DefaultInputHandler(this);
	}

	/**
	 * Handle a key pressed event. This will look up the binding for
	 * the key stroke and execute it.
	 */
	public void keyPressed(KeyEvent evt)
	{
		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiers();
                
		if(keyCode == KeyEvent.VK_CONTROL ||
			keyCode == KeyEvent.VK_SHIFT ||
			keyCode == KeyEvent.VK_ALT ||
			keyCode == KeyEvent.VK_META)
			return;

		if((modifiers & ~KeyEvent.SHIFT_MASK) != 0
			|| evt.isActionKey()
			|| keyCode == KeyEvent.VK_BACK_SPACE
			|| keyCode == KeyEvent.VK_DELETE
			|| keyCode == KeyEvent.VK_ENTER
			|| keyCode == KeyEvent.VK_TAB
			|| keyCode == KeyEvent.VK_ESCAPE)
		{
			if(grabAction != null)
			{
				handleGrabAction(evt);
				return;
			}

			KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode,
				modifiers);
			Object o = currentBindings.get(keyStroke);
                        
			if(o == null)
			{
				// Don't beep if the user presses some
				// key we don't know about unless a
				// prefix is active. Otherwise it will
				// beep when caps lock is pressed, etc.
				if(currentBindings != bindings)
				{
					Toolkit.getDefaultToolkit().beep();
					// F10 should be passed on, but C+e F10
					// shouldn't
					repeatCount = 0;
					repeat = false;
					evt.consume();
				}
				currentBindings = bindings;
				return;
			}
			else if(o instanceof ActionListener)
			{
				currentBindings = bindings;

				executeAction(((ActionListener)o),
					evt.getSource(),null);

				evt.consume();
				return;
			}
			else if(o instanceof Hashtable)
			{
				currentBindings = (Hashtable)o;
				evt.consume();
				return;
			}
		}
	}

	/**
	 * Handle a key typed event. This inserts the key into the text area.
	 */
	public void keyTyped(KeyEvent evt)
	{
		int modifiers = evt.getModifiers();
		char c = evt.getKeyChar();
		if(c != KeyEvent.CHAR_UNDEFINED &&
			(modifiers & KeyEvent.ALT_MASK) == 0 &&
			(modifiers & KeyEvent.META_MASK) == 0)
		{
			if(c >= 0x20 && c != 0x7f)
			{
				KeyStroke keyStroke = KeyStroke.getKeyStroke(
					Character.toUpperCase(c));
				Object o = currentBindings.get(keyStroke);

				if(o instanceof Hashtable)
				{
					currentBindings = (Hashtable)o;
					return;
				}
				else if(o instanceof ActionListener)
				{
					currentBindings = bindings;
					executeAction((ActionListener)o,
						evt.getSource(),
						String.valueOf(c));
					return;
				}

				currentBindings = bindings;

				if(grabAction != null)
				{
					handleGrabAction(evt);
					return;
				}

				// 0-9 adds another 'digit' to the repeat number
				if(repeat && Character.isDigit(c))
				{
					repeatCount *= 10;
					repeatCount += (c - '0');
					return;
				}

				executeAction(INSERT_CHAR,evt.getSource(),
					String.valueOf(evt.getKeyChar()));

				repeatCount = 0;
				repeat = false;
			}
		}
	}

	/**
	 * Converts a string to a keystroke. The string should be of the
	 * form <i>modifiers</i>+<i>shortcut</i> where <i>modifiers</i>
	 * is any combination of A for Alt, C for Control, S for Shift
	 * or M for Meta, and <i>shortcut</i> is either a single character,
	 * or a keycode name from the <code>KeyEvent</code> class, without
	 * the <code>VK_</code> prefix.
	 * @param keyStroke A string description of the key stroke
	 */
	public static KeyStroke parseKeyStroke(String keyStroke)
	{
		if(keyStroke == null)
			return null;
		int modifiers = 0;
		int index = keyStroke.indexOf('+');
		if(index != -1)
		{
			for(int i = 0; i < index; i++)
			{
				switch(Character.toUpperCase(keyStroke
					.charAt(i)))
				{
				case 'A':
					modifiers |= InputEvent.ALT_MASK;
					break;
				case 'C':
					modifiers |= InputEvent.CTRL_MASK;
					break;
				case 'M':
					modifiers |= InputEvent.META_MASK;
					break;
				case 'S':
					modifiers |= InputEvent.SHIFT_MASK;
					break;
				}
			}
		}
		String key = keyStroke.substring(index + 1);
		if(key.length() == 1)
		{
			char ch = Character.toUpperCase(key.charAt(0));
			if(modifiers == 0)
				return KeyStroke.getKeyStroke(ch);
			else
				return KeyStroke.getKeyStroke(ch,modifiers);
		}
		else if(key.length() == 0)
		{
			System.err.println("Invalid key stroke: " + keyStroke);
			return null;
		}
		else
		{
			int ch;

			try
			{
				ch = KeyEvent.class.getField("VK_".concat(key))
					.getInt(null);
			}
			catch(Exception e)
			{
				System.err.println("Invalid key stroke: "
					+ keyStroke);
				return null;
			}

			return KeyStroke.getKeyStroke(ch,modifiers);
		}
	}
        
    public static class paste implements ActionListener
	{
        public void actionPerformed(ActionEvent evt)
        {
            JEditTextArea textArea = getTextArea(evt);

            if(!textArea.isEditable())
            {
                textArea.getToolkit().beep();
                return;
            }
            else
            {
                textArea.paste();
            }
        }
    }

    public static class cut implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            JEditTextArea textArea = getTextArea(evt);

            if(!textArea.isEditable()) 
            {
                textArea.getToolkit().beep();
                textArea.copy();
            }
            else textArea.cut();
        }
    }

    public static class copy implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            getTextArea(evt).copy();
        }
    }

    public static class selectAll implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            getTextArea(evt).selectAll();
        }
    }
    
    public static class undo implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            try
            {
                getTextArea(evt).undo();
            }
            catch(CannotUndoException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static class redo implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            try
            {
                getTextArea(evt).redo();
            }
            catch(CannotUndoException e)
            {
                e.printStackTrace();
            }
        }
    }

	// private members
	private Hashtable bindings;
	private Hashtable currentBindings;

	private DefaultInputHandler(DefaultInputHandler copy)
	{
		bindings = currentBindings = copy.bindings;
	}
}