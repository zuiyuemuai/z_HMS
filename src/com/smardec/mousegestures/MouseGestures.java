// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MouseGestures.java

package com.smardec.mousegestures;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

// Referenced classes of package com.smardec.mousegestures:
//			MouseGesturesRecognizer, MouseGesturesListener

public class MouseGestures
{

	private AWTEventListener mouseGesturesEventListener;
	private MouseGesturesRecognizer mouseGesturesRecognizer;
	private Vector listeners;
	private int mouseButton;

	public MouseGestures()
	{
		mouseGesturesEventListener = null;
		mouseGesturesRecognizer = new MouseGesturesRecognizer(this);
		listeners = new Vector();
		mouseButton = 4;
	}

	public void start()
	{
		if (mouseGesturesEventListener == null)
			mouseGesturesEventListener = new AWTEventListener() {

				public void eventDispatched(AWTEvent event)
				{
					if (event instanceof MouseEvent)
					{
						MouseEvent mouseEvent = (MouseEvent)event;
						if ((mouseEvent.getModifiers() & mouseButton) == mouseButton)
							mouseGesturesRecognizer.processMouseEvent(mouseEvent);
						if ((mouseEvent.getID() == 502 || mouseEvent.getID() == 500) && (mouseEvent.getModifiers() & mouseButton) == mouseButton)
							if (mouseGesturesRecognizer.isGestureRecognized())
							{
								mouseEvent.consume();
								String gesture = mouseGesturesRecognizer.getGesture();
								mouseGesturesRecognizer.clearTemporaryInfo();
								fireProcessMouseGesture(gesture);
							} else
							{
								mouseGesturesRecognizer.clearTemporaryInfo();
							}
					}
				}

			};
		Toolkit.getDefaultToolkit().addAWTEventListener(mouseGesturesEventListener, 48L);
	}

	public void stop()
	{
		if (mouseGesturesEventListener != null)
			Toolkit.getDefaultToolkit().removeAWTEventListener(mouseGesturesEventListener);
	}

	public int getGridSize()
	{
		return mouseGesturesRecognizer.getGridSize();
	}

	public void setGridSize(int gridSize)
	{
		mouseGesturesRecognizer.setGridSize(gridSize);
	}

	public int getMouseButton()
	{
		return mouseButton;
	}

	public void setMouseButton(int mouseButton)
	{
		this.mouseButton = mouseButton;
	}

	public void addMouseGesturesListener(MouseGesturesListener listener)
	{
		if (listener == null)
		{
			return;
		} else
		{
			listeners.add(listener);
			return;
		}
	}

	public void removeMouseGesturesListener(MouseGesturesListener listener)
	{
		if (listener == null)
		{
			return;
		} else
		{
			listeners.remove(listener);
			return;
		}
	}

	private void fireProcessMouseGesture(String gesture)
	{
		for (int i = 0; i < listeners.size(); i++)
			((MouseGesturesListener)listeners.get(i)).processGesture(gesture);

	}

	void fireGestureMovementRecognized(String gesture)
	{
		for (int i = 0; i < listeners.size(); i++)
			((MouseGesturesListener)listeners.get(i)).gestureMovementRecognized(gesture);

	}



}
