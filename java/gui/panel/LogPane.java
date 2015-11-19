/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012 John Cartlidge 
 * 
 *   For a full list of contributors, refer to file CONTRIBUTORS.txt 
 *
 *   CReST was developed at the University of Bristol, UK, using 
 *   financial support from the UK's Engineering and Physical 
 *   Sciences Research Council (EPSRC) grant EP/H042644/1 entitled 
 *   "Cloud Computing for Large-Scale Complex IT Systems". Refer to
 *   <http://gow.epsrc.ac.uk/NGBOViewGrant.aspx?GrantRef=EP/H042644/1>
 * 
 *   CReST is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 *
 *   For further information, contact: 
 *
 *   Dr. John Cartlidge: john@john-cartlidge.co.uk
 *   Department of Computer Science,
 *   University of Bristol, The Merchant Venturers Building,
 *   Woodland Road, Bristol, BS8-1UB, United Kingdom.
 *
 */
package gui.panel;

import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class LogPane extends JPanel
{
	JTextArea textArea;
	JScrollPane scrollTextArea;
	
	/**
	 * Creates a scrollable text area for the log output
	 * 
	 */
	public LogPane()
	{
		this.setLayout(new BorderLayout());
		this.add(new JLabel("your log description here"), BorderLayout.NORTH);
		
		textArea = new JTextArea();
		scrollTextArea = new JScrollPane(textArea);
		textArea.setEditable(false);
		
		this.add(scrollTextArea, BorderLayout.CENTER);
	}

	/**
     * Returns the text in the log pane
     * 
     * @return The string to return
     */
    public String getText()
    {
        return textArea.getText();
    }

    /**
     * Adds text as a new line to the log pane
     * 
     * @param text Text to add
     */
    public void println(String text)
    {
        textArea.append(text + "\n");
    }

    /**
      * Adds text to the log pane, replacing existing text
      * 
      * @param text Text to add
      */
    public void setText(String text)
    {
        textArea.setText(text);
    }
}
