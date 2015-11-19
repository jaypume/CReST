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
package builder.map.canvas;

import java.awt.datatransfer.*;
import java.awt.dnd.*;

/**
 * Governs the DnD of components onto the Canvas. Is working, but probably lacks
 * comments in appropriate places and is modified from and on-line example, so
 * probably contains bad programming practises.
 */
public class CanvasTransferHandler implements DragGestureListener,
		DragSourceListener, DropTargetListener, Transferable
{

	// Looks very cumbersome, borrowed from an online example.
	// TODO: See if this is really necessary.
	static final DataFlavor[] supportedFlavors =
	{ null };
	static
	{
		try
		{
			supportedFlavors[0] = new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	Object mObject;

	// Transferable methods.
	public Object getTransferData(DataFlavor flavor)
	{
		if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
		{
			return mObject;
		} else
		{
			return null;
		}
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		return supportedFlavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType);
	}

	// DropSourceListener method.
	public void dragOver(DragSourceDragEvent ev)
	{
		mObject = ev.getSource();
	}

	// DragGestureListener method.
	public void dragGestureRecognized(DragGestureEvent ev)
	{
		ev.startDrag(null, this, this);
	}

	// DropTargetListener methods.
	public void dragOver(DropTargetDragEvent ev)
	{
		dropTargetDrag(ev);
	}

	public void dropActionChanged(DropTargetDragEvent ev)
	{
		dropTargetDrag(ev);
	}

	void dropTargetDrag(DropTargetDragEvent ev)
	{
		// Accept only JComponents being dragged about.
		if (ev.isDataFlavorSupported(supportedFlavors[0]))
			ev.acceptDrag(ev.getDropAction());
		else
			ev.rejectDrag();
	}

	/**
	 * Adds appropriate objects to the Canvas.
	 */
	public void drop(DropTargetDropEvent ev)
	{
		ev.acceptDrop(ev.getDropAction());

		Canvas canvas = (Canvas) ((DropTarget) ev.getSource()).getComponent();
		try
		{
			mObject = ev.getTransferable().getTransferData(supportedFlavors[0]);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		DraggablePanel co = (DraggablePanel) ((DragSourceContext) mObject)
				.getComponent();
		co.droppedOnCanvas(canvas, ev.getLocation().x, ev.getLocation().y);
		ev.dropComplete(true);
	}

	// Unused methods required by dnd templates

	// DropSourceListener
	public void dragDropEnd(DragSourceDropEvent ev)
	{
	}

	public void dragEnter(DragSourceDragEvent ev)
	{
	}

	public void dragExit(DragSourceEvent ev)
	{
	}

	public void dropActionChanged(DragSourceDragEvent ev)
	{
	}

	// DropTargetListener
	public void dragEnter(DropTargetDragEvent ev)
	{
	}

	public void dragExit(DropTargetEvent ev)
	{
	}
}
