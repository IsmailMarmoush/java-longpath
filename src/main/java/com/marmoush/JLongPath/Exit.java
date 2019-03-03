/*
 * Copyright 2011 Ismail Marmoush
 * 
 * This file is part of JLongPath.
 * 
 * JLongPath is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License Version 3 as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * 
 * JLongPath is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * JLongPath. If not, see http://www.gnu.org/licenses/.
 * 
 * For More Information Please Visit http://marmoush.com
 */
package com.marmoush.JLongPath;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class Exit.
 */
public class Exit extends WindowAdapter
{

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent event)
    {
	System.exit(0);
    }
}
